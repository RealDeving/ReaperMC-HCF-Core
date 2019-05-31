package me.rainny.reaper.factionutils.args;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.event.FactionRelationCreateEvent;
import me.rainny.reaper.factionutils.struct.Relation;
import me.rainny.reaper.factionutils.struct.Role;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.ymls.SettingsYML;

import java.util.*;

/**
 * Faction argument used to request or accept ally {@link Relation} invitations from a {@link Faction}.
 */
public class FactionAllyArgument extends CommandArgument {

    private static final Relation RELATION = Relation.ALLY;

    private final HCF plugin;

    public FactionAllyArgument(HCF plugin) {
        super("ally", "Make an ally pact with other factions.", new String[] { "alliance" });
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <factionName>";
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        if (SettingsYML.MAX_ALLIES_PER_FACTION <= 0) {
            sender.sendMessage(ChatColor.RED + "Sorry but allies are disabled this map.");
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

        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be an officer to send ally requests.");
            return true;
        }

        Faction containingFaction = plugin.getFactionManager().getContainingFaction(args[1]);

        if (!(containingFaction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }

        PlayerFaction targetFaction = (PlayerFaction) containingFaction;

        if (playerFaction == targetFaction) {
            sender.sendMessage(ChatColor.RED + "You cannot send " + RELATION.getDisplayName() + ChatColor.RED + " requests to your own faction.");
            return true;
        }

        Collection<UUID> allied = playerFaction.getAllied();

        if (allied.size() >= SettingsYML.MAX_ALLIES_PER_FACTION) {
            sender.sendMessage(ChatColor.RED + "Your faction already has reached the alliance limit, which is " + SettingsYML.MAX_ALLIES_PER_FACTION + '.');
            return true;
        }

        if (targetFaction.getAllied().size() >= SettingsYML.MAX_ALLIES_PER_FACTION) {
            sender.sendMessage(targetFaction.getDisplayName(sender) + ChatColor.RED + " has reached their maximum alliance limit, which is " + SettingsYML.MAX_ALLIES_PER_FACTION + '.');
            return true;
        }

        if (allied.contains(targetFaction.getUniqueID())) {
            sender.sendMessage(ChatColor.RED + "Your faction already is " + RELATION.getDisplayName() + 'd' + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED
                    + '.');

            return true;
        }

        // Their faction has already requested us, lets' accept.
        if (targetFaction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null) {
            FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, targetFaction, RELATION);
            Bukkit.getPluginManager().callEvent(event);

            targetFaction.getRelations().put(playerFaction.getUniqueID(), RELATION);
            targetFaction.broadcast(ChatColor.GRAY + "Your faction is now " + RELATION.getDisplayName() + 'd' + ChatColor.GRAY + " with " + playerFaction.getDisplayName(targetFaction)
                    + ChatColor.GRAY + '.');

            playerFaction.getRelations().put(targetFaction.getUniqueID(), RELATION);
            playerFaction.broadcast(ChatColor.GRAY + "Your faction is now " + RELATION.getDisplayName() + 'd' + ChatColor.GRAY + " with " + targetFaction.getDisplayName(playerFaction)
                    + ChatColor.GRAY + '.');

            return true;
        }

        if (playerFaction.getRequestedRelations().putIfAbsent(targetFaction.getUniqueID(), RELATION) != null) {
            sender.sendMessage(ChatColor.GRAY + "Your faction has already requested to " + RELATION.getDisplayName() + ChatColor.GRAY + " with " + targetFaction.getDisplayName(playerFaction)
                    + ChatColor.GRAY + '.');

            return true;
        }

        // Handle the request.
        playerFaction.broadcast(targetFaction.getDisplayName(playerFaction) + ChatColor.GRAY + " were informed that you wish to be " + RELATION.getDisplayName() + ChatColor.GRAY + '.');
        targetFaction.broadcast(playerFaction.getDisplayName(targetFaction) + ChatColor.GRAY + " has sent a request to be " + RELATION.getDisplayName() + ChatColor.GRAY + ". Use "
                + SettingsYML.ALLY_COLOUR + "/faction " + getName() + ' ' + playerFaction.getName() + ChatColor.GRAY + " to accept.");

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
        if (playerFaction == null) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(player) && player.canSee(target) && !results.contains(target.getName())) {
                Faction targetFaction = plugin.getFactionManager().getPlayerFaction(target);
                if (targetFaction != null && playerFaction != targetFaction) {
                    if (playerFaction.getRequestedRelations().get(targetFaction.getUniqueID()) != RELATION && playerFaction.getRelations().get(targetFaction.getUniqueID()) != RELATION) {
                        results.add(targetFaction.getName());
                    }
                }
            }
        }

        return results;
    }
}
