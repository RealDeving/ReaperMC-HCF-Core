package me.rainny.reaper.eventutils.tracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.utils.DurationFormatter;

import me.rainny.reaper.HCF;
import me.rainny.reaper.eventutils.CaptureZone;
import me.rainny.reaper.eventutils.EventTimer;
import me.rainny.reaper.eventutils.EventType;
import me.rainny.reaper.factionutils.type.EventFaction;
import me.rainny.reaper.factionutils.type.KothFaction;

import java.util.concurrent.TimeUnit;

public class KothTracker implements EventTracker {

    /**
     * Minimum time the KOTH has to be controlled before this tracker will announce when control has been lost.
     */
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);

    public static final long DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);

    private final HCF plugin;

    public KothTracker(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }

    @SuppressWarnings("static-access")
	@Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction) {
        CaptureZone captureZone = ((KothFaction) eventFaction).getCaptureZone();
        captureZone.updateScoreboardRemaining();
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis <= 0L) { // has been captured.
            plugin.getTimerManager().getEventTimer().handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }

        if (remainingMillis == captureZone.getDefaultCaptureMillis())
            return;

        int remainingSeconds = (int) (remainingMillis / 1000L);
        if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage("§9§lKOTH §8» §7§cSomeone is controlling §4%koth% §c(%remaining%)".replace("%koth%", eventFaction.getName()).replace("%remaining%", DurationFormatter.getRemaining(TimeUnit.SECONDS.toMillis(remainingSeconds), true)));
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer) {
    	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8&m---------------------------------"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8\u2588&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588&8\u2588"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&4\u2588&7\u2588&4\u2588&7\u2588&4\u2588&7\u2588&4\u2588&7\u2588"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&4\u2588\u2588\u2588\u2588\u2588\u2588\u2588&7\u2588       &a&l" + eventFaction.getName()));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&4\u2588&c\u2588&4\u2588&c\u2588&4\u2588&c\u2588&4\u2588&7\u2588 &7has been started. &a(" + DurationFormatter.getRemaining(eventTimer.getRemaining(), true) + ")"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588&4\u2588\u2588\u2588\u2588\u2588\u2588\u2588&7\u2588"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588\u2588\u2588&7\u2588\u2588\u2588&7\u2588\u2588\u2588"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7\u2588\u2588\u2588\u2588&7\u2588&7\u2588\u2588\u2588\u2588"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8\u2588&7\u2588\u2588\u2588&7\u2588&7\u2588\u2588\u2588&8\u2588"));
  	  	Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&8&m---------------------------------"));
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone) {
        player.sendMessage("§cYou are now controlling §4" + captureZone.getName());
        return true;
    }

    @Override
    public void onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are no longer controlling &4" + eventFaction.getName()));

        // Only broadcast if the KOTH has been controlled for at least 25 seconds to prevent spam.
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&9&lKOTH &8» " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.GRAY + " has been knocked!"
                    + " (" + captureZone.getScoreboardRemaining() + ")"));
        }
    }

    @Override
    public void stopTiming() {

    }
}
