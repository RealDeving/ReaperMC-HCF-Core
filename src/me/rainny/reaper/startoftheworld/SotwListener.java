package me.rainny.reaper.startoftheworld;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import me.rainny.reaper.HCF;

public class SotwListener implements Listener {

	private final HCF plugin;

	public SotwListener(HCF plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() != EntityDamageEvent.DamageCause.SUICIDE
				&& plugin.getSotwTimer().getSotwRunnable() != null) {
			Player p = (Player) event.getEntity();
			if (!SotwCommand.sotwEnabled.contains(p.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() != EntityDamageEvent.DamageCause.SUICIDE
				&& plugin.getSotwTimer().getSotwRunnable() != null) {
			Player p = (Player) event.getEntity();
			Player t = (Player) event.getDamager();
			if (!SotwCommand.sotwEnabled.contains(p.getUniqueId())
					|| !SotwCommand.sotwEnabled.contains(t.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
}
