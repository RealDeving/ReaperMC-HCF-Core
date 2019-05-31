package me.rainny.reaper.listener.fixes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorFixListener implements Listener
{
  private static List<Material> ALLOWED = Arrays.asList(new Material[] { Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS });
  









  public ArmorFixListener() {}
  









  @EventHandler
  public void onItemDamage(PlayerItemDamageEvent event)
  {
    ItemStack stack = event.getItem();
    if ((stack != null) && (ALLOWED.contains(stack.getType())) && 
      (ThreadLocalRandom.current().nextInt(3) != 0)) {
      event.setCancelled(true);
    }
  }
}
