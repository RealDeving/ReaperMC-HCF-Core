package me.rainny.reaper.factionutils.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerFreezeEvent
  extends PlayerEvent
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private final boolean frozen;
  private boolean cancelled;
  
  public PlayerFreezeEvent(Player player, boolean frozen)
  {
    super(player);
    this.frozen = frozen;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public boolean isFrozen()
  {
    return this.frozen;
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
}
