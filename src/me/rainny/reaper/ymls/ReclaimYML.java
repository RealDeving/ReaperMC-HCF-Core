package me.rainny.reaper.ymls;

import org.bukkit.plugin.java.JavaPlugin;

import com.doctordark.utils.BetterConfig;

import me.rainny.reaper.HCF;

public class ReclaimYML {

	public static BetterConfig reclaimsettings;
	public static BetterConfig reclaimusers;
	
	public static void init(JavaPlugin plugin) {
		reclaimsettings = new BetterConfig(HCF.getPlugin(), "reclaim-settings.yml", null);
		reclaimusers = new BetterConfig(HCF.getPlugin(), "reclaim-users.yml", null);
		
		reclaimsettings.saveDefaultConfig();
		reclaimusers.saveDefaultConfig();
	}
	 
	
	
}
