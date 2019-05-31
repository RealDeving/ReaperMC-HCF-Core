package com.doctordark.utils.other.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.doctordark.utils.BukkitUtils;
import com.google.common.collect.ImmutableList;

public abstract class ArgumentExecutor implements CommandExecutor, TabCompleter {
    protected final List<CommandArgument> arguments;
    protected final String label;

    public ArgumentExecutor(final String label) {
        this.arguments = new ArrayList<CommandArgument>();
        this.label = label;
    }

    public boolean containsArgument(final CommandArgument argument) {
        return this.arguments.contains(argument);
    }

    public void addArgument(final CommandArgument argument) {
        this.arguments.add(argument);
    }

    public void removeArgument(final CommandArgument argument) {
        this.arguments.remove(argument);
    }

    public CommandArgument getArgument(final String id) {
        for (final CommandArgument argument : this.arguments) {
            final String name = argument.getName();
            if (name.equalsIgnoreCase(id) || Arrays.asList(argument.getAliases()).contains(id.toLowerCase())) {
                return argument;
            }
        }
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    public List<CommandArgument> getArguments() {
        return (List<CommandArgument>) ImmutableList.copyOf((Collection<CommandArgument>) this.arguments);
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
        	sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------*----------------------------------*--------");
            sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + WordUtils.capitalizeFully(this.label + ChatColor.RED + " Help"));
            for (final CommandArgument argument : this.arguments) {
                final String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " §f" + argument.getDescription() + '.');
                }
            }
        	sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------*----------------------------------*--------");
            return true;
        }
        final CommandArgument argument2 = this.getArgument(args[0]);
        final String permission2 = (argument2 == null) ? null : argument2.getPermission();
        if (argument2 == null || (permission2 != null && !sender.hasPermission(permission2))) {
            sender.sendMessage(ChatColor.RED + WordUtils.capitalizeFully(this.label) + "Error.");
            return true;
        }
        argument2.onCommand(sender, command, label, args);
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        List<String> results = new ArrayList<String>();
        if (args.length < 2) {
            for (final CommandArgument argument : this.arguments) {
                final String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    results.add(argument.getName());
                }
            }
        } else {
            final CommandArgument argument2 = this.getArgument(args[0]);
            if (argument2 == null) {
                return results;
            }
            final String permission2 = argument2.getPermission();
            if (permission2 == null || sender.hasPermission(permission2)) {
                results = argument2.onTabComplete(sender, command, label, args);
                if (results == null) {
                    return null;
                }
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }
}
