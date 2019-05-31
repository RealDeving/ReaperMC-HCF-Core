package me.rainny.reaper.armors.bard;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.doctordark.utils.DurationFormatter;
import com.doctordark.utils.other.chat.Lang;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.rainny.reaper.HCF;
import me.rainny.reaper.armors.PvpClass;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.PlayerFaction;

@SuppressWarnings("deprecation")
public class BardClass extends PvpClass implements Listener {

	public static final int HELD_EFFECT_DURATION_TICKS = 100; // the amount of time in ticks to apply a Held potion
																// effect for faction members

	private static final long BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(10L); // time in milliseconds for Bard
																						// buff cooldowns
	private static final int TEAMMATE_NEARBY_RADIUS = 25;
	private static final long HELD_REAPPLY_TICKS = 20L;

	private final Map<UUID, BardData> bardDataMap = new HashMap<>();
	private final Map<Material, BardEffect> bardEffects = new EnumMap<>(Material.class);

	private final HCF plugin;

	public BardClass(HCF plugin) {
		super("Bard", TimeUnit.SECONDS.toMillis(5L));
		this.plugin = plugin;

		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		this.bardEffects.put(Material.WHEAT, new BardEffect(35, new PotionEffect(PotionEffectType.SATURATION, 120, 1),
				new PotionEffect(PotionEffectType.SATURATION, 100, 0)));
		this.bardEffects.put(Material.SUGAR, new BardEffect(35, new PotionEffect(PotionEffectType.SPEED, 120, 2),
				new PotionEffect(PotionEffectType.SPEED, 100, 1)));
		this.bardEffects.put(Material.BLAZE_POWDER,
				new BardEffect(45, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1),
						new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0)));
		this.bardEffects.put(Material.IRON_INGOT,
				new BardEffect(35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2),
						new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0)));
		this.bardEffects.put(Material.GHAST_TEAR,
				new BardEffect(30, new PotionEffect(PotionEffectType.REGENERATION, 60, 2),
						new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
		this.bardEffects.put(Material.FEATHER, new BardEffect(40, new PotionEffect(PotionEffectType.JUMP, 120, 2),
				new PotionEffect(PotionEffectType.JUMP, 100, 0)));
		this.bardEffects.put(Material.SPIDER_EYE,
				new BardEffect(55, new PotionEffect(PotionEffectType.WITHER, 100, 1), null));
		this.bardEffects.put(Material.MAGMA_CREAM,
				new BardEffect(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0),
						new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
	}

	@Override
	public boolean onEquip(Player player) {
		if (plugin.getTimerManager().getInvincibilityTimer().getRemaining(player) > 0L) {
			player.sendMessage(ChatColor.RED + "You cannot equip the " + getName() + " Class whilst PVP Protected.");
			return false;
		}

		if (!super.onEquip(player)) {
			return false;
		}

		BardData bardData = new BardData();
		bardDataMap.put(player.getUniqueId(), bardData);
		bardData.startEnergyTracking();
		bardData.heldTask = new BukkitRunnable() {
			int lastEnergy;

			@Override
			public void run() {
				// Apply the bard effects here.
				ItemStack held = player.getItemInHand();
				if (held != null) {
					BardEffect bardEffect = bardEffects.get(held.getType());
					if (bardEffect == null)
						return;

					if (bardEffect.heldable.getType() == PotionEffectType.JUMP) {
						plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.heldable);
					}

					if (!plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
						// Apply the held effect to faction members.
						PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
						if (playerFaction != null) {
							Collection<Entity> nearbyEntities = player.getNearbyEntities(TEAMMATE_NEARBY_RADIUS,
									TEAMMATE_NEARBY_RADIUS, TEAMMATE_NEARBY_RADIUS);
							for (Entity nearby : nearbyEntities) {
								if (nearby instanceof Player && !player.equals(nearby)) {
									Player target = (Player) nearby;
									if (playerFaction.getMembers().containsKey(target.getUniqueId())) {
										plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.heldable);
									}
								}
							}
						}
					}
				}

				int energy = (int) getEnergy(player);
				// the -1 check is for offsets with the energy per millisecond
				if (energy != 0 && energy != lastEnergy
						&& (energy % 10 == 0 || lastEnergy - energy - 1 > 0 || energy == BardData.MAX_ENERGY)) {
					lastEnergy = energy;
					player.sendMessage(ChatColor.GOLD + name + " energy is now at " + ChatColor.RED + energy
							+ ChatColor.GOLD + '.');
				}
			}
		}.runTaskTimer(plugin, 0L, HELD_REAPPLY_TICKS);
		return true;
	}

	@Override
	public void onUnequip(Player player) {
		super.onUnequip(player);
		clearBardData(player.getUniqueId());
	}

	private void clearBardData(UUID uuid) {
		BardData bardData = bardDataMap.remove(uuid);
		if (bardData != null && bardData.getHeldTask() != null) {
			((BukkitRunnable) bardData.getHeldTask()).cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		clearBardData(event.getPlayer().getUniqueId());
	}

	private final TObjectLongMap<UUID> msgCooldowns = new TObjectLongHashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PvpClass equipped = plugin.getPvpClassManager().getEquippedClass(player);
		if (equipped == null || !equipped.equals(this)) {
			return;
		}

		UUID uuid = player.getUniqueId();
		long lastMessage = msgCooldowns.get(uuid);
		long millis = System.currentTimeMillis();
		if (lastMessage != msgCooldowns.getNoEntryValue() && lastMessage - millis > 0L) {
			return;
		}

		ItemStack newStack = player.getInventory().getItem(event.getNewSlot());
		if (newStack != null) {
			BardEffect bardEffect = bardEffects.get(newStack.getType());
			if (bardEffect != null) {
				msgCooldowns.put(uuid, millis + 1500L);
				player.sendMessage(ChatColor.GOLD + "Bard Effect: " + ChatColor.GOLD);
				player.sendMessage(ChatColor.GOLD + "\u00bb " + ChatColor.GRAY + "Clickable Effect: " + ChatColor.AQUA
						+ Lang.fromPotionEffectType(bardEffect.clickable.getType()) + ' '
						+ (bardEffect.clickable.getAmplifier() + 1) + ChatColor.GRAY + " ("
						+ (bardEffect.clickable.getDuration() / 20) + "s)");
				player.sendMessage(ChatColor.GOLD + "\u00bb " + ChatColor.GRAY + "Energy Cost: " + ChatColor.AQUA
						+ bardEffect.energyCost);
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if (!event.hasItem()) {
			return;
		}
		final Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_AIR || (!event.isCancelled() && action == Action.RIGHT_CLICK_BLOCK)) {
			final ItemStack stack = event.getItem();
			final BardEffect bardEffect = this.bardEffects.get(stack.getType());
			if (bardEffect == null || bardEffect.clickable == null) {
				return;
			}
			event.setUseItemInHand(Event.Result.DENY);
			final Player player = event.getPlayer();
			final BardData bardData = this.bardDataMap.get(player.getUniqueId());
			if (bardData != null) {
				if (!this.canUseBardEffect(player, bardData, bardEffect, true)) {
					return;
				}
				if (stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() - 1);
				} else {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				}
				if (bardEffect != null
						&& !this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
					final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
					if (playerFaction != null
							&& !bardEffect.clickable.getType().equals((Object) PotionEffectType.WITHER)) {
						final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(25.0,
								25.0, 25.0);
						for (final Entity nearby : nearbyEntities) {
							if (nearby instanceof Player && !player.equals(nearby)) {
								final Player target = (Player) nearby;
								if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
									continue;
								}
								plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
							}
						}
					} else if (playerFaction != null
							&& bardEffect.clickable.getType().equals((Object) PotionEffectType.WITHER)) {
						final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(25.0,
								25.0, 25.0);
						for (final Entity nearby : nearbyEntities) {
							if (nearby instanceof Player && !player.equals(nearby)) {
								final Player target = (Player) nearby;
								if (playerFaction.getMembers().containsKey(target.getUniqueId())) {
									continue;
								}
								plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
							}
						}
					} else if (bardEffect.clickable.getType().equals((Object) PotionEffectType.WITHER)) {
						final Collection<Entity> nearbyEntities = (Collection<Entity>) player.getNearbyEntities(25.0,
								25.0, 25.0);
						for (final Entity nearby : nearbyEntities) {
							if (nearby instanceof Player && !player.equals(nearby)) {
								final Player target = (Player) nearby;
								plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
							}
						}
					}
				}
				plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);
				bardData.setBuffCooldown(BUFF_COOLDOWN_MILLIS);

				player.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&eYou have just usted a &aBard Buff &ethat cost you &l45 &eof your Energy."));
				// player.sendMessage(ChatColor.GOLD + "You have just used " + name + " buff " +
				// ChatColor.RED + Lang.fromPotionEffectType(bardEffect.clickable.getType()) + '
				// '
				// + (bardEffect.clickable.getAmplifier() + 1) + ChatColor.GOLD + " costing you
				// " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.GRAY + " energy. "
				// + "Your energy is now " + ChatColor.GREEN + ((newEnergy * 10.0) / 10.0)/*
				// TODO:neeeded? */+ ChatColor.GRAY + '.');
			}
		}
	}

	private boolean canUseBardEffect(Player player, BardData bardData, BardEffect bardEffect, boolean sendFeedback) {
		String errorFeedback = null;
		double currentEnergy = bardData.getEnergy();
		if (bardEffect.energyCost > currentEnergy) {
			errorFeedback = ChatColor.RED + "You need at least " + ChatColor.BOLD + bardEffect.energyCost
					+ ChatColor.RED + " energy to use this Bard buff, whilst you only have " + ChatColor.BOLD
					+ currentEnergy + ChatColor.RED + '.';
		}

		long remaining = bardData.getRemainingBuffDelay();
		if (remaining > 0L) {
			errorFeedback = ChatColor.RED + "You cannot use this bard buff for another " + ChatColor.BOLD
					+ DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + '.';
		}

		Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
		if (factionAt.isSafezone()) {
			errorFeedback = ChatColor.RED + "You may not use bard buffs in safe-zones.";
		}

		if (sendFeedback && errorFeedback != null) {
			player.sendMessage(errorFeedback);
		}

		return errorFeedback == null;
	}

	@Override
	public boolean isApplicableFor(Player player) {
		ItemStack helmet = player.getInventory().getHelmet();
		if (helmet == null || helmet.getType() != Material.GOLD_HELMET)
			return false;

		ItemStack chestplate = player.getInventory().getChestplate();
		if (chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE)
			return false;

		ItemStack leggings = player.getInventory().getLeggings();
		if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS)
			return false;

		ItemStack boots = player.getInventory().getBoots();
		return !(boots == null || boots.getType() != Material.GOLD_BOOTS);
	}

	public long getRemainingBuffDelay(Player player) {
		synchronized (bardDataMap) {
			BardData bardData = bardDataMap.get(player.getUniqueId());
			return bardData == null ? 0L : bardData.getRemainingBuffDelay();
		}
	}

	/**
	 * Gets the energy of a {@link Player}.
	 *
	 * @param player the {@link Player} to get for
	 * @return the energy, or 0 if not tracking this player
	 */
	public double getEnergy(Player player) {
		synchronized (bardDataMap) {
			BardData bardData = bardDataMap.get(player.getUniqueId());
			return bardData == null ? 0 : bardData.getEnergy();
		}
	}

	public long getEnergyMillis(Player player) {
		synchronized (bardDataMap) {
			BardData bardData = bardDataMap.get(player.getUniqueId());
			return bardData == null ? 0 : bardData.getEnergyMillis();
		}
	}

	/**
	 * Sets the energy of a {@link Player}.
	 *
	 * @param player the {@link Player} to set for
	 * @param energy the energy amount to set
	 * @return the new energy amount
	 */
	public double setEnergy(Player player, double energy) {
		BardData bardData = bardDataMap.get(player.getUniqueId());
		if (bardData == null)
			return 0.0;

		bardData.setEnergy(energy);
		return bardData.getEnergy();
	}
}
