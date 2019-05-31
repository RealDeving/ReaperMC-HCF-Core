package me.rainny.reaper.listener.combatloggers;

import java.beans.ConstructorProperties;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.minecraft.server.v1_7_R4.EntityHuman;

public class LoggerDeathEvent extends Event {
	@ConstructorProperties({ "loggerEntity" })
	public LoggerDeathEvent(LoggerEntity loggerEntity, EntityHuman killer) {
		this.loggerEntity = loggerEntity;
		this.killer = killer;
	}

	private static final HandlerList handlers = new HandlerList();
	private final LoggerEntity loggerEntity;
	private final EntityHuman killer;
	
	public LoggerEntity getLoggerEntity() {
		return this.loggerEntity;
	}
	
	public EntityHuman getKiller() {
		return this.killer;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
