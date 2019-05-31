package me.rainny.reaper.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import me.rainny.reaper.HCF;

public class MineListener implements Listener {
	
	private HCF hcf;
	public MineListener(Plugin plugin) {
		this.hcf = (HCF) plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlock();
		String uuid = p.getUniqueId().toString();
		if(e.isCancelled()) {
			return;
		}
		if(block.getType().equals(Material.DIAMOND_ORE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 1);
				hcf.getConfig().set(uuid + ".Mined.Gold", 0);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 0);
				hcf.getConfig().set(uuid + ".Mined.Coal", 0);
				hcf.getConfig().set(uuid + ".Mined.Redstone",0);
				hcf.getConfig().set(uuid + ".Mined.Iron", 0);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 0);
				hcf.getConfig().set(uuid + ".Mined.Stone", 0);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Diamond", hcf.getConfig().getInt(uuid + ".Mined.Diamond") + 1);
				hcf.saveConfig();
			}
		}
		if(block.getType().equals(Material.GOLD_ORE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 0);
				hcf.getConfig().set(uuid + ".Mined.Gold", 1);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 0);
				hcf.getConfig().set(uuid + ".Mined.Coal", 0);
				hcf.getConfig().set(uuid + ".Mined.Redstone",0);
				hcf.getConfig().set(uuid + ".Mined.Iron", 0);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 0);
				hcf.getConfig().set(uuid + ".Mined.Stone", 0);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Gold", hcf.getConfig().getInt(uuid + ".Mined.Gold") + 1);
				hcf.saveConfig();
			}
		}
		if(block.getType().equals(Material.EMERALD_ORE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 0);
				hcf.getConfig().set(uuid + ".Mined.Gold", 0);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 1);
				hcf.getConfig().set(uuid + ".Mined.Coal", 0);
				hcf.getConfig().set(uuid + ".Mined.Redstone",0);
				hcf.getConfig().set(uuid + ".Mined.Iron", 0);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 0);
				hcf.getConfig().set(uuid + ".Mined.Stone", 0);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Emerald", hcf.getConfig().getInt(uuid + ".Mined.Emerald") + 1);
				hcf.saveConfig();
			}
		}
		if(block.getType().equals(Material.COAL_ORE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 0);
				hcf.getConfig().set(uuid + ".Mined.Gold", 0);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 0);
				hcf.getConfig().set(uuid + ".Mined.Coal", 1);
				hcf.getConfig().set(uuid + ".Mined.Redstone",0);
				hcf.getConfig().set(uuid + ".Mined.Iron", 0);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 0);
				hcf.getConfig().set(uuid + ".Mined.Stone", 0);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Coal", hcf.getConfig().getInt(uuid + ".Mined.Coal") + 1);
				hcf.saveConfig();
			}
		}
		if(block.getType().equals(Material.REDSTONE_ORE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 0);
				hcf.getConfig().set(uuid + ".Mined.Gold", 0);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 0);
				hcf.getConfig().set(uuid + ".Mined.Coal", 0);
				hcf.getConfig().set(uuid + ".Mined.Redstone",1);
				hcf.getConfig().set(uuid + ".Mined.Iron", 0);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 0);
				hcf.getConfig().set(uuid + ".Mined.Stone", 0);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Redstone", hcf.getConfig().getInt(uuid + ".Mined.Redstone") + 1);
				hcf.saveConfig();
			}
		}
		if(block.getType().equals(Material.IRON_ORE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 0);
				hcf.getConfig().set(uuid + ".Mined.Gold", 0);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 0);
				hcf.getConfig().set(uuid + ".Mined.Coal", 0);
				hcf.getConfig().set(uuid + ".Mined.Redstone",0);
				hcf.getConfig().set(uuid + ".Mined.Iron", 1);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 0);
				hcf.getConfig().set(uuid + ".Mined.Stone", 0);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Iron", hcf.getConfig().getInt(uuid + ".Mined.Iron") + 1);
				hcf.saveConfig();
			}
		}
		if(block.getType().equals(Material.LAPIS_ORE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 0);
				hcf.getConfig().set(uuid + ".Mined.Gold", 0);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 0);
				hcf.getConfig().set(uuid + ".Mined.Coal", 0);
				hcf.getConfig().set(uuid + ".Mined.Redstone",0);
				hcf.getConfig().set(uuid + ".Mined.Iron", 0);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 1);
				hcf.getConfig().set(uuid + ".Mined.Stone", 0);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Lapis", hcf.getConfig().getInt(uuid + ".Mined.Lapis") + 1);
				hcf.saveConfig();
			}
		}
		if(block.getType().equals(Material.STONE)) {
			if(!hcf.getConfig().contains(uuid)) {
				hcf.getConfig().set(uuid + ".Mined.Diamond", 0);
				hcf.getConfig().set(uuid + ".Mined.Gold", 0);
				hcf.getConfig().set(uuid + ".Mined.Emerald", 0);
				hcf.getConfig().set(uuid + ".Mined.Coal", 0);
				hcf.getConfig().set(uuid + ".Mined.Redstone",0);
				hcf.getConfig().set(uuid + ".Mined.Iron", 0);
				hcf.getConfig().set(uuid + ".Mined.Lapis", 0);
				hcf.getConfig().set(uuid + ".Mined.Stone", 1);
				hcf.saveConfig();
			} else {
				hcf.getConfig().set(uuid + ".Mined.Stone", hcf.getConfig().getInt(uuid + ".Mined.Stone") + 1);
				hcf.saveConfig();
			}
		}
	}
	
	

}
