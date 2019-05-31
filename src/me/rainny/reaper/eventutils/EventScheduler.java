package me.rainny.reaper.eventutils;

import net.minecraft.util.com.google.common.primitives.Ints;

import org.bukkit.Bukkit;

import me.rainny.reaper.HCF;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class that can handle schedules for game events.
 */
public class EventScheduler {
	
	private static final String FILE_NAME = "event-schedules.txt";
	private static final long QUERY_DELAY;
	private final Map<LocalDateTime, String> scheduleMap;
	private final HCF plugin;
	private long lastQuery;

	public EventScheduler(final HCF plugin) {
		this.scheduleMap = new LinkedHashMap<LocalDateTime, String>();
		this.plugin = plugin;
		this.reloadSchedules();
	}

	private static LocalDateTime getFromString(final String input) {
		if (!input.contains(",")) {
			return null;
		}
		final String[] args = input.split(",");
		if (args.length != 5) {
			return null;
		}
		final Integer year = Ints.tryParse(args[0]);
		if (year == null) {
			return null;
		}
		final Integer month = Ints.tryParse(args[1]);
		if (month == null) {
			return null;
		}
		final Integer day = Ints.tryParse(args[2]);
		if (day == null) {
			return null;
		}
		final Integer hour = Ints.tryParse(args[3]);
		if (hour == null) {
			return null;
		}
		final Integer minute = Ints.tryParse(args[4]);
		if (minute == null) {
			return null;
		}
		return LocalDateTime.of(year, month, day, hour, minute);
	}

	private void reloadSchedules() {
		try (final BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(this.plugin.getDataFolder(), "event-schedules.txt")),
						StandardCharsets.UTF_8))) {
			String currentLine;
			while ((currentLine = bufferedReader.readLine()) != null) {
				if (currentLine.startsWith("#")) {
					continue;
				}
				currentLine = currentLine.trim();
				final String[] args = currentLine.split(":");
				if (args.length != 2) {
					continue;
				}
				final LocalDateTime localDateTime = getFromString(args[0]);
				if (localDateTime == null) {
					continue;
				}
				this.scheduleMap.put(localDateTime, args[1]);
			}
		} catch (FileNotFoundException ex2) {
			Bukkit.getConsoleSender().sendMessage("Could not find file " + FILE_NAME);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public Map<LocalDateTime, String> getScheduleMap() {
		final long millis = System.currentTimeMillis();
		if (millis - EventScheduler.QUERY_DELAY > this.lastQuery) {
			this.reloadSchedules();
			this.lastQuery = millis;
		}
		return this.scheduleMap;
	}

	static {
		QUERY_DELAY = TimeUnit.SECONDS.toMillis(60L);
	}
}
