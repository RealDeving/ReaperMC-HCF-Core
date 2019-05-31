package me.rainny.reaper.bosses.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.rainny.reaper.HCF;
import me.rainny.reaper.bosses.particle.ParticleEffect;

public class ReaperSpawn implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if(strings.length == 0) {
            Zombie zom = player.getWorld().spawn(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 20, player.getLocation().getZ()), Zombie.class);
            zom.setVillager(false);
            zom.setBaby(false);
            zom.setMaxHealth((double)150);
            zom.setHealth((double)150);
            zom.setCustomName(ChatColor.RED + ChatColor.BOLD.toString() + "Grim Reaper");
            zom.setCustomNameVisible(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    zom.setFireTicks(0);
                }
            }.runTaskTimer(HCF.getInstance(), 0, 1);
            new BukkitRunnable() {
                int count = 0;
                @SuppressWarnings("deprecation")
				public void run() {
                    count++;
                    if(count == 30) {
                        this.cancel();
                        try {
                            ParticleEffect.EXPLODE.sendToPlayers(Bukkit.getOnlinePlayers(), zom.getLocation(), 2, 0, 3, 0, 70);
                            ParticleEffect.BLOCK_CRACK.sendToPlayers(Bukkit.getOnlinePlayers(), zom.getLocation(), 5, 8, 3, 0, 80);
                            Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "WARNING");
                            Bukkit.broadcastMessage(ChatColor.RED + "The Grim Reaper has been unleased");
                            Bukkit.broadcastMessage(ChatColor.RED + "We will need players to fight till THE DEATH!");
                        }
                        catch (Exception xe1) {
                            xe1.printStackTrace();
                        }
                    }
                    else {
                        try {
                            ParticleEffect.FIREWORKS_SPARK.sendToPlayers(Bukkit.getOnlinePlayers(), zom.getLocation(), 1,0,1,0,50);
                            ParticleEffect.RED_DUST.sendToPlayers(Bukkit.getOnlinePlayers(), zom.getLocation(), 0, 0, 0, 0, 100);
                        } catch (Exception xe) {
                            xe.printStackTrace();
                        }
                    }
                }
            }.runTaskTimer(HCF.getInstance(), 0,1);



            EntityEquipment equip =  zom.getEquipment();
            equip.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
            equip.getItemInHand().addEnchantment(Enchantment.KNOCKBACK, 2);

            equip.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            equip.getHelmet().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

            equip.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            equip.getChestplate().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

            equip.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
            equip.getLeggings().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

            equip.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
            equip.getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

        }
        return false;
    }
}
