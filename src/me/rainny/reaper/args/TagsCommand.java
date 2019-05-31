package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rainny.reaper.systems.Tags.Tags;


public class TagsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can access tags.");
            return true;
        }
		Player p = (Player) sender;
		Tags.openGUI(p);
		
		return true;
	}
	
	

}
