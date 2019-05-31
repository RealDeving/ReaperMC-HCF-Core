package me.rainny.reaper.pm;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class MsgCMD implements CommandExecutor {

	public static HashMap<UUID, String> lastplayer;
	public static HashMap<UUID, Boolean> sounds;
	public static HashMap<UUID, Boolean> toggle;
	
	public MsgCMD() {
		lastplayer = new HashMap<UUID, String>();
		sounds = new HashMap<UUID, Boolean>();
		toggle = new HashMap<UUID, Boolean>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		Player p = (Player) sender;
		StringBuilder str = new StringBuilder();
		
		if(c.equalsIgnoreCase("msg") 
				|| c.equalsIgnoreCase("m")
				|| c.equalsIgnoreCase("message") 
				|| c.equalsIgnoreCase("tell") 
				|| c.equalsIgnoreCase("w")) {
				if(args.length == 0) {
					p.sendMessage("§cUsage: /" + c + " <Player> <Message>");
				} else if(args.length == 1) {
					p.sendMessage("§cUsage: /" + c + " <Player> <Message>");
				} else if(args.length >= 2) {
					for (int i = 1; i < args.length; i++) {
                        str.append(args[i] + " ");
                    }
					String message = str.toString();
					Player target = Bukkit.getPlayer(args[0]);
					if(target != null) {
						if(target.isOnline()) {
							
							if (!toggle.containsKey(p.getUniqueId())) {
								toggle.put(p.getUniqueId(), true);
							}
							if (!toggle.containsKey(target.getUniqueId())) {
								toggle.put(target.getUniqueId(), true);
							}
							
							if(toggle.get(p.getUniqueId())) {
								if(toggle.get(target.getUniqueId())) {
									target.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											"&7(From &f%player%&7) %msg%".
											replace("%player%", PermissionsEx.getUser(p).getSuffix() + p.getName()).
											replace("%msg%", message)));
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											"&7(To &f%player%&7) %msg%".
											replace("%player%", PermissionsEx.getUser(target).getSuffix() + target.getName())).
											replace("%msg%", message));
									lastplayer.put(p.getUniqueId(), target.getName());
									lastplayer.put(target.getUniqueId(), p.getName());
									if (!sounds.containsKey(target.getUniqueId())) {
										sounds.put(target.getUniqueId(), true);
									}
									if(sounds.get(target.getUniqueId())) {
										target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 10, 1);
									}
								} else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Reaper &8» §c" + target.getName() + " has private messages off."));
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Reaper &8» §cYou cannot message whilst private messages are off."));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Reaper &8» §cThat player is not online."));
						}
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Reaper &8» §cThat player is not online."));
					}
				}
		}
		return true;
	}

}