package me.rainny.reaper.timer.type;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import me.rainny.reaper.HCF;
import me.rainny.reaper.timer.PlayerTimer;

public class FishermenTimer extends PlayerTimer implements Listener {
	
	public HCF plugin;
	
	public FishermenTimer(HCF plugin) {
		super("Fishermen Rod", TimeUnit.SECONDS.toMillis(20L));
	    this.plugin = plugin;
	}

	@Override
	public String getScoreboardPrefix() {
		return ChatColor.GOLD.toString() + ChatColor.BOLD;
	}

}
