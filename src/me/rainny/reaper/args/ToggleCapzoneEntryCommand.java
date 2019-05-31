package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionUser;

import java.util.Collections;
import java.util.List;

/**
 * Command used to toggle messages shown when entering or leaving
 */
public class ToggleCapzoneEntryCommand implements CommandExecutor, TabExecutor {

    private final HCF plugin;

    public ToggleCapzoneEntryCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        FactionUser factionUser = plugin.getUserManager().getUser(((Player) sender).getUniqueId());
        boolean newStatus = !factionUser.isCapzoneEntryAlerts();
        factionUser.setCapzoneEntryAlerts(newStatus);

        sender.sendMessage(ChatColor.GRAY + "You will now " + (newStatus ? ChatColor.GREEN.toString() : ChatColor.RED + "un") + "able" + ChatColor.GRAY + " to see capture zone entry messages.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
