package me.rainny.reaper.args;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.doctordark.utils.InventoryUtils;
import com.doctordark.utils.ItemBuilder;
import com.doctordark.utils.other.chat.Lang;

import me.rainny.reaper.HCF;
import me.rainny.reaper.ymls.SettingsYML;

public class MapKitCommand
  implements CommandExecutor, TabCompleter, Listener
{
  private final Set<Inventory> tracking = new HashSet<Inventory>();
  
  public MapKitCommand(HCF plugin)
  {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (!(sender instanceof Player))
    {
      sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
      return true;
    }
    List<ItemStack> items = new ArrayList<ItemStack>();
    for (Map.Entry<Enchantment, Integer> entry : SettingsYML.ENCHANTMENT_LIMITS.entrySet()) {
      items.add(new ItemBuilder(Material.ENCHANTED_BOOK).displayName(ChatColor.GRAY + Lang.fromEnchantment((Enchantment)entry.getKey()) + ": " + ChatColor.GREEN + entry.getValue()).build());
    }
    for (Map.Entry<PotionType, Integer> entry : SettingsYML.POTION_LIMITS.entrySet()) {
      items.add(new ItemBuilder(new Potion((PotionType)entry.getKey()).toItemStack(1)).displayName(
        ChatColor.GRAY + WordUtils.capitalizeFully(((PotionType)entry.getKey()).name().replace('_', ' ')) + ": " + ChatColor.GREEN + entry.getValue()).build());
    }
    Player player = (Player)sender;
    Inventory inventory = Bukkit.createInventory(player, InventoryUtils.getSafestInventorySize(items.size()), "Railed HCF Map " + SettingsYML.MAP_NUMBER + " Kit");
    this.tracking.add(inventory);
    for (ItemStack item : items) {
      inventory.addItem(new ItemStack[] { item });
    }
    player.openInventory(inventory);
    return true;
  }
  
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
  {
    return Collections.emptyList();
  }
  
  @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
  public void onInventoryClick(InventoryClickEvent event)
  {
    if (this.tracking.contains(event.getInventory())) {
      event.setCancelled(true);
    }
  }
      }   

