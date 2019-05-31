package me.rainny.reaper.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.rainny.reaper.HCF;

public class HitDetectionListener implements Listener {

    @SuppressWarnings("deprecation")
	public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, HCF.getPlugin());
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setMaximumNoDamageTicks(17);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setMaximumNoDamageTicks(17);
    }
}