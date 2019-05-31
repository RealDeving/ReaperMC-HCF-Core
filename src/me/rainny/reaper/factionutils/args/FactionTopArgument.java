package me.rainny.reaper.factionutils.args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.doctordark.utils.BukkitUtils;
import com.doctordark.utils.JavaUtils;
import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.type.Faction;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class FactionTopArgument extends CommandArgument {

	private static final int MAX_FACTIONS_PER_PAGE = 10;

	private final HCF plugin;

	public FactionTopArgument(HCF plugin) {
		super("top", "See the top factions.");
		this.plugin = plugin;
		this.aliases = new String[] { "toplist" };
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		final Integer page;
		if (args.length < 2) {
			page = 1;
		} else {
			page = JavaUtils.tryParseInt(args[1]);
			if (page == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
				return true;
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				showList(page, label, sender);
			}
		}.runTaskAsynchronously(plugin);
		return true;
	}

	private void showList(final int pageNumber, final String label, final CommandSender sender) {
		if (pageNumber < 1) {
			sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
			return;
		}

		// Store a map of factions to their online player count.
		HashMap<PlayerFaction, Integer> factions = new HashMap<PlayerFaction, Integer>();
		for (Faction fac : plugin.getFactionManager().getFactions()) {
			if (fac instanceof PlayerFaction)
				factions.put((PlayerFaction) fac, ((PlayerFaction) fac).getPoints());
		}

		Map<Integer, List<BaseComponent[]>> pages = new HashMap<>();
		Arrays.sort(factions.entrySet().toArray(), new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Object i1, Object i2) {
				return ((Map.Entry<PlayerFaction, Integer>) i2).getValue()
						.compareTo(((Map.Entry<PlayerFaction, Integer>) i1).getValue());
			}
		});

		for (PlayerFaction playerFaction : factions.keySet()) {
			int currentPage = pages.size();

			List<BaseComponent[]> results = pages.get(currentPage);
			if (results == null || results.size() >= MAX_FACTIONS_PER_PAGE) {
				pages.put(++currentPage, results = new ArrayList<>(MAX_FACTIONS_PER_PAGE));
			}

			String displayName = playerFaction.getDisplayName(sender);

			int index = results.size() + (currentPage > 1 ? (currentPage - 1) * MAX_FACTIONS_PER_PAGE : 0) + 1;
			ComponentBuilder builder = new ComponentBuilder("  " + index + ". ")
					.color(net.md_5.bungee.api.ChatColor.WHITE);
			builder.append(displayName).color(net.md_5.bungee.api.ChatColor.RED)
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							'/' + label + " show " + playerFaction.getName()))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder(ChatColor.GRAY + "Click to view " + displayName + ChatColor.GRAY + '.')
									.create()));
			// Show points
			builder.append(" Points: " + playerFaction.getPoints(), ComponentBuilder.FormatRetention.FORMATTING)
					.color(net.md_5.bungee.api.ChatColor.GRAY);

			results.add(builder.create());
		}

		int maxPages = pages.size();

		if (pageNumber > maxPages) {
			sender.sendMessage(ChatColor.RED + "There "
					+ (maxPages == 1 ? "is only " + maxPages + " page" : "are only " + maxPages + " pages") + ".");
			return;
		}

		sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(
				ChatColor.RED + " Faction Top " + ChatColor.GRAY + "(" + "Page " + pageNumber + '/' + maxPages + ')');

		Player player = sender instanceof Player ? (Player) sender : null;
		Collection<BaseComponent[]> components = pages.get(pageNumber);
		for (BaseComponent[] component : components) {
			if (component == null)
				continue;
			if (player != null) {
				player.spigot().sendMessage(component);
			} else {
				sender.sendMessage(TextComponent.toPlainText(component));
			}
		}

		sender.sendMessage(ChatColor.GRAY + " You are currently on " + ChatColor.WHITE + "Page " + pageNumber + '/'
				+ maxPages + ChatColor.GOLD + '.');
		sender.sendMessage(ChatColor.GRAY + " To view other pages, use " + ChatColor.RED + '/' + label + ' ' + getName()
				+ " <page#>" + ChatColor.GRAY + '.');
		sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}

}
