package me.rainny.reaper.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.rainny.reaper.ymls.SettingsYML;

public class BorderListener implements Listener
{
    
    public static boolean isWithinBorder(final Location location) {
        final int borderSize = SettingsYML.BORDER_SIZES.get(location.getWorld().getEnvironment());
        return Math.abs(location.getBlockX()) <= borderSize && Math.abs(location.getBlockZ()) <= borderSize;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreaturePreSpawn(final CreatureSpawnEvent event) {
        if (!isWithinBorder(event.getLocation())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketFillEvent event) {
        if (!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot fill buckets past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot empty buckets past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (!isWithinBorder(to) && isWithinBorder(from)) {
            final Player player = event.getPlayer();
            player.sendMessage(ChatColor.RED + "You cannot go past the border.");
            event.setTo(from);
            final Entity vehicle = player.getVehicle();
            if (vehicle != null) {
                vehicle.eject();
                vehicle.teleport(from);
                vehicle.setPassenger((Entity)player);
            }
        }
    }
    
    }
