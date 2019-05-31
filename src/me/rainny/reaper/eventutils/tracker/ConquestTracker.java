package me.rainny.reaper.eventutils.tracker;

import com.doctordark.utils.DurationFormatter;
import com.doctordark.utils.compat.com.google.common.collect.GuavaCompat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;

import me.rainny.reaper.HCF;
import me.rainny.reaper.eventutils.CaptureZone;
import me.rainny.reaper.eventutils.EventTimer;
import me.rainny.reaper.eventutils.EventType;
import me.rainny.reaper.factionutils.event.FactionRemoveEvent;
import me.rainny.reaper.factionutils.type.ConquestFaction;
import me.rainny.reaper.factionutils.type.EventFaction;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.ymls.SettingsYML;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Tracker used for handling the Conquest points.
 */
public class ConquestTracker implements EventTracker, Listener {

    /**
     * Minimum time the KOTH has to be controlled before this tracker will announce when control has been lost.
     */
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5L);
    public static final long DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    private static final Comparator<Map.Entry<PlayerFaction, Integer>> POINTS_COMPARATOR = (e1, e2) -> e2.getValue().compareTo(e1.getValue());

    private final Map<PlayerFaction, Integer> factionPointsMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private final HCF plugin;

    public ConquestTracker(HCF plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(FactionRemoveEvent event) {
        Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            synchronized (factionPointsMap) {
                factionPointsMap.remove(faction);
            }
        }
    }

    /**
     * Gets the map containing the points for all factions.
     *
     * @return immutable copy of the faction points map
     */
    public Map<PlayerFaction, Integer> getFactionPointsMap() {
        return ImmutableMap.copyOf(factionPointsMap);
    }

    /**
     * Gets the amount of points a {@link PlayerFaction} has
     * gained for this {@link ConquestTracker}.
     *
     * @param faction the faction to get for
     * @return the new points of the {@link PlayerFaction}.
     */
    public int getPoints(PlayerFaction faction) {
        synchronized (factionPointsMap) {
            return GuavaCompat.firstNonNull(factionPointsMap.get(faction), 0);
        }
    }

    /**
     * Sets the points a {@link PlayerFaction} has gained for this {@link ConquestTracker}.
     *
     * @param faction the faction to set for
     * @param amount  the amount to set
     * @return the new points of the {@link PlayerFaction}
     */
    public int setPoints(PlayerFaction faction, int amount) {
        if (amount <= 0) return amount;

        synchronized (factionPointsMap) {
            factionPointsMap.put(faction, amount);
            List<Map.Entry<PlayerFaction, Integer>> entries = Ordering.from(POINTS_COMPARATOR).sortedCopy(factionPointsMap.entrySet());

            factionPointsMap.clear();
            for (Map.Entry<PlayerFaction, Integer> entry : entries) {
                factionPointsMap.put(entry.getKey(), entry.getValue());
            }
        }

        return amount;
    }

    /**
     * Takes points from a {@link PlayerFaction} gained from this {@link ConquestTracker}.has
     *
     * @param faction the faction to take from
     * @param amount  the amount to take
     * @return the new points of the {@link PlayerFaction}
     */
    public int takePoints(PlayerFaction faction, int amount) {
        return setPoints(faction, getPoints(faction) - amount);
    }

    /**
     * Adds points to a {@link PlayerFaction} gained from this {@link ConquestTracker}.has
     *
     * @param faction the faction to add from
     * @param amount  the amount to add
     * @return the new points of the {@link PlayerFaction}
     */
    public int addPoints(PlayerFaction faction, int amount) {
        return setPoints(faction, getPoints(faction) + amount);
    }

    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }

    @SuppressWarnings("static-access")
	@Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
        List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for (CaptureZone captureZone : captureZones) {
            captureZone.updateScoreboardRemaining();
            Player cappingPlayer = captureZone.getCappingPlayer();
            if (cappingPlayer == null) continue;

            // The capture zone has been controlled.
            long remainingMillis = captureZone.getRemainingCaptureMillis();
            if (remainingMillis <= 0L) {
                UUID uuid = cappingPlayer.getUniqueId();

                PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
                if (playerFaction != null) {
                    int newPoints = addPoints(playerFaction, 1);
                    if (newPoints < SettingsYML.CONQUEST_REQUIRED_WIN_POINTS) {
                        // Reset back to the default for this tracker.
                        captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + eventFaction.getName() + "] " +
                                ChatColor.RED + ChatColor.BOLD + playerFaction.getName() +
                                ChatColor.GOLD + " gained " + 1 + " point for capturing " + captureZone.getDisplayName() + ChatColor.GOLD + ". " +
                                ChatColor.AQUA + '(' + newPoints + '/' + SettingsYML.CONQUEST_REQUIRED_WIN_POINTS + ')');
                    } else {
                        // Clear all the points for the next Conquest event.
                        synchronized (factionPointsMap) {
                            factionPointsMap.clear();
                        }

                        plugin.getTimerManager().getEventTimer().handleWinner(cappingPlayer);
                        return;
                    }
                }
                return;
            }

            int remainingSeconds = (int) Math.round((double) remainingMillis / 1000L);
            if (remainingSeconds % 5 == 0) {
                cappingPlayer.sendMessage(ChatColor.GRAY + "[" + eventFaction.getName() + "] " + ChatColor.GOLD +
                        "Attempting to control " + ChatColor.GRAY + captureZone.getDisplayName() + ChatColor.GOLD + ". " +
                        ChatColor.GRAY + '(' + remainingSeconds + "s)");
            }
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
    	  Bukkit.broadcastMessage("&8&m---------------------------------");
    	  Bukkit.broadcastMessage("&8\u2588&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588&8\u2588");
    	  Bukkit.broadcastMessage("&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
    	  Bukkit.broadcastMessage("&7\u2588&4\u2588&7\u2588&4\u2588&7\u2588&4\u2588&7\u2588&4\u2588&7\u2588");
    	  Bukkit.broadcastMessage("&7\u2588&4\u2588\u2588\u2588\u2588\u2588\u2588\u2588&7\u2588       &a&l%koth%");
    	  Bukkit.broadcastMessage("&7\u2588&4\u2588&c\u2588&4\u2588&c\u2588&4\u2588&c\u2588&4\u2588&7\u2588 &7has been started. &a(%remaining%)");
    	  Bukkit.broadcastMessage("&7\u2588&4\u2588\u2588\u2588\u2588\u2588\u2588\u2588&7\u2588");
    	  Bukkit.broadcastMessage("&7\u2588\u2588\u2588&7\u2588\u2588\u2588&7\u2588\u2588\u2588");
    	  Bukkit.broadcastMessage("&7\u2588\u2588\u2588\u2588&7\u2588&7\u2588\u2588\u2588\u2588");
    	  Bukkit.broadcastMessage("&8\u2588&7\u2588\u2588\u2588&7\u2588&7\u2588\u2588\u2588&8\u2588");
    	  Bukkit.broadcastMessage("&8&m---------------------------------");
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        if (plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture for the Conquest Event.");
            return false;
        }

        return true;
    }

    @Override
    public void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage("&4&lKOTH &8» &7&4" + player.getName() 
            + " &chas lost control of &4"+ captureZone.getName() + " &c(" + DurationFormatter.getRemaining(captureZone.getRemainingCaptureMillis(), true) + ")");
        }
    }

    @Override
    public void stopTiming() {
        synchronized (factionPointsMap) {
            factionPointsMap.clear();
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Faction currentEventFac = plugin.getTimerManager().getEventTimer().getEventFaction();
        if (currentEventFac instanceof ConquestFaction) {
            Player player = event.getEntity();
            PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction != null && SettingsYML.CONQUEST_POINT_LOSS_PER_DEATH > 0) {
                int oldPoints = getPoints(playerFaction);
                if (oldPoints == 0) return;

                int newPoints = takePoints(playerFaction, SettingsYML.CONQUEST_POINT_LOSS_PER_DEATH);
                event.setDeathMessage(null); // for some reason if it isn't handled manually, weird colour coding happens
                Bukkit.broadcastMessage(ChatColor.GRAY + "[" + currentEventFac.getName() + "] " +
                        ChatColor.GOLD + playerFaction.getName() + " lost " +
                        SettingsYML.CONQUEST_POINT_LOSS_PER_DEATH + " points because " + player.getName() + " died." +
                        ChatColor.AQUA + " (" + newPoints + '/' + SettingsYML.CONQUEST_REQUIRED_WIN_POINTS + ')' + ChatColor.GRAY + '.');
            }
        }
    }
}
