package me.rainny.reaper.timer.type;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.rainny.reaper.HCF;
import me.rainny.reaper.armors.archer.ArcherClass;
import me.rainny.reaper.timer.PlayerTimer;
import me.rainny.reaper.timer.event.TimerExpireEvent;

public class ArcherTimer extends PlayerTimer implements Listener {

	public String getScoreboardPrefix() {
		return ChatColor.GOLD.toString() + ChatColor.BOLD;
	}

	public ArcherTimer(HCF plugin) {
		super("Archer Mark", TimeUnit.SECONDS.toMillis(7L));
	}

	public void run() {
	}

	@EventHandler
	public void onExpire(TimerExpireEvent e) {
		if ((e.getUserUUID().isPresent()) && (e.getTimer().equals(this))) {
			UUID userUUID = e.getUserUUID().get();
			Player player = Bukkit.getPlayer(userUUID);
			if (player == null) {
				return;
			}
			Bukkit.getPlayer(ArcherClass.TAGGED.get(userUUID)).sendMessage(ChatColor.GRAY
					+ "Your archer mark on " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + " has expired.");
			player.sendMessage(ChatColor.GRAY + "You're no longer archer marked.");
			ArcherClass.TAGGED.remove(player.getUniqueId());
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player))) {
			Player entity = (Player) e.getEntity();
			if (getRemaining(entity) > 0L) {
				Double damage = Double.valueOf(e.getDamage() * 0.3D);
				e.setDamage(e.getDamage() + damage.doubleValue());
			}
		}
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow))) {
			Player entity = (Player) e.getEntity();
			Entity damager = (Entity) ((Arrow) e.getDamager()).getShooter();
			if (((damager instanceof Player)) && (getRemaining(entity) > 0L)) {
				if (((UUID) ArcherClass.TAGGED.get(entity.getUniqueId())).equals(damager.getUniqueId())) {
					setCooldown(entity, entity.getUniqueId());
				}
				Double damage = Double.valueOf(e.getDamage() * 0.3D);
				e.setDamage(e.getDamage() + damage.doubleValue());
			}
		}
	}
}
