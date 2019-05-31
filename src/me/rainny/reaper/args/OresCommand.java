package me.rainny.reaper.args;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.doctordark.utils.BetterItemBuilder;

import me.rainny.reaper.HCF;

public class OresCommand implements CommandExecutor, Listener {

	private HCF hcf;
	public OresCommand(HCF hcf) {
		this.hcf = hcf;
		Bukkit.getPluginManager().registerEvents(this, HCF.plugin);
	}
	
	String guiname = "§cStats";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}
		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
			return true;
		}
		Player p = (Player) sender;
		if(args.length == 0) {
			String uuid = p.getUniqueId().toString();
			if(hcf.getConfig().contains(uuid)) {
				openGUI(p);
			} else {
				p.sendMessage(ChatColor.RED + "You haven't mined anything yet!");
			}
		} else {
			if(args.length == 1) {
				Player target = Bukkit.getPlayer(args[0]);
				if(target == null) {
					p.sendMessage("§cThat player is not currently online.");
					return true;
				}
				String uuid = target.getUniqueId().toString();
				if(hcf.getConfig().contains(uuid)) {
					openGUI(p, target);
				} else {
					p.sendMessage(ChatColor.RED + target.getName() + " hasn't mined anything yet!");
				}
			}
		}
		return true;
	}
	
	public void openGUI(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, guiname);
		
		ItemStack kills = new BetterItemBuilder(Material.DIAMOND_SWORD, 1)
				.setName("§cKills: §7" + p.getStatistic(Statistic.PLAYER_KILLS))
				.toItemStack();
		inv.setItem(3, kills);
		
		ItemStack deaths = new BetterItemBuilder(Material.SKULL_ITEM)
				.setSkullOwner("skeleton")
				.setName("§cDeaths: §7" + p.getStatistic(Statistic.DEATHS))
				.toItemStack();
		inv.setItem(5, deaths);		

		ItemStack lapis = new BetterItemBuilder(Material.LAPIS_ORE)
				.setName("§cLapis: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Lapis"))
				.toItemStack();
		inv.setItem(11, lapis);
		
		ItemStack coal = new BetterItemBuilder(Material.COAL_ORE)
				.setName("§cCoal: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Coal"))
				.toItemStack();
		inv.setItem(12, coal);
		
		ItemStack redstone = new BetterItemBuilder(Material.REDSTONE_ORE)
				.setName("§cRedstone: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Redstone"))
				.toItemStack();
		inv.setItem(13, redstone);
		
		ItemStack gold = new BetterItemBuilder(Material.GOLD_ORE)
				.setName("§cGold: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Gold"))
				.toItemStack();
		inv.setItem(14, gold);

		ItemStack emerald = new BetterItemBuilder(Material.EMERALD_ORE)
				.setName("§cEmerald: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Emerald"))
				.toItemStack();
		inv.setItem(15, emerald);
		
		ItemStack iron = new BetterItemBuilder(Material.IRON_ORE)
				.setName("§cEmerald: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Emerald"))
				.toItemStack();
		inv.setItem(21, iron);
		
		ItemStack diamond = new BetterItemBuilder(Material.DIAMOND_ORE)
				.setName("§cDiamond: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Diamond"))
				.toItemStack();
		inv.setItem(22, diamond);
		
		ItemStack stone = new BetterItemBuilder(Material.STONE)
				.setName("§cStone: §7" + hcf.getConfig().getInt(p.getUniqueId() + ".Mined.Stone"))
				.toItemStack();
		inv.setItem(23, stone);
		
		p.openInventory(inv);
	}
	
	public void openGUI(Player p, Player target) {
		Inventory inv = Bukkit.createInventory(null, 27, guiname);
		
		ItemStack kills = new BetterItemBuilder(Material.DIAMOND_SWORD, 1)
				.setName("§cKills: §7" + target.getStatistic(Statistic.PLAYER_KILLS))
				.toItemStack();
		inv.setItem(3, kills);
		
		ItemStack deaths = new BetterItemBuilder(Material.SKULL_ITEM)
				.setSkullOwner("skeleton")
				.setName("§cDeaths: §7" + target.getStatistic(Statistic.DEATHS))
				.toItemStack();
		inv.setItem(5, deaths);		

		ItemStack lapis = new BetterItemBuilder(Material.LAPIS_ORE)
				.setName("§cLapis: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Lapis"))
				.toItemStack();
		inv.setItem(11, lapis);
		
		ItemStack coal = new BetterItemBuilder(Material.COAL_ORE)
				.setName("§cCoal: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Coal"))
				.toItemStack();
		inv.setItem(12, coal);
		
		ItemStack redstone = new BetterItemBuilder(Material.REDSTONE_ORE)
				.setName("§cRedstone: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Redstone"))
				.toItemStack();
		inv.setItem(13, redstone);
		
		ItemStack gold = new BetterItemBuilder(Material.GOLD_ORE)
				.setName("§cGold: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Gold"))
				.toItemStack();
		inv.setItem(14, gold);

		ItemStack emerald = new BetterItemBuilder(Material.EMERALD_ORE)
				.setName("§cEmerald: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Emerald"))
				.toItemStack();
		inv.setItem(15, emerald);
		
		ItemStack iron = new BetterItemBuilder(Material.IRON_ORE)
				.setName("§cEmerald: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Emerald"))
				.toItemStack();
		inv.setItem(21, iron);
		
		ItemStack diamond = new BetterItemBuilder(Material.DIAMOND_ORE)
				.setName("§cDiamond: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Diamond"))
				.toItemStack();
		inv.setItem(22, diamond);
		
		ItemStack stone = new BetterItemBuilder(Material.STONE)
				.setName("§cStone: §7" + hcf.getConfig().getInt(target.getUniqueId() + ".Mined.Stone"))
				.toItemStack();
		inv.setItem(23, stone);
		
		p.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(guiname)) {
        	e.setCancelled(true);
        }
	}

}
