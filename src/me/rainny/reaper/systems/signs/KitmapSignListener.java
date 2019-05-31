package me.rainny.reaper.systems.signs;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitmapSignListener implements Listener {

	private Map<UUID, Long> nextClick = new HashMap<UUID, Long>();
	public static Map<String, Kit> kits = new TreeMap<String, Kit>(String.CASE_INSENSITIVE_ORDER);

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.nextClick.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onSignUse(PlayerInteractEvent event) {
		Sign sign;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getState() instanceof Sign
				&& (sign = (Sign) block.getState()).getLine(0).equals("§9- Kit -")) {
			if (this.nextClick.containsKey(player.getUniqueId())) {
				long nextClick = this.nextClick.get(player.getUniqueId());
				if (System.currentTimeMillis() < nextClick) {
					return;
				}
			}
			this.nextClick.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1L));
			String clazz = sign.getLine(1).toLowerCase();
			if (player.hasPermission("kitsigns." + sign.getLine(1).toLowerCase())) {
				if (!kits.containsKey(clazz)) {
					player.sendMessage("§aImproper kit defined, please contact an administrator.");
				} else {
					Kit kit = kits.get(clazz);
					kit.giveTo(player);
					player.setHealth(20.0);
					player.setFireTicks(0);
					player.setFoodLevel(20);
					player.setSaturation(20.0f);
					player.sendMessage("§aInventory updated with kit " + clazz);
				}
			} else {
				player.sendMessage("§cYou do not have access to this kit.");
			}
		}
	}

	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		Player player = event.getPlayer();
		String line = event.getLine(0);
		if (line.equals("- Kit -")) {
			if (!player.hasPermission("kitmap.createsign")) {
				event.setCancelled(true);
			} else {
				event.setLine(0, "§9- Kit -");
				player.sendMessage("§aCreated KitMap sign");
			}
		}
	}

	public static class Kit {

		private ItemStack helmet;
		private ItemStack chest;
		private ItemStack legs;
		private ItemStack boots;
		private ItemStack[] contents = new ItemStack[36];
		private static final ItemStack SPLASH_HEATH = new ItemStack(Material.POTION, 1, (short) 16421);

		public ItemStack getHelmet() {
			return this.helmet;
		}

		public void setHelmet(ItemStack helmet) {
			this.helmet = helmet;
		}

		public ItemStack getChest() {
			return this.chest;
		}

		public void setChest(ItemStack chest) {
			this.chest = chest;
		}

		public ItemStack getLegs() {
			return this.legs;
		}

		public void setLegs(ItemStack legs) {
			this.legs = legs;
		}

		public ItemStack getBoots() {
			return this.boots;
		}

		public void setBoots(ItemStack boots) {
			this.boots = boots;
		}

		public void addItem(int slot, ItemStack item) {
			if (slot < 0 || slot >= 36) {
				throw new IllegalArgumentException("Invalid inventory slot provided, must be between 0 and 36");
			}
			this.contents[slot] = item == null ? null : item.clone();
		}

		public ItemStack[] getContents() {
			return (ItemStack[]) this.contents.clone();
		}

		public void giveTo(Player player) {
			PlayerInventory inventory = player.getInventory();
			inventory.clear();
			inventory.setHelmet(this.helmet == null ? null : this.helmet.clone());
			inventory.setChestplate(this.chest == null ? null : this.chest.clone());
			inventory.setLeggings(this.legs == null ? null : this.legs.clone());
			inventory.setBoots(this.boots == null ? null : this.boots.clone());
			int i = 0;
			while (i < this.contents.length) {
				ItemStack item = this.contents[i];
				inventory.setItem(i, item == null ? null : item.clone());
				++i;
			}
			while (inventory.firstEmpty() != -1) {
				inventory.addItem(new ItemStack[] { SPLASH_HEATH.clone() });
			}
			player.updateInventory();
		}

	}

}
