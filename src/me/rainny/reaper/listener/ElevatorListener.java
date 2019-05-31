package me.rainny.reaper.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener {
	private String prefix = "§cERROR: ";
	private String signTitle = "§9[Elevator]";
	private List<Material> safe;
	
	public ElevatorListener() {
		safe = new ArrayList<Material>();
        for (Material mat : Material.values()) {
        	if (mat.isTransparent()) {
        		safe.add(mat);
        	}
        }
        safe.add(Material.SEEDS);
        safe.add(Material.SIGN_POST);
        safe.add(Material.WOODEN_DOOR);
        safe.add(Material.WALL_SIGN);
        safe.add(Material.STONE_PLATE);
        safe.add(Material.IRON_DOOR_BLOCK);
        safe.add(Material.WOOD_PLATE);
        safe.add(Material.FENCE_GATE);
        safe.add(Material.WATER);
        safe.add(Material.STATIONARY_WATER);
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignUpdate(final SignChangeEvent e) {
		if (StringUtils.containsIgnoreCase(e.getLine(0), "Elevator")) {
			boolean up;
			if (StringUtils.containsIgnoreCase(e.getLine(1), "Up")) {
				up = true;
			} else {
				if (!StringUtils.containsIgnoreCase(e.getLine(1), "Down")) {
					e.getPlayer().sendMessage(String.valueOf(prefix) + "Sign must be either way up or down");
					fail(e);
					return;
				}
				up = false;
			}
			e.setLine(0, signTitle);
			e.setLine(1, up ? "Up" : "Down");
			e.setLine(2, "");
			e.setLine(3, "");
		}
	}

	public void fail(final SignChangeEvent e) {
		e.setLine(0, signTitle);
		e.setLine(1, ChatColor.RED + "Error");
		e.setLine(2, "");
		e.setLine(3, "");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(final PlayerInteractEvent e) {
		Sign s;
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		if (e.getClickedBlock().getLocation().getBlock().getState() instanceof Sign
				&& (s = (Sign) e.getClickedBlock().getLocation().getBlock().getState()).getLine(0)
						.contains("[Elevator]")
				&& (s.getLine(1).contains("Up") || s.getLine(1).contains("Down"))) {
			Player p = e.getPlayer();
			if (s.getLine(1).contains("Up")) {
				goUp(s.getBlock(), p);
				return;
			} else if (s.getLine(1).contains("Down")) {
				goDown(s.getBlock(), p);
				return;
			}
		}
	}

	public boolean goUp(Block b, Player p) {
		Location l = b.getLocation();
		double y = p.getLocation().getY();
		Location loc = l;
		double i = y + 2.0;
		while (i < 254.0) {
			if (!isSafe(getBlockAt(l, i).getType()) && isSafe(getBlockAt(l, i + 1.0).getType())
					&& isSafe(getBlockAt(l, i + 2.0).getType())) {
				loc = new Location(l.getWorld(), l.getX() + 0.5, i + 1.0, l.getZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch());
				p.teleport(loc);
				return true;
			}
			i+=1.0;
		}
		return false;
	}
	
	public boolean goDown(Block b, Player p) {
		Location l = b.getLocation();
		double y = p.getLocation().getY();
		Location loc = l;
		double i = y - 2.0;
		while (i > 1.0) {
			if (!isSafe(getBlockAt(l, i).getType()) && isSafe(getBlockAt(l, i + 1.0).getType())
					&& isSafe(getBlockAt(l, i + 2.0).getType())) {
				loc = new Location(l.getWorld(), l.getX() + 0.5, i + 1.0, l.getZ() + 0.5, p.getLocation().getYaw(), p.getLocation().getPitch());
				p.teleport(loc);
				return true;
			}
			i-=1.0;
		}
		return false;
	}

	public Block getBlockAt(Location l, double y) {
		return l.getWorld().getBlockAt(new Location(l.getWorld(), l.getX(), y, l.getZ()));
	}

	public boolean isSafe(final Material m) {
		return safe.contains(m);
	}
}
