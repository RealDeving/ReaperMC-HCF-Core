package me.rainny.reaper.args;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rainny.reaper.ymls.SettingsYML;

public class SetCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cThis command can only be ran by a player idiot");
		}
		
		Player p = (Player) sender;
		
		if(args.length == 0) {
			p.sendMessage("§8§m------------------------------------");
			p.sendMessage("");
			p.sendMessage("§c » §7/set entrance");
			p.sendMessage("");
			p.sendMessage("§c » §7/set exit");
			p.sendMessage("");
			p.sendMessage("§8§m------------------------------------");
			return true;
		}
		
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("entrance")) {
				Location l = p.getLocation();
				double x = l.getX();
				double y = l.getY();
				double z = l.getZ();
				
				String location = x + "," + y + "," + z;
				
				SettingsYML.config.set("set_end_locations.END_ENTRANCE", location);
				SettingsYML.save();
				p.sendMessage("§aEnd Entrance has been set to your location");
				
			} else if(args[0].equalsIgnoreCase("exit")) {
				Location l = p.getLocation();
				double x = l.getX();
				double y = l.getY();
				double z = l.getZ();
				
				String location = x + "," + y + "," + z;
				
				SettingsYML.config.set("set_end_locations.END_EXIT", location);
				SettingsYML.save();
				p.sendMessage("§aEnd Exit has been set to your location");
			}
		}
		
		
		return true;
	}

}
