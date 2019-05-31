package me.rainny.reaper.deathban;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.doctordark.utils.DurationFormatter;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionUser;
import me.rainny.reaper.listener.DelayedMessageRunnable;

public class DeathbanListener implements Listener {
    private static final long RESPAWN_KICK_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(10L);
    private static final long RESPAWN_KICK_DELAY_TICKS = RESPAWN_KICK_DELAY_MILLIS / 50L;
    private static final long LIFE_USE_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30L);
    private static final String LIFE_USE_DELAY_WORDS = org.apache.commons.lang3.time.DurationFormatUtils.formatDurationWords(LIFE_USE_DELAY_MILLIS, true, true);

    @SuppressWarnings("unused")
    private static final String DEATH_BAN_BYPASS_PERMISSION = "hcf.deathban.bypass";
    private final TObjectIntMap < UUID > respawnTickTasks = new net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap<UUID>();
    private final TObjectLongMap < UUID > lastAttemptedJoinMap = new TObjectLongHashMap < UUID > ();
    private final HCF plugin;

    public DeathbanListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("hcf.deathban.bypass")) {
            return;
        }

        FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
        Deathban deathban = user.getDeathban();
        if ((deathban == null) || (!deathban.isActive())) {
            return;
        }

        if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.\nVisit www.reapermc.org to view SOTW info.");
            return;
        }

        UUID uuid = player.getUniqueId();
        int lives = this.plugin.getDeathbanManager().getLives(uuid);

        String formattedRemaining = DurationFormatter.getRemaining(deathban.getRemaining(), true, false);

        if (lives <= 0) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You are currently deathbanned for " + ChatColor.BOLD + formattedRemaining);

            return;
        }

        long millis = System.currentTimeMillis();
        long lastAttemptedJoinMillis = this.lastAttemptedJoinMap.get(uuid);


        if ((lastAttemptedJoinMillis != this.lastAttemptedJoinMap.getNoEntryValue()) && (lastAttemptedJoinMillis - millis < LIFE_USE_DELAY_MILLIS)) {
            this.lastAttemptedJoinMap.remove(uuid);
            user.removeDeathban();
            lives = this.plugin.getDeathbanManager().takeLives(uuid, 1);

            event.setResult(PlayerLoginEvent.Result.ALLOWED);
            new DelayedMessageRunnable(player, ChatColor.GRAY + "You have used a life for entry. You now have " + ChatColor.WHITE + lives + ChatColor.GRAY + " lives.").runTask(this.plugin);

            return;
        }


        this.lastAttemptedJoinMap.put(uuid, millis + LIFE_USE_DELAY_MILLIS);

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You are currently Deathbanned for Duration: " + ChatColor.WHITE + formattedRemaining + "\n" +
            ChatColor.RED + "You currently have " + ChatColor.WHITE + (lives <= 0 ? "no" : Integer.valueOf(lives)) + " lives." + ChatColor.RED + "You may use a life by reconnecting within " + ChatColor.WHITE +
            LIFE_USE_DELAY_WORDS + ChatColor.RED + ".");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Deathban deathban = this.plugin.getDeathbanManager().applyDeathBan(player, event.getDeathMessage());
        long remaining = deathban.getRemaining();
        if ((remaining <= 0L) || (player.hasPermission("hcf.deathban.bypass"))) {
            return;
        }

        if ((RESPAWN_KICK_DELAY_MILLIS <= 0L) || (remaining < RESPAWN_KICK_DELAY_MILLIS)) {
            handleKick(player, deathban);
            return;
        }


        this.respawnTickTasks.put(player.getUniqueId(), new BukkitRunnable() {
            public void run() {
                DeathbanListener.this.handleKick(player, deathban);
            }
        }.runTaskLater(this.plugin, RESPAWN_KICK_DELAY_TICKS).getTaskId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRequestRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
        Deathban deathban = user.getDeathban();
        if ((deathban != null) && (deathban.getRemaining() > 0L)) {
            if (player.hasPermission("hcf.deathban.bypass")) {
                cancelRespawnKickTask(player);
                user.removeDeathban();
                new DelayedMessageRunnable(player, ChatColor.RED + "Bypass access granted.").runTask(this.plugin);

                return;
            }



            handleKick(player, deathban);
        }
        


    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        cancelRespawnKickTask(event.getPlayer());
    }

    private void cancelRespawnKickTask(Player player) {
        int taskId = this.respawnTickTasks.remove(player.getUniqueId());
        if (taskId != this.respawnTickTasks.getNoEntryValue()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private void handleKick(Player player, Deathban deathban) {
        if (this.plugin.getEotwHandler().isEndOfTheWorld()) {
            player.kickPlayer(ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.\nVisit www.reapermc.org for SOTW info!");
        } else {
            player.kickPlayer(ChatColor.RED + "You are currently deathbanned for " + ChatColor.BOLD + DurationFormatter.getRemaining(deathban.getRemaining(), true, false) + "\n" +
                ChatColor.RED + "Reason: " + ChatColor.GRAY + deathban.getReason());
        }
    }
}