package me.rainny.reaper.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.rainny.reaper.ymls.SettingsYML;

public class EndListener
implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == TeleportCause.END_PORTAL) {
            SettingsYML.reload();
        	event.setTo(SettingsYML.END_ENTRANCE);
            event.getPlayer().sendMessage("hi");
        }
    }
}