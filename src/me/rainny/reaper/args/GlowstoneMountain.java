package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.rainny.reaper.HCF;

public class GlowstoneMountain implements CommandExecutor {

	private HCF hcf;
	public GlowstoneMountain(HCF hcf) {
		this.hcf = hcf;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("command.glowstonemountain")) {
			sender.sendMessage(ChatColor.RED + "You don not have permission for this command.");
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage(ChatColor.GRAY + "You have reset Glowstone Mountain!");
			hcf.getClaimHandler();
		}
		return true;
	}

}
