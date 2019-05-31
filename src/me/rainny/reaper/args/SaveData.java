package me.rainny.reaper.args;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.rainny.reaper.HCF;

public class SaveData implements CommandExecutor {

	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
	    HCF.getInstance().getCombatLogListener().removeCombatLoggers();
	    HCF.getInstance().getDeathbanManager().saveDeathbanData();
	    HCF.getInstance().getEconomyManager().saveEconomyData();
	    HCF.getInstance().getFactionManager().saveFactionData();
	    HCF.getInstance().getKeyManager().saveKeyData();
	    HCF.getInstance().getTimerManager().saveTimerData();
	    HCF.getInstance().getUserManager().saveUserData();
	    sender.sendMessage("§aSuccesffully Saved Server data.");
		return true;
	}

}
