package me.rainny.reaper.eventutils.argument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.doctordark.utils.JavaUtils;
import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.eventutils.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An {@link CommandArgument} used to delete a loot table for an {@link EventType}.
 */
public class EventDelLootTableArgument extends CommandArgument {

    private final HCF plugin;

    public EventDelLootTableArgument(HCF plugin) {
        super("delloottable", "Deletes a loot table at a specific slot for a type");
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <eventType> [size (multiple of 9)]";
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

        EventType eventType = EventType.getByDisplayName(args[1]);

        if (eventType == null) {
            sender.sendMessage(ChatColor.RED + "There is not an event type named " + args[1] + '.');
            return true;
        }

        Integer index = JavaUtils.tryParseInt(args[2]);

        if (index == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }

        List<Inventory> inventories = plugin.getKeyManager().getEventKey().getInventories(eventType);
        int size = inventories.size();

        if (index < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot edit an inventory less than 1.");
            return true;
        }

        if (index > size) {
            sender.sendMessage(ChatColor.RED + "There are only " + size + " possible loot inventories for " + eventType.getDisplayName() + ChatColor.RED + '.');
            return true;
        }

        int removedIndex = --index;
        inventories.remove(removedIndex);
        sender.sendMessage(ChatColor.GRAY + "Removed inventory for " + eventType.getDisplayName() + " at index " + removedIndex + '.');

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }

        EventType[] eventTypes = EventType.values();
        List<String> results = new ArrayList<>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            results.add(eventType.name());
        }

        return results;
    }
}
