package me.rainny.reaper.args;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionUser;

public class ResetStatsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if(args.length <= 0) {
			sender.sendMessage("§cUsage: /" + c + " <player>" );
			return true;
		} else if(args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if(target.isOnline() && target != null) {
				FactionUser user = HCF.getInstance().getUserManager().getUser(target.getUniqueId());
				user.setKills(0);
				user.setDeaths(0);
				target.setStatistic(Statistic.PLAYER_KILLS, 0);
				target.setStatistic(Statistic.DEATHS, 0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Diamond", 0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Gold", 0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Emerald", 0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Coal", 0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Redstone",0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Iron", 0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Lapis", 0);
				HCF.getInstance().getConfig().set(target.getUniqueId().toString() + ".Mined.Stone", 0);
				sender.sendMessage("§aSuccesfully reset " + args[0] + "'s stats.");
			}
		}
		return true;
	}

}
