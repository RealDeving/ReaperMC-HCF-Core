package me.rainny.reaper.ymls;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.doctordark.utils.BetterConfig;

import me.rainny.reaper.HCF;
import net.md_5.bungee.api.ChatColor;

public class LangYML {
	
	public static String PLAYER_ONLY;
	public static String COBBLE_ENABLED;
	public static String COBBLE_DISABLED;
	public static String FROZEN;
	public static String UNFROZEN;
	public static String UNABLE_FREEZE;
	public static String UNFROZE_PLAYER;
	public static String FROZE_PLAYER;
	public static String CROWBAR_GIVE;
	public static String CROWBAR_RECEIVED;
	public static String LOG_OUT;
	public static String LOGGING_OUT;
	public static String PING;
	public static String PING_OTHER;
	public static String PLAYTIME;
	public static String NO_RECLAIM;
	public static String KITMAP_RECLAIM;
	public static String ALREADY_RECLAIMED;
	
	public static List<String> COORDS;
	public static List<String> FROZEN_MESSAGE;
	public static List<String> HELP;
	public static List<String> LFF;
	public static List<String> STARTED_RECORDING;
	public static List<String> STOPPED_RECORDING;
	
	public static FileConfiguration config;
	
	public static void init(HCF plugin){
		BetterConfig betterconfig = new BetterConfig(HCF.getPlugin(), "messages.yml", null);
		betterconfig.saveDefaultConfig();
		config = betterconfig.getConfiguration();
		
		
		PLAYER_ONLY = ChatColor.translateAlternateColorCodes('&', config.getString("PLAYER_ONLY"));
		COBBLE_ENABLED = color(config.getString("COBBLE_ENABLED"));
		COBBLE_DISABLED = color(config.getString("COBBLE_DISABLED"));
		COORDS = color(config.getStringList("COORDS"));
		FROZEN = color(config.getString("FROZEN"));
		UNFROZEN = color(config.getString("UNFROZEN"));
		UNABLE_FREEZE = color(config.getString("UNABLE_FREEZE"));
		UNFROZE_PLAYER = color(config.getString("UNFROZE_PLAYER"));
		FROZE_PLAYER = color(config.getString("FROZE_PLAYER"));
		FROZEN_MESSAGE = replace(color(config.getStringList("FROZEN_MESSAGE")), "%square%", "\u2588");
		CROWBAR_GIVE = color(config.getString("CROWBAR_GIVE"));
		CROWBAR_RECEIVED = color(config.getString("CROWBAR_RECEIVED"));
		HELP = replace(color(config.getStringList("HELP")), "%doublearrow%", "»");
		LOG_OUT = color(config.getString("LOG_OUT"));
		LOGGING_OUT = color(config.getString("LOGGING_OUT"));
		LFF = color(config.getStringList("LFF"));
		PING = color(config.getString("PING"));
		PING_OTHER = color(config.getString("PING_OTHER"));
		PLAYTIME = color(config.getString("PLAYTIME"));
		NO_RECLAIM = color(config.getString("NO_RECLAIM"));
		KITMAP_RECLAIM = color(config.getString("KITMAP_RECLAIM"));
		ALREADY_RECLAIMED = color(config.getString("ALREADY_RECLAIMED"));
		STARTED_RECORDING = color(config.getStringList("STARTED_RECORDING"));
		STOPPED_RECORDING = color(config.getStringList("STOPPED_RECORDING"));
	}
	
	public static String color(String s) {
		
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static List < String > color(List < String > text) {
        List < String > messages = new ArrayList < String > ();
        for (String string: text) {
            messages.add(color(string));
        }
        return messages;
    }
	
	public static List<String> replace(List<String> text, String oldChar, String newChar) {
        List < String > messages = new ArrayList < String > ();
        for (String string: text) {
            messages.add(string.replaceAll(oldChar, newChar));
        }
        return messages;
    }


}