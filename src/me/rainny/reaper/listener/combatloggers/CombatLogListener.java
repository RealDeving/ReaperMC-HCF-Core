package me.rainny.reaper.listener.combatloggers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import me.rainny.reaper.HCF;

public class CombatLogListener implements Listener {
	private static final Set<UUID> SAFE_DISCONNECTS = new HashSet<>();
	private static final Map<UUID, CombatLogEntry> LOGGERS = new HashMap<>();

	public static void safelyDisconnect(Player player, String reason) {
		if (SAFE_DISCONNECTS.add(player.getUniqueId())) {
			player.kickPlayer(reason);
		}
	}

	public static void addSafeDisconnect(UUID uuid) {
		SAFE_DISCONNECTS.add(uuid);
	}

	public static void removeCombatLoggers() {
		Iterator<CombatLogEntry> iterator = LOGGERS.values().iterator();
		while (iterator.hasNext()) {
			CombatLogEntry entry = (CombatLogEntry) iterator.next();
			entry.task.cancel();
			entry.loggerEntity.getBukkitEntity().remove();
			iterator.remove();
		}
		SAFE_DISCONNECTS.clear();
	}

	private final HCF plugin;

	public CombatLogListener(HCF plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (e.isCancelled() && ((CraftEntity) e.getEntity()).getHandle() instanceof LoggerEntity) {
			e.setCancelled(false);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuitSafe(PlayerQuitEvent event) {
		SAFE_DISCONNECTS.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onLoggerInteract(EntityInteractEvent event) {
		Collection<CombatLogEntry> entries = LOGGERS.values();
		Iterator<CombatLogEntry> var3 = entries.iterator();
		while (var3.hasNext()) {
			CombatLogEntry entry = var3.next();
			if (entry.loggerEntity.getBukkitEntity().equals(event.getEntity())) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLoggerDeath(LoggerDeathEvent event) {
		CombatLogEntry entry = LOGGERS.remove(event.getLoggerEntity().getUUID());
		if (entry != null) {
			entry.task.cancel();
			entry.setKillMessage(entry.getKillMessage());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		CombatLogEntry combatLogEntry = (CombatLogEntry) LOGGERS.remove(event.getPlayer().getUniqueId());
		if (combatLogEntry != null) {
			CraftLivingEntity loggerEntity = combatLogEntry.loggerEntity.getBukkitEntity();
			Player player = event.getPlayer();
			event.setSpawnLocation(loggerEntity.getLocation());
			player.setFallDistance(loggerEntity.getFallDistance());
			player.setHealth(Math.min(((CraftPlayer) player).getMaxHealth(), loggerEntity.getHealth()));
			player.setRemainingAir(loggerEntity.getRemainingAir());
			loggerEntity.remove();
			combatLogEntry.task.cancel();
			new BukkitRunnable() {
				public void run() {
					if (combatLogEntry.getKillMessage() != null) {
						player.sendMessage(ChatColor.RED + "You were killed whilst logged out: ");
						player.sendMessage(combatLogEntry.getKillMessage());
					}
				}
			}.runTaskLater(plugin, 20);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerInventory inventory = player.getInventory();
		if (player.getGameMode().equals(GameMode.CREATIVE))
			SAFE_DISCONNECTS.add(player.getUniqueId());
		if ((player.getGameMode() != GameMode.CREATIVE) && (!player.isDead()) && (!SAFE_DISCONNECTS.contains(uuid))) {
			/*if (this.plugin.getTimerManager().combatTimer.getRemaining(uuid) <= 0L) {
				return;  Disables logger for non-combat tagged users
			}*/
			Location location = player.getLocation();
			if (this.plugin.getFactionManager().getFactionAt(location).isSafezone()) {
				return;
			}
			if (LOGGERS.containsKey(player.getUniqueId())) {
				return;
			}
			boolean pvpTimer = plugin.getTimerManager().combatTimer.getRemaining(uuid) > 0;
			World world = location.getWorld();
			LoggerEntity loggerEntity = new LoggerEntity(player, world);
			LoggerSpawnEvent calledEvent = new LoggerSpawnEvent(loggerEntity);
			Bukkit.getPluginManager().callEvent(calledEvent);
			LOGGERS.put(uuid,
					new CombatLogEntry(player, loggerEntity,
							new LoggerRemovable(uuid, loggerEntity).runTaskLater(this.plugin,
									20 * 20 + (pvpTimer ? 20 * 15 : 0))));
			CraftLivingEntity craftEntity = loggerEntity.getBukkitEntity();
			if (craftEntity != null) {
				CraftLivingEntity craftLivingEntity = craftEntity;
				EntityEquipment entityEquipment = craftLivingEntity.getEquipment();
				entityEquipment.setItemInHand(inventory.getItemInHand());
				entityEquipment.setArmorContents(inventory.getArmorContents());
				craftLivingEntity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 1));
			}
		}
	}

	private static class LoggerRemovable extends BukkitRunnable {
		private final UUID uuid;
		private final LoggerEntity loggerEntity;

		public LoggerRemovable(UUID uuid, LoggerEntity loggerEntity) {
			this.uuid = uuid;
			this.loggerEntity = loggerEntity;
		}

		public void run() {
			if (CombatLogListener.LOGGERS.remove(this.uuid) != null) {
				this.loggerEntity.dead = true;
			}
		}
	}
}
