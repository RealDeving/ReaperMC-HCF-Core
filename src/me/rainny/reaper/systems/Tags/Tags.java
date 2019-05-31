package me.rainny.reaper.systems.Tags;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.doctordark.utils.BetterItemBuilder;

import me.rainny.reaper.HCF;
import me.rainny.reaper.sql.SQLUtil;

public class Tags implements CommandExecutor, Listener {

	private static HashMap<String, String> playertags = new HashMap<>();
	public static HashMap<UUID, String> tags1 = new HashMap<>();

	// CONFIRUABLE THINGS
	String permission = "hcf.tag.";
	static String itemname = "&c%tagname% &4» &r%tag%";
	static String exitgui = "§cClick to exit GUI";
	static String currenttag = "§cYour current tag §4» §r";
	static String removetag = "§7Click to remove current tag";
	static String setprefix = "&7Your current prefix has been set to &r%tag%";

	public static HCF main;

	private static String guiname;
	private static List<String> purchasedlore = new ArrayList<>();
	private static List<String> notpurchasedlore = new ArrayList<>();
	SQLUtil sql;

	public Tags(HCF main) {
		(sql = main.sql).createTable("tags", Arrays.asList("varchar(255)", "varchar(255)"),
				Arrays.asList("tagname", "tagvalue"));
		sql.createTable("tagdata", Arrays.asList("varchar(255)", "varchar(255)"), Arrays.asList("uuid", "tagname"));
		loadTags();
		notpurchasedlore.add("§7You can purchase this tag @ §cstore.reapermc.xyz");
		purchasedlore.add("§7Right/Left Click to activate this tag");
		guiname = "§cTags";
		HCF.getInstance().getCommand("tags").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, HCF.getInstance());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String c, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cYou must be a player to run this command.");
		}
		Player p = (Player) sender;
		if (args.length == 0) {
			loadTags();
			openGUI(p);
		}
		return true;
	}

	public static void openGUI(Player p) {
		int count = 0;
		Inventory inv = Bukkit.createInventory(null, 54, guiname);
		ItemStack line = new BetterItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.GRAY).toItemStack();
		inv.setItem(36, line);
		inv.setItem(37, line);
		inv.setItem(38, line);
		inv.setItem(39, line);
		inv.setItem(40, line);
		inv.setItem(41, line);
		inv.setItem(42, line);
		inv.setItem(43, line);
		inv.setItem(44, line);
		ItemStack leave = new BetterItemBuilder(Material.IRON_DOOR).setName(exitgui).toItemStack();
		inv.setItem(48, leave);
		ItemStack current = new BetterItemBuilder(Material.NETHER_STAR)
				.setName("§cYour Current Tag: " + getPrefixName(p)).toItemStack();
		inv.setItem(49, current);
		ItemStack none = new BetterItemBuilder(Material.WOOL).setName(removetag).toItemStack();
		inv.setItem(50, none);
		for (String s : playertags.keySet()) {
			if (p.hasPermission("hcf.tags." + s)) {
				ItemStack item = new BetterItemBuilder(Material.WOOL, 1).setDyeColor(DyeColor.GREEN)
						.setName(ChatColor.translateAlternateColorCodes('&', itemname
								.replace("%tagname%", WordUtils.capitalize(s)).replaceAll("%tag%", playertags.get(s))))
						.setLore(purchasedlore).toItemStack();
				inv.setItem(count, item);
				count++;
			} else {
				ItemStack item = new BetterItemBuilder(Material.WOOL, 1).setDyeColor(DyeColor.RED)
						.setName(ChatColor.translateAlternateColorCodes('&',
								"§c" + WordUtils.capitalize(s) + " §4 » " + playertags.get(s)))
						.setLore(notpurchasedlore).toItemStack();
				inv.setItem(count, item);
				count++;
			}
		}
		p.openInventory(inv);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		tags1.put(p.getUniqueId(), getPrefixName(p));
		sql.createValuesInKeys("tagdata", Arrays.asList("uuid", "tagname"),
				Arrays.asList(p.getUniqueId().toString(), "none"));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (tags1.containsKey(p.getUniqueId())) {
			tags1.remove(p.getUniqueId());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		if (e.getInventory().getName().equalsIgnoreCase(guiname)) {
			e.setCancelled(true);
			if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType() == Material.AIR)
					|| (!e.getCurrentItem().hasItemMeta())) {
				player.closeInventory();
				return;
			}
			if (item.getItemMeta().getDisplayName()
					.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', exitgui))) {
				player.closeInventory();
			}
			if (item.getItemMeta().getDisplayName()
					.contains(ChatColor.translateAlternateColorCodes('&', removetag))) {
				setPrefix(player, "none");
				player.closeInventory();
			}
			for (String s : playertags.keySet()) {
				String thisitemname = ChatColor.translateAlternateColorCodes('&',
						itemname.replace("%tagname%", WordUtils.capitalize(s)).replaceAll("%tag%", playertags.get(s)));
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(thisitemname)) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&',
							setprefix.replaceAll("%tag%", playertags.get(s))));
					setPrefix(player, s);
				}
			}
		}
	}

	public void loadTags() {
		try {
			ResultSet rs = HCF.getInstance().sql.getValues("tags");
			while (rs.next()) {
				playertags.put(rs.getString("tagname"), rs.getString("tagvalue"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getPrefixName(Player p) {
		String name = String
				.valueOf(HCF.getInstance().sql.getValue("tagdata", p.getUniqueId().toString(), "uuid", "tagname"));
		if (name != null) {
			return name;
		} else {
			return "none";
		}
	}

	public static String getPrefix(Player p) {
		String prefixname = getPrefixName(p);
		if (prefixname == "none") {
			return "";
		} else {
			return playertags.get(prefixname);
		}
	}

	public static String getChatTag(Player p) {
		String tagname = tags1.get(p.getUniqueId());
		if (tagname != "none") {
			String tag = playertags.get(tagname);
			return tag + " ";
		} else {
			return "";
		}
	}

	public void setPrefix(Player p, String prefix) {
		sql.updateValueInKey("tagdata", "uuid", "tagname", p.getUniqueId().toString(), prefix);
		tags1.put(p.getUniqueId(), prefix);
	}

}
