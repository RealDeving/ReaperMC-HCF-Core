package me.rainny.reaper.scoreboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import com.Rainnny.Reaper.StaffMode;
import com.doctordark.utils.BukkitUtils;
import com.doctordark.utils.DurationFormatter;
import com.doctordark.utils.other.Cooldown;

import me.rainny.reaper.HCF;
import me.rainny.reaper.armors.PvpClass;
import me.rainny.reaper.armors.archer.ArcherClass;
import me.rainny.reaper.armors.bard.BardClass;
import me.rainny.reaper.endoftheworld.EotwHandler;
import me.rainny.reaper.eventutils.EventTimer;
import me.rainny.reaper.eventutils.tracker.ConquestTracker;
import me.rainny.reaper.factionutils.type.ConquestFaction;
import me.rainny.reaper.factionutils.type.EventFaction;
import me.rainny.reaper.factionutils.type.KothFaction;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.listener.DateTimeFormats;
import me.rainny.reaper.scoreboard.providers.ScoreboardEntryProvider;
import me.rainny.reaper.startoftheworld.SotwCommand;
import me.rainny.reaper.startoftheworld.SotwTimer;
import me.rainny.reaper.timer.PlayerTimer;
import me.rainny.reaper.timer.Timer;
import me.rainny.reaper.timer.type.CombatTimer;
import me.rainny.reaper.ymls.SettingsYML;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class ScoreboardProvider implements ScoreboardEntryProvider {

	List<String> template = new ArrayList<String>();
	List<String> empty = new ArrayList<String>();
	private HCF plugin;

	public ScoreboardProvider(final HCF plugin) {
		this.plugin = plugin;
		template.add("§8§m-----------------");
		template.add("§8§m-----------------");
	}

	protected static final String STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);

	@Override
	public List<String> getScoreboardEntries(final Player p) {
		final List<String> lines = new ArrayList<String>();
		lines.add("§8§m-----------------");
		if (StaffMode.instance.isInStaffMode(p)) {
			int staffSize = 0;
        	for (Player staff : Bukkit.getServer().getOnlinePlayers()) {
        		if (staff.hasPermission("core.staff") || staff.hasPermission("base.staff")) {
        			staffSize++;
        		}
        	}
			lines.add("§9§lStaff Mode: ");
			lines.add("§3 » §7Online: §c" + Bukkit.getOnlinePlayers().length + " §7(" + staffSize + "§7)");
			lines.add("§3 » §7Gamemode: §c" + WordUtils.capitalize(p.getGameMode().toString().toLowerCase()));
			lines.add("§3 » §7Vanish: " + (StaffMode.instance.isVanished(p) ? "§atrue" : "§cfalse"));
			lines.add("§3 » §7TPS: §"
					+ (Bukkit.spigot().getTPS()[0] > 18 ? "a" : (Bukkit.spigot().getTPS()[0]) > 15 ? "e" : "c")
					+ new DecimalFormat("#.##").format(Bukkit.spigot().getTPS()[0]));
			lines.add("§8§m-----------------");
		}
		if (SettingsYML.KIT_MAP) {
			lines.add("§4Kills§7: §f" + p.getStatistic(Statistic.PLAYER_KILLS));
			lines.add("§4Deaths§7: §f" + p.getStatistic(Statistic.DEATHS));
			lines.add("§4Balance§7: §f$" + HCF.getPlugin().getEconomyManager().getBalance(p.getUniqueId()));
		}
		SotwTimer.SotwRunnable sotwRunnable = plugin.getSotwTimer().getSotwRunnable();
		if (sotwRunnable != null) {
			if (SotwCommand.sotwEnabled.contains(p.getUniqueId())) {
				lines.add("§a§m§lSOTW§8: " + ChatColor.RED
						+ DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true));
			} else {
				lines.add("§a§lSOTW§8: " + ChatColor.RED
						+ DurationFormatter.getRemaining(sotwRunnable.getRemaining(), true));
			}
		}
		EotwHandler.EotwRunnable eotwRunnable = plugin.getEotwHandler().getRunnable();
		if (eotwRunnable != null) {
			long remaining = eotwRunnable.getMillisUntilStarting();
			if (remaining > 0L) {
				lines.add("§a§lEOTW §6starts in " + DurationFormatter.getRemaining(remaining, true));
			} else if ((remaining = eotwRunnable.getMillisUntilCappable()) > 0L) {
				lines.add("§c§lEOTW §6 cappable in " + DurationFormatter.getRemaining(remaining, true));
			}
		}
		PvpClass pvpClass = plugin.getPvpClassManager().getEquippedClass(p);
		if (pvpClass != null) {
			if (pvpClass instanceof BardClass) {
				BardClass bardClass = (BardClass) pvpClass;
				lines.add("§a§lBard Energy: " + ChatColor.RED + handleBardFormat(bardClass.getEnergyMillis(p), true));
				long remaining2 = bardClass.getRemainingBuffDelay(p);
				if (remaining2 > 0L) {
					lines.add("§a§lBard Effect" + ChatColor.GREEN + ": " + ChatColor.RED
							+ DurationFormatter.getRemaining(remaining2, true));
				}
				long remaining = bardClass.getRemainingBuffDelay(p);
				if (remaining > 0) {
				}
			}
			if (pvpClass instanceof ArcherClass) {
				UUID uuid = p.getUniqueId();
				long timestamp = ArcherClass.archerSpeedCooldowns.get(uuid);
				long millis = System.currentTimeMillis();
				long remaining3 = (timestamp == ArcherClass.archerSpeedCooldowns.getNoEntryValue()) ? -1L
						: (timestamp - millis);
				if (remaining3 > 0L) {
					lines.add("§6§lDelay§7: " + DurationFormatter.getRemaining(remaining3, true));
				}
			}
		}
		Collection<Timer> timers = (Collection<Timer>) this.plugin.getTimerManager().getTimers();
		for (Timer timer : timers) {
			if (timer instanceof PlayerTimer) {
				PlayerTimer playerTimer = (PlayerTimer) timer;
				long remaining4 = playerTimer.getRemaining(p);
				if (remaining4 <= 0L) {
					continue;
				}
				String timerName = playerTimer.getName();
				if (timerName.length() > 14) {
					timerName = timerName.substring(0, timerName.length());
				}
				lines.add(playerTimer.getScoreboardPrefix() + String.valueOf(timerName) + ChatColor.GRAY + ": "
						+ ChatColor.RED
						+ ((timer instanceof CombatTimer) ? DurationFormatter.getRemaining(remaining4, false)
								: DurationFormatter.getRemaining(remaining4, true)));
			}
		}
		if (Cooldown.isInCooldown(p.getUniqueId(), "fisherman")) {
			lines.add("§6§lFishermen:§r §7" + Cooldown.getTimeLeft(p.getUniqueId(), "fisherman"));
		}
		EventTimer eventTimer = plugin.getTimerManager().getEventTimer();
		List<String> conquestLines = null;
		EventFaction eventFaction = eventTimer.getEventFaction();
		if (eventFaction instanceof KothFaction) {
			lines.add("§4Active Events");
			lines.add(eventTimer.getScoreboardPrefix() + eventFaction.getScoreboardName() + ChatColor.GRAY + ": "
					+ ChatColor.WHITE + DurationFormatter.getRemaining(eventTimer.getRemaining(), true));
		} else if (eventFaction instanceof ConquestFaction) {
			ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
			conquestLines = new ArrayList<>();
			conquestLines.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Conquest Event");
			conquestLines.add(
					" " + ChatColor.GOLD.toString() + conquestFaction.getRed().getScoreboardRemaining() + ChatColor.GOLD
							+ " | " + ChatColor.GRAY.toString() + conquestFaction.getYellow().getScoreboardRemaining());
			conquestLines.add(" " + ChatColor.GREEN.toString() + conquestFaction.getGreen().getScoreboardRemaining()
					+ ChatColor.GOLD + " | " + ChatColor.RESET + ChatColor.AQUA.toString()
					+ conquestFaction.getBlue().getScoreboardRemaining());
			// Show the top 3 factions next.
			ConquestTracker conquestTracker = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
			int count = 0;
			for (Map.Entry<PlayerFaction, Integer> entry : conquestTracker.getFactionPointsMap().entrySet()) {
				String factionName = entry.getKey().getName();
				if (factionName.length() > 14)
					factionName = factionName.substring(0, 14);
				for (int i = 0; i < 3; i++) {
					conquestLines.add(ChatColor.GOLD.toString() + ChatColor.BOLD + " " + count++ + ". " + ChatColor.GRAY
							+ factionName + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());
				}
				if (++count == 3)
					break;
			}
		}
		lines.add("§8§m--------------------");
		if (Compare(lines)) {
			return empty;
		}
		return lines;
	}

	private static String handleBardFormat(long millis, boolean trailingZero) {
		return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get()
				.format(millis * 0.001);
	}

	public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("00.0");
		}
	};

	public boolean Compare(List<String> sb) {
		String[] list1 = sb.toArray(new String[0]);
		String[] list2 = template.toArray(new String[0]);
		return Arrays.equals(list1, list2);
	}

}
