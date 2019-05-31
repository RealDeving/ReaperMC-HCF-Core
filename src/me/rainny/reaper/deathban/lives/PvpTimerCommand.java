package me.rainny.reaper.deathban.lives;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.doctordark.utils.BukkitUtils;
import com.doctordark.utils.DurationFormatter;
import com.google.common.collect.ImmutableList;

import me.rainny.reaper.HCF;
import me.rainny.reaper.timer.type.InvincibilityTimer;

/**
 * Command used to manage the {@link InvincibilityTimer} of {@link Player}s.
 */
public class PvpTimerCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public PvpTimerCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        InvincibilityTimer pvpTimer = plugin.getTimerManager().getInvincibilityTimer();

        if (args.length < 1) {
            printUsage(sender, label, pvpTimer);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
            if (pvpTimer.getRemaining(player) <= 0L) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " is currently not active.");
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is now off.");
            pvpTimer.clearCooldown(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("check")) {
            long remaining = pvpTimer.getRemaining(player);
            if (remaining <= 0L) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
                return true;
            }

            sender.sendMessage(ChatColor.GRAY + "Your " + pvpTimer.getDisplayName() + ChatColor.GRAY + " timer is active for another " + ChatColor.BOLD
                    + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.GRAY + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.');

            return true;
        }

        printUsage(sender, label, pvpTimer);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("enable", "time");

    /**
     * Prints the usage of this command to a sender.
     *
     * @param sender
     *            the sender to print for
     * @param label
     *            the label used for command
     */
    private void printUsage(CommandSender sender, String label, InvincibilityTimer pvpTimer) {
    	sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------*-------------------------*--------");
        sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "PVP Timer Help" + ChatColor.GRAY + " (Page 1 of 1)");
        sender.sendMessage(ChatColor.RED + "/" + label + " enable" + ChatColor.GRAY + " §7" + ChatColor.WHITE +"Removes your PVP Timer");
        sender.sendMessage(ChatColor.RED + "/" + label + " time"+ ChatColor.GRAY + "§7 " + ChatColor.WHITE + "Check remaining PVP Timer");
        sender.sendMessage(ChatColor.RED + "/lives " + ChatColor.GRAY + " §7" + ChatColor.WHITE + "Displays help for player lives.");
    	sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------*-------------------------*--------");
    }
}
