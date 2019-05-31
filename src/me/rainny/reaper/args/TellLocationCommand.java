package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.struct.ChatChannel;
import me.rainny.reaper.factionutils.type.PlayerFaction;

public class TellLocationCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
		Player p = (Player) sender;
		if(HCF.plugin.getFactionManager().getPlayerFaction(p.getUniqueId()) == null) {
			p.sendMessage("§cYou do not have a faction.");
			return true;
		}
		
		PlayerFaction faction = HCF.plugin.getFactionManager().getPlayerFaction(p.getUniqueId());
		
		if(faction.getMember(p).getChatChannel() == ChatChannel.FACTION) {
			p.chat("[" + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + "]");
		} else if(faction.getMember(p).getChatChannel() == ChatChannel.ALLIANCE) {
			p.chat("/f c f");
			p.chat("[" + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + "]");
			p.chat("/f c a");
		} else {
			p.chat("/f c f");
			p.chat("[" + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + "]");
			p.chat("/f c p");
		}
		
		
		return true;
	}

}
