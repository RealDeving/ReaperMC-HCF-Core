package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionUser;

import java.util.Collections;
import java.util.List;

/**
 * Command used to toggle the lightning strikes on death for a {@link Player}.
 */
public class ToggleLightningCommand implements CommandExecutor, TabExecutor {

            private final HCF plugin;

    public ToggleLightningCommand(HCF plugin) {
                this.plugin = plugin;
            }

            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                    return true;
                }

                FactionUser factionUser = plugin.getUserManager().getUser(((Player) sender).getUniqueId());
                boolean newShowLightning = !factionUser.isShowLightning();
                factionUser.setShowLightning(newShowLightning);

                sender.sendMessage(ChatColor.GRAY + "You will now " + (newShowLightning ? ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.GRAY + " to see lightning strikes on a players death.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
