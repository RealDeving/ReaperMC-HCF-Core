package me.rainny.reaper.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.rainny.reaper.HCF;
import me.rainny.reaper.ymls.SettingsYML;

public class EndFix {

	private HCF plugin;

	@SuppressWarnings("deprecation")
	public EndFix(HCF plugin) {
		this.plugin = plugin;

		new BukkitRunnable() {

			@Override
			public void run() {

				for (Player pp : Bukkit.getOnlinePlayers()) {
					if (pp.getWorld().getEnvironment() == Environment.THE_END) {
						if (pp.getLocation().getBlock().getType() == Material.WATER
								|| pp.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
							pp.teleport(SettingsYML.END_EXIT);
						}
					}

					if (plugin.getSotwTimer() != null) {
						if (pp.getLocation().getBlockY() < 0) {
							if (pp.getWorld().getEnvironment() == Environment.THE_END) {
								pp.teleport(SettingsYML.END_EXIT);
							}

						}
					}
				}

			}
		}.runTaskTimer(this.plugin, 0L, 1L);
	}

}