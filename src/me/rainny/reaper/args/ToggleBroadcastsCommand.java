package me.rainny.reaper.args;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.rainny.reaper.ymls.SettingsYML;

import java.util.Collections;
import java.util.List;

public class ToggleBroadcastsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("Core.staff")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        boolean newStatus = !SettingsYML.DIAMOND_ORE_ALERTS;
        SettingsYML.DIAMOND_ORE_ALERTS = newStatus;
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + sender.getName() + ChatColor.GRAY + " has "
                + (newStatus ? ChatColor.GREEN +"enabled" : ChatColor.RED + "disabled") + ChatColor.GRAY + " found diamond ore notifications.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command commanda, String label, String[] args) {
        return Collections.emptyList();
    }
}
