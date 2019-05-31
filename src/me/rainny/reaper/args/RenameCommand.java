package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.rainny.reaper.HCF;

public class RenameCommand implements CommandExecutor {
    

    public RenameCommand(HCF plugin) {
    }
    
    public String getUsage(String label) {
        return String.valueOf('/') + label + ' ' + "<name>";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(!(sender instanceof Player)) {
    		sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
    		return true;
    	}
    	Player p = (Player) sender;
    	if(args.length < 1) {
			p.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c- &7You should use &e&&7's for colors!"));
		} else {
			ItemStack hand = p.getItemInHand();
			if(hand.getType() != Material.AIR) {
				String name = "";
				 for(int i = 0; i != args.length; i++)
					 name += args[i] + " ";
				
				ItemMeta meta = hand.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&r" + name));
				hand.setItemMeta(meta);
				
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have successfully renamed your &c" + hand.getType().name().toLowerCase() + " &7to &r" + name));
			} else {
				p.sendMessage(ChatColor.RED + "You cannot rename Air!");
			}
		}
    	return true;
    }

}
