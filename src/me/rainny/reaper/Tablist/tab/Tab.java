package me.rainny.reaper.Tablist.tab;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.rainny.reaper.Tablist.Azazel;

public class Tab {
	private Scoreboard scoreboard;
	private Team elevatedTeam;
	private Map<TabEntryPosition, String> entries;

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public Team getElevatedTeam() {
		return this.elevatedTeam;
	}

	public void setElevatedTeam(Team elevatedTeam) {
		this.elevatedTeam = elevatedTeam;
	}

	@SuppressWarnings("deprecation")
	public Tab(Player player, boolean hook, Azazel azazel) {
		this.entries = new ConcurrentHashMap<TabEntryPosition, String>();

		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		if ((hook) && (!player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard()))) {
			this.scoreboard = player.getScoreboard();
		}
		this.elevatedTeam = this.scoreboard.registerNewTeam((String) getBlanks().get(getBlanks().size() - 1));
		for (Player other : Bukkit.getOnlinePlayers()) {
			getElevatedTeam(other, azazel).addEntry(other.getName());

			Tab tab = azazel.getTabByPlayer(other);
			if (tab != null) {
				tab.getElevatedTeam(player, azazel).addEntry(player.getName());
			}
			PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) other).getHandle());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
		player.setScoreboard(this.scoreboard);

		initialize(player);
	}

	public Team getElevatedTeam(Player player, Azazel azazel) {
		if (player.hasMetadata("HydrogenPrefix")) {
			String prefix = ChatColor.getLastColors(player.getDisplayName().replace(ChatColor.RESET + "", ""));

			String name = (String) getBlanks().get(getBlanks().size() - 1) + prefix;
			if (name.length() > 16) {
				name = name.substring(0, 15);
			}
			Team team = this.scoreboard.getTeam(name);
			if (team == null) {
				team = this.scoreboard.registerNewTeam(name);

				team.setPrefix(prefix);
			}
			return team;
		}
		return this.elevatedTeam;
	}

	public Set<TabEntryPosition> getPositions() {
		return this.entries.keySet();
	}

	public Team getByLocation(int x, int y) {
		for (TabEntryPosition position : this.entries.keySet()) {
			if ((position.getX() == x) && (position.getY() == y)) {
				return this.scoreboard.getTeam(position.getKey());
			}
		}
		return null;
	}

	private void initialize(Player player) {
		if (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47) {
			for (int x = 0; x < 4; x++) {
				for (int y = 0; y < 20; y++) {
					String key = getNextBlank();
					TabEntryPosition position = new TabEntryPosition(x, y, key, this.scoreboard);

					this.entries.put(position, key);

					((CraftPlayer) player).getHandle().playerConnection
							.sendPacket(getPlayerPacket((String) this.entries.get(position)));

					Team team = this.scoreboard.getTeam(position.getKey());
					if (team == null) {
						team = this.scoreboard.registerNewTeam(position.getKey());
					}
					team.addEntry((String) this.entries.get(position));
				}
			}
		} else {
			for (int i = 0; i < 60; i++) {
				int x = i % 3;
				int y = i / 3;

				String key = getNextBlank();
				TabEntryPosition position = new TabEntryPosition(x, y, key, this.scoreboard);
				this.entries.put(position, key);

				((CraftPlayer) player).getHandle().playerConnection
						.sendPacket(getPlayerPacket((String) this.entries.get(position)));

				Team team = this.scoreboard.getTeam(position.getKey());
				if (team == null) {
					team = this.scoreboard.registerNewTeam(position.getKey());
				}
				team.addEntry((String) this.entries.get(position));
			}
		}
	}

	private String getNextBlank() {
		for (String blank : getBlanks()) {
			if (this.scoreboard.getTeam(blank) == null) {
				Iterator<String> localIterator2 = this.entries.values().iterator();
				for (;;) {
					if (!localIterator2.hasNext()) {
						break;
					}
					String identifier = (String) localIterator2.next();
					if (identifier.equals(blank)) {
						break;
					}
				}
				return blank;
			}
		}
		return null;
	}

	public List<String> getBlanks() {
		List<String> toReturn = new ArrayList<String>();
		for (ChatColor color : ChatColor.values()) {
			for (int i = 0; i < 4; i++) {
				String identifier = StringUtils.repeat(new StringBuilder().append(color).append("").toString(), 4 - i)
						+ ChatColor.RESET;
				toReturn.add(identifier);
			}
		}
		return toReturn;
	}

	private static Packet getPlayerPacket(String name) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		try {
			Field action = PacketPlayOutPlayerInfo.class.getDeclaredField("action");
			Field username = PacketPlayOutPlayerInfo.class.getDeclaredField("username");
			Field player = PacketPlayOutPlayerInfo.class.getDeclaredField("player");

			action.setAccessible(true);
			username.setAccessible(true);
			player.setAccessible(true);

			action.set(packet, Integer.valueOf(0));
			username.set(packet, name);
			player.set(packet, new GameProfile(UUID.randomUUID(), name));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return packet;
	}

	public static class TabEntryPosition {
		private final int x;
		private final int y;
		private final String key;

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public String getKey() {
			return this.key;
		}

		public TabEntryPosition(int x, int y, String key, Scoreboard scoreboard) {
			this.x = x;
			this.y = y;
			this.key = key;
		}
	}

	public static class UpdatedPacketPlayOutPlayerInfo extends PacketPlayOutPlayerInfo {
	}
}