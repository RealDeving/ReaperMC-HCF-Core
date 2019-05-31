package me.rainny.reaper.args;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.rainny.reaper.ymls.LangYML;
import me.rainny.reaper.ymls.ReclaimYML;
import me.rainny.reaper.ymls.SettingsYML;

public class ReclaimCommand implements CommandExecutor{
	
	FileConfiguration reclaimsettings = ReclaimYML.reclaimsettings.getConfiguration();
	FileConfiguration reclaimusers = ReclaimYML.reclaimusers.getConfiguration();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou must be a player to run this command dipshit.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(!SettingsYML.KIT_MAP) {
			if(!reclaimusers.getStringList("Reclaimed").contains(p.getUniqueId().toString())) {
				for(String s : reclaimsettings.getKeys(false)) {
					if(p.hasPermission(reclaimsettings.getString(s + ".permission"))) {
						List<String> reclaimed = reclaimusers.getStringList("Reclaimed");
						reclaimed.add(p.getUniqueId().toString());
						reclaimusers.set("Reclaimed", reclaimed);
						ReclaimYML.reclaimusers.saveConfig();
						for(String s2 : reclaimsettings.getStringList(s + ".commands")) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s2
									.replace("%player%", sender.getName())
									.replace("%rank%", s));
						}
						return true;
					}
				}
			} else {
				p.sendMessage(LangYML.ALREADY_RECLAIMED);
				return true;
			}
			p.sendMessage(LangYML.NO_RECLAIM);
		} else {
			p.sendMessage(LangYML.KITMAP_RECLAIM);
		}
		
		
		return true;
	}


}
