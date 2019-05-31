package me.rainny.reaper.args;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class TentacionCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player)sender;
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7R.I.P XXXTentacion... You'll be missed &4<3"));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l&ohttps://www.youtube.com/watch?v=pgN-vvVVxMA"));
		return true;
	}

}
