package me.rainny.reaper.args;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

import me.rainny.reaper.listener.ColorUtils;
import me.rainny.reaper.listener.DeathListener;

import org.apache.commons.lang.*;

public class RefundCommand implements CommandExecutor {
    public boolean onCommand(final CommandSender cs, final Command cmd, final String s, final String[] args) {
        final String Usage = ChatColor.RED + "/" + s + " <playerName> <reason>";
        if (!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.RED + "You must be a player");
            return true;
        }
        final Player p = (Player)cs;
        if (args.length < 2) {
            cs.sendMessage(Usage);
            return true;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.RED + "Player must be online");
            return true;
        }
        final Player target = Bukkit.getPlayer(args[0]);
        if (DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId())) {
            target.getInventory().setContents((ItemStack[])DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
            target.getInventory().setArmorContents((ItemStack[])DeathListener.PlayerArmorContents.get(target.getUniqueId()));
            final String reason = StringUtils.join((Object[])args, ' ', 1,  args.length);
            Command.broadcastCommandMessage((CommandSender)p, ColorUtils.translate("&8[&2-&8] &f&o" + target.getName() + "&a for &f&o" + reason + "&a."));
            DeathListener.PlayerArmorContents.remove(target.getUniqueId());
            DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
            return true;
        }
        p.sendMessage(ChatColor.RED + "Player was already refunded items");
        return false;
    }
}
