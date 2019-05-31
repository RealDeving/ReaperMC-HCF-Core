package me.rainny.reaper.factionutils.args;

import com.doctordark.utils.JavaUtils;
import com.doctordark.utils.other.command.CommandArgument;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import me.rainny.reaper.factionutils.FactionExecutor;
import me.rainny.reaper.factionutils.type.Faction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Faction argument used to show help on how to use {@link Faction}s.
 */
public class FactionHelpArgument extends CommandArgument {

    private static final int HELP_PER_PAGE = 8;

    private ImmutableMultimap<Integer, String> pages;
    private final FactionExecutor executor;

    public FactionHelpArgument(FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            showPage(sender, label, 1);
            return true;
        }

        Integer page = JavaUtils.tryParseInt(args[1]);

        if (page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }

        showPage(sender, label, page);
        return true;
    }

    private void showPage(CommandSender sender, String label, int pageNumber) {
        // Create the multimap.
        if (pages == null) {
            boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
            Multimap<Integer, String> pages = ArrayListMultimap.create();
            for (CommandArgument argument : executor.getArguments()) {
                if (argument == this)
                    continue;

                // Check the permission and if the player can access.
                String permission = argument.getPermission();
                if (permission != null && !sender.hasPermission(permission))
                    continue;
                if (argument.isPlayerOnly() && !isPlayer)
                    continue;

                count++;
                pages.get(val).add("§3» /" + label + ' ' + argument.getName() + " §f§m--§r §7" + argument.getDescription());
                if (count % HELP_PER_PAGE == 0) {
                    val++;
                }
            }

            // Finally assign it.
            this.pages = ImmutableMultimap.copyOf(pages);
        }

        int totalPageCount = (pages.size() / HELP_PER_PAGE) + 1;

        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }

        if (pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "There are only " + totalPageCount + " pages.");
            return;
        }

    	sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------");
        sender.sendMessage("§9§lFactions Help " + "" + ChatColor.GRAY + "(Page " + pageNumber + '/' + totalPageCount + ')');
        sender.sendMessage("");
        for (String message : pages.get(pageNumber)) {
            sender.sendMessage("  " + message);
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "§9» §7You are currently on " + ChatColor.DARK_AQUA + "Page " + pageNumber + '/' + totalPageCount + ChatColor.GRAY + '.');
        sender.sendMessage(ChatColor.GRAY + "§9» §7To view other pages, use " + ChatColor.DARK_AQUA + '/' + label + ' ' + getName() + " <page#>" + ChatColor.GRAY + '.');
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------");
    }
}
