package me.rainny.reaper.listener.fixes;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class PluginViewListener implements Listener {

	@EventHandler
	public void onPreCommand(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().contains("plugins") && e.getMessage().contains("pl") && e.getMessage().contains("ver") && e.getMessage().contains("version") && e.getMessage().contains("about") && e.getMessage().contains(":")) {
			if(!e.getPlayer().isOp()) {
				e.getPlayer().sendMessage(ChatColor.RED + "No permission.");
				e.setCancelled(true);
			}
		}
	}

}
