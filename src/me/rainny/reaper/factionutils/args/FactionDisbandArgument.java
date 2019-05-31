package me.rainny.reaper.factionutils.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.struct.Role;
import me.rainny.reaper.factionutils.type.PlayerFaction;

public class FactionDisbandArgument extends CommandArgument {

    private final HCF plugin;

    public FactionDisbandArgument(HCF plugin) {
        super("disband", "Disband your faction.");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        if (playerFaction.isRaidable() && !plugin.getEotwHandler().isEndOfTheWorld()) {
            sender.sendMessage(ChatColor.RED + "You cannot disband your faction while it is raidable.");
            return true;
        }

        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a leader to disband the faction.");
            return true;
        }

        Player p = (Player) sender;
        Integer factionbalance = playerFaction.getBalance();
        if(factionbalance > 0) {
        	p.sendMessage("§a$" + playerFaction.getBalance() + " §7has been added to your balance.");
            plugin.getEconomyManager().addBalance(p.getUniqueId(), playerFaction.getBalance());
        }
        plugin.getFactionManager().removeFaction(playerFaction, sender);
        return true;
    }
}
