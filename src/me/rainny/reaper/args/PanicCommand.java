package me.rainny.reaper.args;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PanicCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "freeze " + p.getName());
			return true;
		}
		return true;
	}

}
