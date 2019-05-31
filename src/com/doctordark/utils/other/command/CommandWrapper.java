package com.doctordark.utils.other.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.doctordark.utils.BukkitUtils;

public class CommandWrapper implements CommandExecutor, TabCompleter {
    private final Collection<CommandArgument> arguments;

    public CommandWrapper(final Collection<CommandArgument> arguments) {
        this.arguments = arguments;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            printUsage(sender, label, this.arguments);
            return true;
        }
        final CommandArgument argument = matchArgument(args[0], sender, this.arguments);
        if (argument == null) {
            printUsage(sender, label, this.arguments);
            return true;
        }
        return argument.onCommand(sender, command, label, args);
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        List<String> results;
        if (args.length == 1) {
            results = getAccessibleArgumentNames(sender, this.arguments);
        } else {
            final CommandArgument argument = matchArgument(args[0], sender, this.arguments);
            if (argument == null) {
                return Collections.emptyList();
            }
            results = argument.onTabComplete(sender, command, label, args);
            if (results == null) {
                return null;
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }

    public static void printUsage(final CommandSender sender, final String label, final Collection<CommandArgument> arguments) {
        sender.sendMessage(ChatColor.DARK_AQUA + "*** " + WordUtils.capitalizeFully(label) + " Help ***");
        for (final CommandArgument argument : arguments) {
            final String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription());
            }
        }
    }

    public static CommandArgument matchArgument(final String id, final CommandSender sender, final Collection<CommandArgument> arguments) {
        for (final CommandArgument argument : arguments) {
            final String permission = argument.getPermission();
            if ((permission == null || sender.hasPermission(permission)) && (argument.getName().equalsIgnoreCase(id) || Arrays.asList(argument.getAliases()).contains(id))) {
                return argument;
            }
        }
        return null;
    }

    public static List<String> getAccessibleArgumentNames(final CommandSender sender, final Collection<CommandArgument> arguments) {
        final List<String> results = new ArrayList<String>();
        for (final CommandArgument argument : arguments) {
            final String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                results.add(argument.getName());
            }
        }
        return results;
    }

    @SuppressWarnings("serial")
	public static class ArgumentComparator implements Comparator<CommandArgument>, Serializable {
        @Override
        public int compare(final CommandArgument primaryArgument, final CommandArgument secondaryArgument) {
            return secondaryArgument.getName().compareTo(primaryArgument.getName());
        }
    }
}
