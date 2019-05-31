package me.rainny.reaper.args;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.rainny.reaper.listener.Crowbar;
import me.rainny.reaper.ymls.LangYML;

public class GiveCrowbarCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args) {
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("crowgive")) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.GREEN + "Usage: /"+ label +  " <PlayerName>");
                return true;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target == null) {
                p.sendMessage(ChatColor.RED + "That player is not online! Try again later!");
                return true;
            }

            ItemStack stack = new Crowbar().getItemIfPresent();
            target.getInventory().addItem(new ItemStack[]{stack});
            target.sendMessage(LangYML.CROWBAR_GIVE.replaceAll("%player%", p.getName()));
            p.sendMessage(LangYML.CROWBAR_RECEIVED.replaceAll("%player%", sender.getName()));
            return true;
        }
        return false;
    }
}
