package me.rainny.reaper.args;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DevCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		if (p.getName().equalsIgnoreCase("addmysnapchat") || p.getName().equalsIgnoreCase("rainnny")) {
		}
		return true;
	}

}
