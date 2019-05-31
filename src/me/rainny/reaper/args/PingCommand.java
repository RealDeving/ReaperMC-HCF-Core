package me.rainny.reaper.args;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.doctordark.utils.BukkitUtils;
import com.doctordark.utils.internal.com.doctordark.base.BaseConstants;

import me.rainny.reaper.ymls.LangYML;

public class PingCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if ((args.length > 0) && (sender.hasPermission(command.getPermission() + ".others"))) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + "/ping <player>");
                return true;
            }
            target = (Player) sender;
        }
        if ((target == null) || (!PingCommand.canSee(sender, target))) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] {
                args[0]
            }));
            return true;
        }
        if(target.equals(sender)) {
        	sender.sendMessage(LangYML.PING.replace("%ping%", "" + ((CraftPlayer) target).getHandle().ping));
        } else {
        	sender.sendMessage(LangYML.PING_OTHER.replaceAll("%player%", target.getName()).replaceAll("%ping%", "" + ((CraftPlayer) target).getHandle().ping));
        }
        return true;
    }


    public List < String > onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) && (sender.hasPermission(command.getPermission() + ".others")) ? null : Collections.emptyList();
    }
    public static boolean canSee(CommandSender sender, Player target) {
        return (target != null) && ((!(sender instanceof Player)) || (((Player) sender).canSee(target)));
    }
    
    public static int getPing(Player p) {
    	CraftPlayer craft = (CraftPlayer) p;
    	return craft.getHandle().ping;
    	
    }
    
    public static int getPing(CommandSender p) {
    	CraftPlayer craft = (CraftPlayer) p;
    	return craft.getHandle().ping;
    	
    }
    
}

