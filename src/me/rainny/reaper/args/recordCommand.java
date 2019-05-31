package me.rainny.reaper.args;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rainny.reaper.ymls.LangYML;

public class recordCommand implements CommandExecutor{

	public static HashMap<UUID, Boolean> recording = new HashMap<UUID, Boolean>();
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
		
		Player p = (Player) sender;
		if(recording.get(p.getUniqueId()) == false) {
			for(Player pp : Bukkit.getOnlinePlayers()) {
				if(pp instanceof Player) {
					for(String s : LangYML.STOPPED_RECORDING) {
						pp.sendMessage(s.replaceAll("%player%", p.getName()));
					}
				}
			}
			sender.sendMessage("§4WARNING: §cMisuse of this command may result in a BAN");
			recording.put(p.getUniqueId(), true);
		} else {
			for(Player pp : Bukkit.getOnlinePlayers()) {
				if(pp instanceof Player) {
					for(String s : LangYML.STARTED_RECORDING) {
						pp.sendMessage(s.replaceAll("%player%", p.getName()));
					}
				}
			}
			recording.put(p.getUniqueId(), false);
		}
		
		
		
		return true;
	}

}
