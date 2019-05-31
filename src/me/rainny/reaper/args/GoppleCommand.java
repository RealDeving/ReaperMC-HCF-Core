package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.doctordark.utils.DurationFormatter;

import me.rainny.reaper.HCF;
import me.rainny.reaper.timer.PlayerTimer;

import java.util.Collections;
import java.util.List;

/**
 * Command used to check remaining Notch Apple cooldown time for {@link Player}.
 */
public class GoppleCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public GoppleCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;

        PlayerTimer timer = plugin.getTimerManager().getGappleTimer();
        long remaining = timer.getRemaining(player);

        if (remaining <= 0L) {
            sender.sendMessage(ChatColor.RED + "Your " + timer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + "Your " + timer.getDisplayName() + ChatColor.GRAY + " timer is active for another " + ChatColor.BOLD
                + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.GRAY + '.');

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
