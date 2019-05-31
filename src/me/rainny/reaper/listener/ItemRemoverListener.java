package me.rainny.reaper.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.rainny.reaper.HCF;

public class ItemRemoverListener implements Listener {
	
	private HCF hcf;
	public ItemRemoverListener(HCF hcf) {
		this.hcf = hcf;
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Item item = e.getItemDrop();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				item.remove();
				
			}
		}.runTaskLaterAsynchronously(HCF.getPlugin(), 20L * 60L * 3L);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(hcf, new Runnable() {
			public void run() {
				for(int i = 0 ; i < e.getDrops().size() ; i++) {
					e.getDrops().remove(i);
				}
			}
		}, 20L * 60L * 3L);
	}

}
