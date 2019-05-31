package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReaperCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		sender.sendMessage(ChatColor.RED + "This HCF Core is not made by the owners of the server but was developed by the kids at ReaperMC.org dont use cores and claim they are urs thx from _Real <3");
		return true;
	}

}
