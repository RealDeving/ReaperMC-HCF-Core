package me.rainny.reaper.args;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.doctordark.utils.BukkitUtils;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.event.PlayerFreezeEvent;
import me.rainny.reaper.ymls.LangYML;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;

public class FreezeCommand implements CommandExecutor, Listener {
	public static TObjectLongMap<UUID> frozenPlayers;
	public static HashMap<Player, String> frozenReasons;
	private long defaultFreezeDuration;
	private long serverFrozenMillis;
	private HashSet<String> frozen;

	static {
		FreezeCommand.frozenPlayers = new TObjectLongHashMap<UUID>();
		FreezeCommand.frozenReasons = new HashMap<Player, String>();
	}

	public FreezeCommand(final HCF plugin) {
		this.frozen = new HashSet<String>();
		this.defaultFreezeDuration = TimeUnit.MINUTES.toMillis(60L);
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: " + "/freeze <player>");
			return true;
		}
		Long freezeTicks = this.defaultFreezeDuration;
		final long millis = System.currentTimeMillis();
		final Player target = Bukkit.getServer().getPlayer(args[0]);
		if (target == null || !PingCommand.canSee(sender, target)) {
			sender.sendMessage(
					ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
			return true;
		}
		final UUID targetUUID = target.getUniqueId();
		final boolean shouldFreeze = this.getRemainingPlayerFrozenMillis(targetUUID) > 0L;
		final PlayerFreezeEvent playerFreezeEvent = new PlayerFreezeEvent(target, shouldFreeze);
		Bukkit.getServer().getPluginManager().callEvent((Event) playerFreezeEvent);
		if (playerFreezeEvent.isCancelled()) {
			sender.sendMessage(LangYML.UNABLE_FREEZE.replaceAll("%player%", target.getName()));
			return false;
		}
		if (shouldFreeze) {
			this.frozen.remove(target.getName());
			FreezeCommand.frozenPlayers.remove(targetUUID);
			sender.sendMessage(LangYML.UNFROZE_PLAYER.replaceAll("%player%", target.getName()));
			target.sendMessage(LangYML.UNFROZEN.replaceAll("%player%", sender.getName()));
			target.removePotionEffect(PotionEffectType.BLINDNESS);
			FreezeCommand.frozenReasons.remove(target);
		} else if (args.length == 1) {
			this.frozen.add(target.getName());
			FreezeCommand.frozenPlayers.put(targetUUID, millis + freezeTicks);
			this.Message(target.getName());
			sender.sendMessage(LangYML.FROZE_PLAYER.replaceAll("%player%", target.getName()));
		} else {
			this.frozen.add(target.getName());
			FreezeCommand.frozenPlayers.put(targetUUID, millis + freezeTicks);
			this.Message(target.getName());
			sender.sendMessage(LangYML.FROZE_PLAYER.replaceAll("%player%", sender.getName()));
			final String reason2 = StringUtils.join(args, ' ', 1, args.length);
			FreezeCommand.frozenReasons.put(target, reason2);
		}
		return true;
	}

	private void Message(final String name) {
		final Player p = Bukkit.getPlayer(name);
		new BukkitRunnable() {
			public void run() {
				if (FreezeCommand.this.frozen.contains(name)) {
					for (String s : LangYML.FROZEN_MESSAGE) {
						p.sendMessage(s);
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously((Plugin) HCF.getPlugin(), 0L, 100L);
	}

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
			final String[] args) {
		return (args.length == 1) ? null : Collections.emptyList();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
		final Entity entity = event.getEntity();
		if (entity instanceof Player) {
			final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent) event, false);
			if (attacker == null) {
				return;
			}
			final Player player = (Player) entity;
			if (this.getRemainingServerFrozenMillis() > 0L
					|| this.getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) {
				attacker.sendMessage(ChatColor.RED + player.getName() + " is currently frozen, you may not attack.");
				event.setCancelled(true);
				return;
			}
			if (this.getRemainingServerFrozenMillis() > 0L
					|| this.getRemainingPlayerFrozenMillis(attacker.getUniqueId()) > 0L) {
				event.setCancelled(true);
				attacker.sendMessage(ChatColor.RED + "You may not attack players whilst frozen.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Location from = event.getFrom();
		final Location to = event.getTo();
		if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
			return;
		}
		final Player player = event.getPlayer();
		if (this.getRemainingServerFrozenMillis() > 0L
				|| this.getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) {
			event.setTo(event.getFrom());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		final Player player = e.getPlayer();
		if (this.getRemainingServerFrozenMillis() > 0L
				|| this.getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) {
			if (e.getCause() == TeleportCause.ENDER_PEARL) {
				player.sendMessage(ChatColor.RED + "You may not pearl while frozen!");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		if (this.getRemainingServerFrozenMillis() > 0L
				|| this.getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) {
			Block block = e.getClickedBlock();
			if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST)) {
				player.sendMessage(ChatColor.RED + "You may not open chests while frozen!");
				e.setCancelled(true);
				return;
			}
			if (player.getItemInHand().getType().equals(Material.ENDER_PEARL)) {
				player.sendMessage(ChatColor.RED + "You may not pearl while frozen!");
				e.setCancelled(true);
				return;
			}
			player.sendMessage(ChatColor.RED + "You may not interact with things while frozen!");
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onItemDrop(PlayerDropItemEvent e) {
		final Player player = e.getPlayer();
		if (this.getRemainingServerFrozenMillis() > 0L
				|| this.getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) {
			player.sendMessage(ChatColor.RED + "You may not drop items while frozen!");
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent e) {
		if (this.frozen.contains(e.getPlayer().getName())) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("smodsuite.staff")) {
					p.sendMessage("§c" + e.getPlayer().getName() + " §7has logged out whilst frozen!");
					TextComponent c = new TextComponent("§c(Click here to ban)");
					c.setClickEvent(new ClickEvent(Action.RUN_COMMAND,
							"/ban " + e.getPlayer().getName() + " Logged Out whilst Frozen -s"));
					p.spigot().sendMessage(c);
				}
			}
		}
	}

	public long getRemainingServerFrozenMillis() {
		return this.serverFrozenMillis - System.currentTimeMillis();
	}

	public long getRemainingPlayerFrozenMillis(final UUID uuid) {
		final long remaining = FreezeCommand.frozenPlayers.get((Object) uuid);
		if (remaining == FreezeCommand.frozenPlayers.getNoEntryValue()) {
			return 0L;
		}
		return remaining - System.currentTimeMillis();
	}
}
