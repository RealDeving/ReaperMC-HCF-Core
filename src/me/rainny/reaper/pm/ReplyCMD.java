package me.rainny.reaper.pm;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ReplyCMD implements CommandExecutor {

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		Player p = (Player) sender;
		if(c.equalsIgnoreCase("r") || c.equalsIgnoreCase("reply")) {
				if(args.length == 0) {
					p.sendMessage("§cUsage: /" + c + " <Message..>");
				} else if (args.length >= 1) {
					String message = "";
					for (int i = 0; i < args.length; i++) {
					    if (message != "") message += " ";
					    String part = args[i];
					    message += part;
					}
					if(MsgCMD.lastplayer.get(p.getUniqueId()) != null) {
						Player target = Bukkit.getPlayer(MsgCMD.lastplayer.get(p.getUniqueId()));
						if(target != null) {
							if(target.isOnline()) {
								target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7(From &f%player%&7) %msg%".replace("%player%", PermissionsEx.getUser(p).getSuffix() + p.getName()).replace("%msg%", message)));
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7(To &f%player%&7) %msg%".replace("%player%", PermissionsEx.getUser(target).getSuffix() + target.getName()).replace("%msg%", message)));
							} else {
								p.sendMessage("&9Reaper &8» §cThat player is not online.");
							}
						} else {
							p.sendMessage("&9Reaper &8» §cThat player is not online.");
						}
					} else {
						p.sendMessage("&9Reaper &8» §cYou have not messaged anyone.");
					}
				}
		}
		return true;
	}

}