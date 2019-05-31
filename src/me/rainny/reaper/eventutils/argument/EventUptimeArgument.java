package me.rainny.reaper.eventutils.argument;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.eventutils.EventTimer;
import me.rainny.reaper.factionutils.type.EventFaction;
import me.rainny.reaper.listener.DateTimeFormats;

/**
 * A {@link CommandArgument} argument used for checking the uptime of current event.
 */
public class EventUptimeArgument extends CommandArgument {

    private final HCF plugin;

    public EventUptimeArgument(HCF plugin) {
        super("uptime", "Check the uptime of an event");
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        EventTimer eventTimer = plugin.getTimerManager().getEventTimer();

        if (eventTimer.getRemaining() <= 0L) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }

        EventFaction eventFaction = eventTimer.getEventFaction();
        sender.sendMessage(ChatColor.GRAY + "Up-time of " + eventTimer.getName() + " timer"
                + (eventFaction == null ? "" : ": " + ChatColor.BLUE + '(' + eventFaction.getDisplayName(sender) + ChatColor.BLUE + ')') + ChatColor.GRAY + " is " + ChatColor.GRAY
                + DurationFormatUtils.formatDurationWords(eventTimer.getUptime(), true, true) + ChatColor.GRAY + ", started at " + ChatColor.GOLD
                + DateTimeFormats.HR_MIN_AMPM_TIMEZONE.format(eventTimer.getStartStamp()) + ChatColor.GRAY + '.');

        return true;
    }
}
