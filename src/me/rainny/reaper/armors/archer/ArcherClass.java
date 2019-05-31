package me.rainny.reaper.armors.archer;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.rainny.reaper.HCF;
import me.rainny.reaper.armors.PvpClass;

public class ArcherClass extends PvpClass implements Listener {
	public static final HashMap<UUID, UUID> TAGGED = new HashMap<UUID, UUID>();
	private static final PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
	private static final PotionEffect ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 3);
	private static final long ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45L);
	private static final long ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
	public static final TObjectLongMap<UUID> archerSpeedCooldowns = new TObjectLongHashMap<UUID>();
	private final TObjectLongMap<UUID> archerJumpCooldowns = new TObjectLongHashMap<UUID>();
	private final HCF plugin;
	private static Random random = new Random();

	public ArcherClass(HCF plugin) {
		super("Archer", TimeUnit.SECONDS.toMillis(5L));
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if (((entity instanceof Player)) && ((damager instanceof Arrow))) {
			Arrow arrow = (Arrow) damager;
			ProjectileSource source = arrow.getShooter();
			if ((source instanceof Player)) {
				Player damaged = (Player) event.getEntity();
				Player shooter = (Player) source;
				PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
				if ((equipped == null) || (!equipped.equals(this))) {
					return;
				}
				if ((this.plugin.getTimerManager().archerTimer.getRemaining((Player) entity) == 0L)
						|| (this.plugin.getTimerManager().archerTimer.getRemaining((Player) entity) < TimeUnit.SECONDS
								.toMillis(5L))) {
					if ((this.plugin.getPvpClassManager().getEquippedClass(damaged) != null)
							&& (this.plugin.getPvpClassManager().getEquippedClass(damaged).equals(this))) {
						return;
					}
					double distance = shooter.getLocation().distance(damaged.getLocation());
					shooter.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&e[&9Arrow Range &e(&c" + String.format("%.1f", new Object[] { Double.valueOf(distance) })
									+ "&e)] " + "&6Marked " + damaged.getName() + " &6for 6 seconds. &9&l(1 heart)"));

					damaged.sendMessage(ChatColor.GOLD + "You were archer tagged by " + ChatColor.RED
							+ shooter.getName() + ChatColor.GOLD + " from " + ChatColor.RED
							+ String.format("%.1f", new Object[] { Double.valueOf(distance) }) + ChatColor.GOLD
							+ " blocks away.");
					TAGGED.put(damaged.getUniqueId(), shooter.getUniqueId());
					this.plugin.getTimerManager().archerTimer.setCooldown(damaged, damaged.getUniqueId(), TimeUnit.SECONDS.toMillis(6L), true);
					LeatherArmorMeta helmMeta = (LeatherArmorMeta) shooter.getInventory().getHelmet().getItemMeta();
					LeatherArmorMeta chestMeta = (LeatherArmorMeta) shooter.getInventory().getChestplate()
							.getItemMeta();
					LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) shooter.getInventory().getLeggings()
							.getItemMeta();
					LeatherArmorMeta bootsMeta = (LeatherArmorMeta) shooter.getInventory().getBoots().getItemMeta();
					Color green = Color.fromRGB(6717235);

					double r = random.nextDouble();
					r = random.nextDouble();
					if ((r <= 0.5D) && (helmMeta.getColor().equals(green)) && (chestMeta.getColor().equals(green))
							&& (leggingsMeta.getColor().equals(green)) && (bootsMeta.getColor().equals(green))) {
						damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
						shooter.sendMessage(ChatColor.GRAY + "Since your armor is green, you gave " + damaged.getName()
								+ " the poison effect for 6 seconds...");
						damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName()
								+ "'s armor is green, you were given the poison effect for 6 seconds...");
					}
					Color blue = Color.fromRGB(3361970);
					if ((r <= 0.5D) && (helmMeta.getColor().equals(blue)) && (chestMeta.getColor().equals(blue))
							&& (leggingsMeta.getColor().equals(blue)) && (bootsMeta.getColor().equals(blue))) {
						damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 0));
						shooter.sendMessage(ChatColor.GRAY + "Since your armor is blue, you gave " + damaged.getName()
								+ " the slowness effect for 6 seconds...");
						damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName()
								+ "'s armor is blue, you were given the slowness effect for 6 seconds...");
					}
					Color gray = Color.fromRGB(5000268);
					if ((r <= 0.5D) && (helmMeta.getColor().equals(gray)) && (chestMeta.getColor().equals(gray))
							&& (leggingsMeta.getColor().equals(gray)) && (bootsMeta.getColor().equals(gray))) {
						damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
						shooter.sendMessage(ChatColor.GRAY + "Since your armor is gray, you gave " + damaged.getName()
								+ " the blindness effect for 6 seconds...");
						damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName()
								+ "'s armor is gray, you were given the blindness effect for 6 seconds...");
					}
					Color black = Color.fromRGB(1644825);
					if ((r <= 0.2D) && (helmMeta.getColor().equals(black)) && (chestMeta.getColor().equals(black))
							&& (leggingsMeta.getColor().equals(black)) && (bootsMeta.getColor().equals(black))) {
						damaged.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 0));
						shooter.sendMessage(ChatColor.GRAY + "Since your armor is black, you gave " + damaged.getName()
								+ " the wither effect for 6 seconds...");
						damaged.sendMessage(ChatColor.GRAY + "Since " + shooter.getName()
								+ "'s armor is black, you were given the wither effect for 6 seconds...");
					}
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onArcherSpeedClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem())
				&& (event.getItem().getType() == Material.SUGAR)) {
			if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
				return;
			}
			Player player = event.getPlayer();
			UUID uuid = player.getUniqueId();
			long timestamp = this.archerSpeedCooldowns.get(uuid);
			long millis = System.currentTimeMillis();
			long remaining = timestamp == this.archerSpeedCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
			if (remaining > 0L) {
				player.sendMessage(ChatColor.RED + "Cannot use Speed Boost for another "
						+ DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
			} else {
				ItemStack stack = player.getItemInHand();
				if (stack.getAmount() == 1) {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				} else {
					stack.setAmount(stack.getAmount() - 1);
				}
				player.sendMessage(ChatColor.GREEN + "Speed 4 activated for 7 seconds.");

				this.plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_SPEED_EFFECT);
				this.archerSpeedCooldowns.put(event.getPlayer().getUniqueId(),
						System.currentTimeMillis() + ARCHER_SPEED_COOLDOWN_DELAY);
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onArcherJumpClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem())
				&& (event.getItem().getType() == Material.FEATHER)) {
			if (this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this) {
				return;
			}
			Player player = event.getPlayer();
			UUID uuid = player.getUniqueId();
			long timestamp = this.archerJumpCooldowns.get(uuid);
			long millis = System.currentTimeMillis();
			long remaining = timestamp == this.archerJumpCooldowns.getNoEntryValue() ? -1L : timestamp - millis;
			if (remaining > 0L) {
				player.sendMessage(ChatColor.RED + "Cannot use Jump Boost for another "
						+ DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
			} else {
				ItemStack stack = player.getItemInHand();
				if (stack.getAmount() == 1) {
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				} else {
					stack.setAmount(stack.getAmount() - 1);
				}
				player.sendMessage(ChatColor.GREEN + "Jump Boost 4 activated for 7 seconds.");

				this.plugin.getEffectRestorer().setRestoreEffect(player, ARCHER_JUMP_EFFECT);
				this.archerJumpCooldowns.put(event.getPlayer().getUniqueId(),
						System.currentTimeMillis() + ARCHER_JUMP_COOLDOWN_DELAY);
			}
		}
	}

	public boolean isApplicableFor(Player player) {
		PlayerInventory playerInventory = player.getInventory();
		ItemStack helmet = playerInventory.getHelmet();
		if ((helmet == null) || (helmet.getType() != Material.LEATHER_HELMET)) {
			return false;
		}
		ItemStack chestplate = playerInventory.getChestplate();
		if ((chestplate == null) || (chestplate.getType() != Material.LEATHER_CHESTPLATE)) {
			return false;
		}
		ItemStack leggings = playerInventory.getLeggings();
		if ((leggings == null) || (leggings.getType() != Material.LEATHER_LEGGINGS)) {
			return false;
		}
		ItemStack boots = playerInventory.getBoots();
		return (boots != null) && (boots.getType() == Material.LEATHER_BOOTS);
	}
}
