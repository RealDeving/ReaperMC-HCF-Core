package me.rainny.reaper.listener.combatloggers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class CombatLogEntry {
	public final LoggerEntity loggerEntity;
	public final BukkitTask task;
	private final Player player;
	private String killMessage = null;

	public CombatLogEntry(Player player, LoggerEntity loggerEntity, BukkitTask task) {
		this.player = player;
		this.loggerEntity = loggerEntity;
		this.task = task;
	}

	public Player getPlayer() {
		return player;
	}

	public LoggerEntity getLoggerEntity() {
		return loggerEntity;
	}

	public BukkitTask getTask() {
		return task;
	}

	public String getKillMessage() {
		return killMessage;
	}

	public void setKillMessage(String killMessage) {
		this.killMessage = killMessage;
	}
}
