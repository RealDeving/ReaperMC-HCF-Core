package me.rainny.reaper.Tablist.tab.Provider;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import me.rainny.reaper.Tablist.tab.TabAdapter;
import me.rainny.reaper.Tablist.tab.TabTemplate;
import me.rainny.reaper.factionutils.FactionManager;
import me.rainny.reaper.factionutils.FactionMember;
import me.rainny.reaper.factionutils.struct.Role;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.ymls.SettingsYML;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class TabProvider implements TabAdapter {
	
	
    public TabTemplate getTemplate(Player p) {
        TabTemplate template = new TabTemplate();
        UUID id = p.getUniqueId();

        final int lives = HCF.getInstance().getDeathbanManager().getLives(id);
        final int balance = HCF.getInstance().getEconomyManager().getBalance(id);
        final int diamonds = HCF.getInstance().getConfig().getInt(id + ".Mined.Diamond");
        final int emeralds = HCF.getInstance().getConfig().getInt(id + ".Mined.Emerald");
        
        FactionManager fm = HCF.getInstance().getFactionManager();
        PlayerFaction faction = fm.getPlayerFaction(p.getUniqueId());
        
        //String FactionHQ = faction.getHome() == null ? "None" : faction.getHome().getBlockX() + ", " + faction.getHome().getBlockZ();
        
        //FIRST COLUMN
        template.left("§9Location§7:");
        template.left("§7(" + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockZ() + ") [" + getCardinalDirection(p) + "§7]");
        template.left("");
        template.left("§9Player Info§7:");
        template.left("§3 » §7Lives: §c" + lives);
        template.left("§3 » §7Balance: §c" + balance);
        template.left("§3 » §7Rank: §c" + getRank(p));
        template.left("");
        if(SettingsYML.KIT_MAP == true) {
        	template.left("§4Kitmap Info");
        	template.left("§3 » Kills§7: " + p.getStatistic(Statistic.PLAYER_KILLS));
        	template.left("§3 » Deaths§7: " + p.getStatistic(Statistic.DEATHS));
        }
        
        template.middle("§9ReaperMC");
        template.middle("");
        template.middle("");
        if(faction != null) {
        	template.middle("§9Faction Info§7:");
        	template.middle(" §3» Name: §a" + faction.getName());
        	template.middle(" §3» DTR: " + faction.getDtrColour().toString() + faction.getDeathsUntilRaidable());
        	if(faction.getHome() != null) {
        		template.middle(" §3» Home: §7(" + faction.getHome().getBlockX() + ", " + faction.getHome().getBlockZ() + ")");
        	} else {
        		template.middle(" §3» Home: §7None");
        	}
        	
        	template.middle("");
        	template.middle("§9Faction Members§7:");
        	for(UUID s : faction.getOnlineMembers().keySet()) {
        		FactionMember user = faction.getOnlineMembers().get(s);
        		if(user.getRole() == Role.LEADER) {
        			template.middle("§7**§a" + user.getName());
        		} else if(user.getRole() == Role.CAPTAIN) {
        			template.middle("§7*§a" + user.getName());
        		} else {
        			template.middle("§a" + user.getName());
        		}
        	}
        }
        
        template.right("§9Miner Info§7:");
        template.right("§bDiamonds: " + diamonds);
        template.right("§aEmeralds: " + emeralds);
        template.right("");      
        template.right("§9Border§7:");
        template.right("§3 » §7OverWorld: §c" + SettingsYML.BORDER_SIZES.get(World.Environment.NORMAL)); 
        template.right("§3 » §7Nether: §c" + SettingsYML.BORDER_SIZES.get(World.Environment.NETHER)); 
        template.right(""); 
        template.right("§9Online Players§7:");
        template.right("§3 » §7" + Bukkit.getOnlinePlayers().length);
        
        template.farRight("");
        template.farRight("");
        template.farRight("");
        template.farRight("");
        template.farRight("");
        template.farRight("§c§lWarning!");
        template.farRight("");
        template.farRight("");
        template.farRight("");
        template.farRight("");
        template.farRight("§aPlease use");
        template.farRight("§a1.7 for the");
        template.farRight("§aoptimal playing");
        template.farRight("§aexperience");
        
        return template;
    }
    
    
    private static String getDirection(double rot) {
        if (0.0D <= rot && rot < 22.5D) {
            return "§3W§7";
        } else if (22.5D <= rot && rot < 67.5D) {
            return "§9N§3W§7";
        } else if (67.5D <= rot && rot < 112.5D) {
            return "§3N§7";
        } else if (112.5D <= rot && rot < 157.5D) {
            return "§9N§3E§7";
        } else if (157.5D <= rot && rot < 202.5D) {
            return "§3E§7";
        } else if (202.5D <= rot && rot < 247.5D) {
            return "§9S§3E§7";
        } else if (247.5D <= rot && rot < 292.5D) {
            return "§3S§7";
        } else if (292.5D <= rot && rot < 337.5D) {
            return "§9S§3W§7";
        } else {
            return 337.5D <= rot && rot < 360.0D ? "§3W§7" : null;
        }
    }
    
    public static String getCardinalDirection(Player p) {
        double rot = (double)((p.getLocation().getYaw() - 90.0F) % 360.0F);
        if (rot < 0.0D) {
            rot += 360.0D;
        }

        return getDirection(rot);
    }
    
    public static String getRank(Player p) {
    	String[] rank1 = PermissionsEx.getUser(p).getGroupNames();
    	String rank2 = rank1[0];
		String rankfirst = rank2.substring(0, 1);
		rankfirst = rankfirst.toUpperCase();
		String rank = rankfirst + rank2.substring(1);
		return rank;
    }
    
}
