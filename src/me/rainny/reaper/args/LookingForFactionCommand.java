package me.rainny.reaper.args;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.utils.Cooldown;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.ymls.LangYML;

public class LookingForFactionCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
		
		Player p = (Player) sender;
		PlayerFaction faction = HCF.plugin.getFactionManager().getPlayerFaction(p.getUniqueId());
		
		if(faction == null) {
			if(!Cooldown.isInCooldown(p.getUniqueId(), "lff")) {
				Cooldown cd = new Cooldown(p.getUniqueId(), "lff", (int) TimeUnit.HOURS.toSeconds(1));
				cd.start();
				for(Player pp : Bukkit.getOnlinePlayers()) {
					if(pp instanceof Player) {
						for(String s : LangYML.LFF) {
							pp.sendMessage(s.replaceAll("%player%", p.getName()));
						}
					}
				}
			} else {
				int millis = Cooldown.getTimeLeft(p.getUniqueId(), "lff") * 1000;
				String hms = String.format("%02dh %02dm %02ds", TimeUnit.MILLISECONDS.toHours(millis),
		                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
		                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
				p.sendMessage("§cYou must wait " + hms);
			}
		} else {
			p.sendMessage("§cYou are already in a faction.");
		}
		
		
		return true;
	}

}
