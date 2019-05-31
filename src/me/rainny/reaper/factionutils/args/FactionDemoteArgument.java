package me.rainny.reaper.factionutils.args;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.FactionMember;
import me.rainny.reaper.factionutils.struct.Relation;
import me.rainny.reaper.factionutils.struct.Role;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.PlayerFaction;

import java.util.*;

/**
 * Faction argument used to demote players to members in {@link Faction}s.
 */
public class FactionDemoteArgument extends CommandArgument {

    private final HCF plugin;

    public FactionDemoteArgument(HCF plugin) {
        super("demote", "Demotes a player to a member.", new String[] { "uncaptain", "delcaptain", "delofficer" });
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a officer to edit the faction roster.");
            return true;
        }

        FactionMember targetMember = playerFaction.getMember(args[1]);

        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "That player is not in your faction.");
            return true;
        }

        if (targetMember.getRole() != Role.CAPTAIN) {
            sender.sendMessage(ChatColor.RED + "You can only demote faction captains.");
            return true;
        }

        targetMember.setRole(Role.MEMBER);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + targetMember.getName() + ChatColor.GRAY + " has been demoted from a faction captain.");
        return true;
    }

    @SuppressWarnings("deprecation")
	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER)) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        Collection<UUID> keySet = playerFaction.getMembers().keySet();
        for (UUID entry : keySet) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
            String targetName = target.getName();
            if (targetName != null && playerFaction.getMember(target.getUniqueId()).getRole() == Role.CAPTAIN) {
                results.add(targetName);
            }
        }

        return results;
    }
}
