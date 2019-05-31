package me.rainny.reaper.args;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.rainny.reaper.ymls.LangYML;

public class CoordsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender p, Command command, String label, String[] args) {
        if (!(p instanceof Player)) {
            p.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
		for(String s : LangYML.COORDS) {
			p.sendMessage(s);
		}
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
    
}