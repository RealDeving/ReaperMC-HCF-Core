package me.rainny.reaper.listener.combatloggers;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LoggerSpawnEvent
        extends Event
        implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final LoggerEntity loggerEntity;

    public LoggerEntity getLoggerEntity()
    {
        return this.loggerEntity;
    }

    public LoggerSpawnEvent(LoggerEntity loggerEntity)
    {
        this.loggerEntity = loggerEntity;
    }

    public boolean isCancelled()
    {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
