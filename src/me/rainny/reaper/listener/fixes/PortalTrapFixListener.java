package me.rainny.reaper.listener.fixes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import me.rainny.reaper.HCF;

/**
 * Listener that prevents {@link Player}s from being trapped in portals.
 */
public class PortalTrapFixListener implements Listener {
    private final HCF plugin;

    public PortalTrapFixListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if(e.getClickedBlock() == null) return;
        if(this.plugin.getFactionManager().getFactionAt(e.getClickedBlock().getLocation()).isSafezone()) return;
        if(e.getClickedBlock().getType() == Material.PORTAL){
            e.getClickedBlock().setType(Material.AIR);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&eYou have &cdisabled &ethis portal&e."));
        }
    }


}
