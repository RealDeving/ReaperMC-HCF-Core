package me.rainny.reaper.scoreboard;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.rainny.reaper.HCF;
import me.rainny.reaper.armors.archer.ArcherClass;
import me.rainny.reaper.factionutils.type.PlayerFaction;

public class NameManager {

	@SuppressWarnings("deprecation")
	public NameManager() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					try {
						Objective obj = null;
						Scoreboard sb = p.getScoreboard();
						PlayerFaction f = HCF.getInstance().getFactionManager().getPlayerFaction(p.getUniqueId());
						if (sb == null) {
							sb = Bukkit.getScoreboardManager().getNewScoreboard();
							p.setScoreboard(sb);
						}
						if ((obj = sb.getObjective("names")) == null) {
							sb.registerNewObjective("names", "dummy");
						}
						for (Player p2 : Bukkit.getOnlinePlayers()) {
							if (f != null) {
								if (f.isAllied(p2.getUniqueId())) {
									Team t = sb.getTeam(p2.getName());
									if (t == null) {
										t = sb.registerNewTeam(p2.getName());
									}
									t.setPrefix("§9");
								} else if (ArcherClass.TAGGED.containsKey(p2.getUniqueId())) {
									Team t = sb.getTeam(p2.getName());
									if (t == null) {
										t = sb.registerNewTeam(p2.getName());
									}
									t.setPrefix("§e");
								} else if (f.getMembers().containsKey(p2.getUniqueId())) {
									Team t = sb.getTeam(p2.getName());
									if (t == null) {
										t = sb.registerNewTeam(p2.getName());
									}
									t.setPrefix("§2");
								} else {
									Team t = sb.getTeam(p2.getName());
									if (t == null) {
										t = sb.registerNewTeam(p2.getName());
									}
									t.setPrefix("§4");
								}
							} else {
								Team t = sb.getTeam(p2.getName());
								if (t == null) {
									t = sb.registerNewTeam(p2.getName());
								}
								t.setPrefix("§4");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.runTaskTimerAsynchronously(HCF.plugin, 10L, 10L);
	}

	public void addFocus(PlayerFaction f, UUID uuid) {
		for (UUID puuid : f.getMembers().keySet()) {
			Player p = Bukkit.getPlayer(puuid);
			if (p.isOnline()) {
				p.getScoreboard().getTeam("CLfocused").addPlayer(Bukkit.getPlayer(uuid));
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void update() {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			PlayerFaction f = HCF.getInstance().getFactionManager().getPlayerFaction(pl.getUniqueId());
			if (f == null) {
				return;
			}
			Scoreboard sb = pl.getScoreboard();
			Team members = null;
			if (sb.getTeam("CLmembers") == null) {
				members = sb.registerNewTeam("CLmembers");
				members.setPrefix(ChatColor.translateAlternateColorCodes('&', "&4"));
				members.setCanSeeFriendlyInvisibles(true);
				members.setAllowFriendlyFire(false);
			} else {
				members = sb.registerNewTeam("CLmembers");
			}
			Team ally = null;
			if (sb.getTeam("CLally") == null) {
				ally = (hasTeam(sb, "CLally") ? sb.getTeam("CLally") : sb.registerNewTeam("CLally"));
				ally.setPrefix(ChatColor.translateAlternateColorCodes('&', "&9"));
			} else {
				ally = (hasTeam(sb, "CLally") ? sb.getTeam("CLally") : sb.registerNewTeam("CLally"));
			}
			Team other = null;
			if (sb.getTeam("CLother") == null) {
				other = (hasTeam(sb, "CLother") ? sb.getTeam("CLother") : sb.registerNewTeam("CLother"));
				other.setPrefix(ChatColor.translateAlternateColorCodes('&', "&4"));
			} else {
				other = (hasTeam(sb, "CLother") ? sb.getTeam("CLother") : sb.registerNewTeam("CLother"));
			}
			Team tagged = null;
			if (sb.getTeam("CLtagger") == null) {
				tagged = (hasTeam(sb, "CLtagged") ? sb.getTeam("CLtagged") : sb.registerNewTeam("CLtagged"));
				tagged.setPrefix(ChatColor.translateAlternateColorCodes('&', "&e"));
			} else {
				tagged = (hasTeam(sb, "CLtagged") ? sb.getTeam("CLtagged") : sb.registerNewTeam("CLtagged"));
			}
			Team focused = null;
			if (sb.getTeam("CLfocused") == null) {
				focused = (hasTeam(sb, "CLfocused") ? sb.getTeam("CLfocused") : sb.registerNewTeam("CLfocused"));
				focused.setPrefix(ChatColor.translateAlternateColorCodes('&', "&d"));
			} else {
				focused = (hasTeam(sb, "CLfocused") ? sb.getTeam("CLfocused") : sb.registerNewTeam("CLfocused"));
			}
			members.addPlayer(pl);
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (f.getMembers().containsKey(p2.getUniqueId())) {
					members.addPlayer(p2);
				} else if (f.getAllied().contains(p2.getUniqueId())) {
					ally.addPlayer(p2);
				} else if (ArcherClass.TAGGED.containsKey(p2.getUniqueId())) {
					tagged.addPlayer(p2);
				} else {
					other.addPlayer(p2);
				}
			}
		}
	}

	public boolean hasTeam(Scoreboard sb, String name) {
		if (sb.getTeams().contains(name)) {
			return true;
		} else {
			return false;
		}
	}

}
