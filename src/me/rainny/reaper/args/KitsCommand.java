package me.rainny.reaper.args;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.doctordark.utils.Config;

import me.rainny.reaper.HCF;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

@SuppressWarnings("deprecation")
public class KitsCommand implements CommandExecutor, Listener {

	private HCF hcf;
	public KitsCommand(HCF hcf) {
		this.hcf = hcf;
		inventory = Bukkit.createInventory(null, 45, ChatColor.RED + "Railed Donator Kits");
	}
	
	public HashMap<UUID, Map<String, Long>> cooldowns = new HashMap<UUID, Map<String, Long>>();
    Config config = new Config((HCF) hcf, "timers");
    private long day = 86400000;
    private Inventory inventory;
    private int protection = 2;
    private int sharpness = 1;
    private int unbreaking = 3;
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("kit")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This is for players only!");
				return true;
			}
			if(args.length == 0) {
				Player player = (Player) sender;
				this.openKitsInventory(player);
				sender.sendMessage(ChatColor.GREEN + "Opened kits!");
			}
		}
		return false;
	}
	
	private void openKitsInventory(Player player) {
		
		inventory.setItem(11, titaniumKitGlass(player));
		inventory.setItem(13, diamondKitGlass(player));
		inventory.setItem(15, bardKitGlass(player));
		inventory.setItem(29, archerKitGlass(player));
		inventory.setItem(31, minerKitGlass(player));
		inventory.setItem(33, starterKitGlass(player));
		
		player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 20, 1);
		
		player.openInventory(inventory);
	}
	
	private void closeInventory(Player player) {
		player.closeInventory();
	}
	
	@SuppressWarnings("unchecked")
	private void giveTitaniumKit(Player player) {
		if(player.hasPermission("riots.titanium")) {
			if(cooldowns.containsKey(player.getUniqueId())) {
				if(cooldowns.get(player.getUniqueId()).containsKey("TitaniumKit")) {
					this.closeInventory(player);
					player.sendMessage(ChatColor.RED + "You cannot receive this kit for " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(cooldowns.get(player.getUniqueId()).get("TitaniumKit"), true, true) + ChatColor.RED + "!");
				} else {
					cooldowns.put(player.getUniqueId(), (Map<String, Long>) new AbstractMap.SimpleEntry<String, Long>("TitanKit", day * 3));
					ItemStack helm = new ItemStack(Material.DIAMOND_HELMET, 1);
					ItemMeta helmMeta = helm.getItemMeta();
					
					helmMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lTitanium &7Kit"));
					helm.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
					helm.addUnsafeEnchantment(Enchantment.DURABILITY, unbreaking);
					helm.setItemMeta(helmMeta);
				   
					ItemStack cp = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
					ItemMeta cpMeta = cp.getItemMeta();
					
					cpMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lTitanium &7Kit"));
					cp.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
					cp.addUnsafeEnchantment(Enchantment.DURABILITY, unbreaking);
					cp.setItemMeta(cpMeta);
					
					ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
					ItemMeta legsMeta = legs.getItemMeta();
					
					legsMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lTitanium &7Kit"));
					legs.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
					legs.addUnsafeEnchantment(Enchantment.DURABILITY, unbreaking);
					legs.setItemMeta(legsMeta);
					
					ItemStack booties = new ItemStack(Material.DIAMOND_BOOTS, 1);
					ItemMeta bootiesMeta = booties.getItemMeta();
					
					bootiesMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lTitanium &7Kit"));
					booties.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
					booties.addUnsafeEnchantment(Enchantment.DURABILITY, unbreaking);
					booties.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);
					booties.setItemMeta(bootiesMeta);
					
					ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
					ItemMeta swordMeta = sword.getItemMeta();
					
					sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, sharpness);
					sword.addUnsafeEnchantment(Enchantment.DURABILITY, unbreaking);
					swordMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&lTitanium &7Kit"));
					
					sword.setItemMeta(swordMeta);
					
					ItemStack pearls = new ItemStack(Material.ENDER_PEARL, 16);
					
					player.getInventory().addItem(helm);
					player.getInventory().addItem(cp);
					player.getInventory().addItem(legs);
					player.getInventory().addItem(booties);
					player.getInventory().addItem(sword);
					player.getInventory().addItem(pearls);
					player.updateInventory();
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You do not have access to this kit! Purchase it at store.ReaperMC.net!");
		}
	}
	
	private ItemStack titaniumKitGlass(Player player) {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 14);
		ItemMeta meta = stack.getItemMeta();
		addGlow(stack);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4Titanium &7Kit"));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7This is a kit contains:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&fGod Diamond kit!"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &cPurchase the Titanium Rank from store.ReaperMC.net"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7CoolDown: &c3 Days"));
		if(cooldowns.containsKey(player.getUniqueId())) {
			if(cooldowns.get(player.getUniqueId()).containsKey("TitaniumKit")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &c" + DurationFormatUtils.formatDurationWords(cooldowns.get(player.getUniqueId()).get("TitaniumKit"), true, true)));
			} else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
			}
		} else {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private ItemStack diamondKitGlass(Player player) {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 9);
		ItemMeta meta = stack.getItemMeta();
		addGlow(stack);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bDiamond &7Kit"));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7This kit contains:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&fGod Diamond kit with Looting!"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &cPurchase this from store.ReaperMC.net"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7CoolDown: &c3 Days"));
		if(cooldowns.containsKey(player.getUniqueId())) {
			if(cooldowns.get(player.getUniqueId()).containsKey("DiamondKit")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &c" + DurationFormatUtils.formatDurationWords(cooldowns.get(player.getUniqueId()).get("DiamondKit"), true, true)));
			} else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
			}
		} else {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private ItemStack bardKitGlass(Player player) {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 4);
		ItemMeta meta = stack.getItemMeta();
		addGlow(stack);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eBard &7Kit"));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7This kit contains:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&fGod Bard kit with Bard Materials!"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &cPurchase this from store.ReaperMC.net"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7CoolDown: &c3 Days"));
		if(cooldowns.containsKey(player.getUniqueId())) {
			if(cooldowns.get(player.getUniqueId()).containsKey("BardKit")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &c" + DurationFormatUtils.formatDurationWords(cooldowns.get(player.getUniqueId()).get("BardKit"), true, true)));
			} else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
			}
		} else {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private ItemStack archerKitGlass(Player player) {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 6);
		ItemMeta meta = stack.getItemMeta();
		addGlow(stack);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dArcher &7Kit"));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7This kit contains:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&fGod Archer kit with Archer Materials!"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &cPurchase this from store.ReaperMC.net"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7CoolDown: &c3 Days"));
		if(cooldowns.containsKey(player.getUniqueId())) {
			if(cooldowns.get(player.getUniqueId()).containsKey("ArcherKit")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &c" + DurationFormatUtils.formatDurationWords(cooldowns.get(player.getUniqueId()).get("ArcherKit"), true, true)));
			} else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
			}
		} else {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private ItemStack minerKitGlass(Player player) {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 6);
		ItemMeta meta = stack.getItemMeta();
		addGlow(stack);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&9Miner &7Kit"));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7This kit contains:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&fGod Miner Set with Diamond Pickaxe and Anvils!"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &cPurchase this from store.ReaperMC.net"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7CoolDown: &c3 Days"));
		if(cooldowns.containsKey(player.getUniqueId())) {
			if(cooldowns.get(player.getUniqueId()).containsKey("ArcherKit")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &c" + DurationFormatUtils.formatDurationWords(cooldowns.get(player.getUniqueId()).get("ArcherKit"), true, true)));
			} else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
			}
		} else {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private ItemStack starterKitGlass(Player player) {
		ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte) 13);
		ItemMeta meta = stack.getItemMeta();
		addGlow(stack);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fStarter &7Kit"));
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7This kit contains:"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&fLeather Set with an Iron Sword, Iron Pickaxe, and Food!"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &cPurchase this from store.ReaperMC.net"));
		lore.add("");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7CoolDown: &c3 Days"));
		if(cooldowns.containsKey(player.getUniqueId())) {
			if(cooldowns.get(player.getUniqueId()).containsKey("StarterKit")) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &c" + DurationFormatUtils.formatDurationWords(cooldowns.get(player.getUniqueId()).get("StarterKit"), true, true)));
			} else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
			}
		} else {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &7Available in: &aNow"));
		}
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack addGlow(ItemStack item){
        net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
	
	@EventHandler
	public void onInteract(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		
		if(!ChatColor.stripColor(e.getClickedInventory().getTitle()).equalsIgnoreCase("ReaperMC Donator Kits")) {
			return;
		}
		
		if(e.getInventory().getSize() != 45) {
			return;
		}
		
		if(e.getClick() != ClickType.LEFT) {
			return;
		}
		
		if(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Titanium Kit")) {
			this.giveTitaniumKit(player);
		}
	}
	
	public void onDisable() {
		config.set("kits", cooldowns);
	}
	
	@SuppressWarnings("unchecked")
	public void onEnable() {
		if(config.get("kits") != null) {
			cooldowns = (HashMap<UUID, Map<String, Long>>) config.get("kits");
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(hcf, new Runnable() {
			public void run() {
				if(cooldowns.size() > 0) {
					for(UUID uuid : cooldowns.keySet()) {
			            for(String strings : cooldowns.get(uuid).keySet()) {
			            	if(cooldowns.get(uuid).get(strings) <= 0L) {
			            		cooldowns.get(uuid).remove(strings);
			            	} else {
			            		cooldowns.put(uuid, (Map<String, Long>) new AbstractMap.SimpleEntry<String, Long>(strings, cooldowns.get(uuid).get(strings) - 1000L));
			            	}
			            }
		            }
				}
			}
		}, 20L, 20L);
	}

}
