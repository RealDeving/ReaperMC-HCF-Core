package me.rainny.reaper.bosses.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.doctordark.utils.ItemBuilder;

public class BossDeathListener implements Listener {

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		try {
			if (e.getEntity().getCustomName().equals(ChatColor.RED + ChatColor.BOLD.toString() + "Grim Reaper")) {
				Player p = (Player) e.getEntity().getKiller();
				Bukkit.broadcastMessage(ChatColor.GREEN + ChatColor.BOLD.toString()
						+ "The Grim Reaper has been slain by " + e.getEntity().getKiller().getName());
				if (e.getEntity().getKiller() instanceof Player) {
					p.sendMessage(ChatColor.GREEN + "You have slained §4§lThe Grim Reaper" + ChatColor.GREEN
							+ " you will be rewarded with the §4§lGrim Reaper's Scythe§4!");
					ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.DAMAGE_ALL, 3)
							.displayName("&4&ki&4&lGrim Reaper's Scythe&4&ki&4")
							.lore(ChatColor.translateAlternateColorCodes('&',
									"&4&o" + p.getName() + " killed the &4&l&oGrim Reaper&4&o!"))
							.build();
					e.getEntity().getLocation().getWorld().dropItemNaturally(e.getEntity().getLocation(), item);
				}
			}
		} catch (NullPointerException xe) {
		}

	}
}
