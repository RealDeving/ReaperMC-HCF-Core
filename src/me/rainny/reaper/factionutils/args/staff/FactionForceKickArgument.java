package me.rainny.reaper.factionutils.args.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionMember;
import me.rainny.reaper.factionutils.struct.Role;
import me.rainny.reaper.factionutils.type.PlayerFaction;

import java.util.Collections;
import java.util.List;

public class FactionForceKickArgument extends CommandArgument {

    private final HCF plugin;

    public FactionForceKickArgument(HCF plugin) {
        super("forcekick", "Forcefully kick a player from their faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        PlayerFaction playerFaction = plugin.getFactionManager().getContainingPlayerFaction(args[1]);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }

        FactionMember factionMember = playerFaction.getMember(args[1]);

        if (factionMember == null) {
            sender.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }

        if (factionMember.getRole() == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You cannot forcefully kick faction leaders. Use /f forceremove instead.");
            return true;
        }

        if (playerFaction.removeMember(sender, null, factionMember.getUniqueId(), true, true)) {
            playerFaction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + factionMember.getName() + " has been forcefully kicked by " + sender.getName() + '.');
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 2 ? null : Collections.<String> emptyList();
    }
}
