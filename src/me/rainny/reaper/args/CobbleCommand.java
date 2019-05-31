package me.rainny.reaper.args;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Sets;

import me.rainny.reaper.ymls.LangYML;


public class CobbleCommand implements Listener, CommandExecutor {

    public static Set<Player> disabled = Sets.newHashSet();

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LangYML.PLAYER_ONLY);
            return true;
        }
        Player player = (Player) sender;
        if (disabled.contains(player)) {
            disabled.remove(player);
            player.sendMessage(LangYML.COBBLE_DISABLED);
        } else {
            disabled.add(player);
            player.sendMessage(LangYML.COBBLE_ENABLED);
        }
            return true;
        }
    
        

        @EventHandler
        public void onPlayerPickup(PlayerQuitEvent event){
            disabled.remove(event.getPlayer());
        }
        @EventHandler
        public void onPlayerPickup(PlayerPickupItemEvent event){
            Material type = event.getItem().getItemStack().getType();
            if(type == Material.STONE || type == Material.COBBLESTONE){
                if(disabled.contains(event.getPlayer())){
                    event.setCancelled(true);
                }
            }
        }
    }
