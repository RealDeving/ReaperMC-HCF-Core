package me.rainny.reaper.listener;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilRepairEvent;
import org.bukkit.inventory.ItemStack;

public class UnRepairableListener implements Listener
{
    @EventHandler
    public void onRepair(final PrepareAnvilRepairEvent e) {
        if (e.getInventory().getContents() == null) {
            return;
        }
        for (final ItemStack itemStack : e.getInventory().getContents()) {
            if (!itemStack.hasItemMeta()) {
                return;
            }
            if (!itemStack.getItemMeta().hasLore()) {
                return;
            }
            if (itemStack.getItemMeta().getLore() == null) {
                return;
            }
            for (final String lore : itemStack.getItemMeta().getLore()) {
                final String fixedLore = ChatColor.stripColor(lore.toLowerCase());
                if (fixedLore.contains("no repair") || fixedLore.contains("unrepairable") || fixedLore.contains("norepair") || fixedLore.contains("nofix") || fixedLore.contains("no fix")) {
                    e.setCancelled(true);
                    e.setResult(new ItemStack(Material.AIR));
                    e.getRepairer().closeInventory();
                    ((Player)e.getRepairer()).sendMessage(ChatColor.RED + "This item cannot be repaired.");
                }
            }
        }
    }
}
