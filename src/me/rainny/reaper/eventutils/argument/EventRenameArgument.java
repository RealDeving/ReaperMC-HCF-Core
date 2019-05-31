package me.rainny.reaper.eventutils.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.type.CapturableFaction;
import me.rainny.reaper.factionutils.type.EventFaction;
import me.rainny.reaper.factionutils.type.Faction;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link CommandArgument} used for renaming an {@link CapturableFaction}.
 */
public class EventRenameArgument extends CommandArgument {

    private final HCF plugin;

    public EventRenameArgument(HCF plugin) {
        super("rename", "Renames an event");
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <oldName> <newName>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Faction faction = plugin.getFactionManager().getFaction(args[2]);

        if (faction != null) {
            sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[2] + '.');
            return true;
        }

        faction = plugin.getFactionManager().getFaction(args[1]);

        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }

        String oldName = faction.getName();
        faction.setName(args[2], sender);

        sender.sendMessage(ChatColor.GRAY + "Renamed event " + ChatColor.WHITE + oldName + ChatColor.GRAY + " to " + ChatColor.WHITE + faction.getName() + ChatColor.GRAY + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        return plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
