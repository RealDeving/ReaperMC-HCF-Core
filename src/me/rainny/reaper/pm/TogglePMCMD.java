package me.rainny.reaper.pm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class TogglePMCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		Player p = (Player) sender;
		if (c.equalsIgnoreCase("togglepm")) {
			if (!MsgCMD.toggle.containsKey(p.getUniqueId())) {
				MsgCMD.toggle.put(p.getUniqueId(), true);
			}
			if (MsgCMD.toggle.get(p.getUniqueId()) == true) {
				MsgCMD.toggle.put(p.getUniqueId(), false);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&9Reaper &8» &7You have toggled your messages &coff"));
			} else {
				MsgCMD.toggle.put(p.getUniqueId(), true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&9Reaper &8» &7You have toggled your messages &aon"));
			}
		}
		return true;
	}

}