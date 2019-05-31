package me.rainny.reaper.scoreboard.providers;

import org.bukkit.entity.*;

import me.rainny.reaper.scoreboard.providers.reflection.ReflectionConstants;

import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import net.minecraft.server.v1_7_R4.*;
import org.apache.commons.lang.*;
import java.util.*;
import org.bukkit.*;

public class PlayerScoreboard
{
    public static final String[] TEAM_NAMES;
    private final Player player;
    private final PlayerConnection connection;
    private final String objectiveName;
    private int lastSent;
    private boolean valid;
    
    @SuppressWarnings("unchecked")
	public PlayerScoreboard(final Player player) {
        lastSent = 0;
        valid = true;
        this.player = player;
        connection = ((CraftPlayer)player).getHandle().playerConnection;
        objectiveName = "Sidebar-" + RandomStringUtils.random(8, "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789$_?*+-/()[]{}%!=&@<>~");
        final PacketPlayOutScoreboardObjective objective = new PacketPlayOutScoreboardObjective();
        ReflectionConstants.SCOREBOARD_OBJECTIVE_NAME.set(objective, objectiveName);
        ReflectionConstants.SCOREBOARD_OBJECTIVE_TITLE.set(objective, ScoreboardManager.instance.getTitle());
        ReflectionConstants.SCOREBOARD_OBJECTIVE_ACTION.set(objective, 0);
        this.connection.sendPacket((Packet)objective);
        final PacketPlayOutScoreboardDisplayObjective displayObjective = new PacketPlayOutScoreboardDisplayObjective();
        ReflectionConstants.SCOREBOARD_DISPLAY_OBJECTIVE_NAME.set(displayObjective, this.objectiveName);
        ReflectionConstants.SCOREBOARD_DISPLAY_OBJECTIVE_POSITION.set(displayObjective, 1);
        connection.sendPacket((Packet)displayObjective);
        for (int i = 0; i < 15; ++i) {
            final PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam();
            ReflectionConstants.SCOREBOARD_TEAM_NAME.set(team, PlayerScoreboard.TEAM_NAMES[i]);
            ReflectionConstants.SCOREBOARD_TEAM_PLAYERS.get(team).add(PlayerScoreboard.TEAM_NAMES[i]);
            this.connection.sendPacket((Packet)team);
        }
    }
    
    public void send() {
        if (!this.valid) {
            return;
        }
        final List<String> lines = ScoreboardManager.instance.getProvider().getScoreboardEntries(this.player);
        if (this.lastSent != lines.size()) {
            for (int i = 0; i < 15; ++i) {
                final PacketPlayOutScoreboardScore score = new PacketPlayOutScoreboardScore();
                ReflectionConstants.SCOREBOARD_SCORE_NAME.set(score, PlayerScoreboard.TEAM_NAMES[i]);
                ReflectionConstants.SCOREBOARD_SCORE_OBJECTIVE.set(score, this.objectiveName);
                ReflectionConstants.SCOREBOARD_SCORE_ACTION.set(score, 1);
                this.connection.sendPacket((Packet)score);
            }
        }
        for (int i = 0; i < Math.min(lines.size(), 15); ++i) {
            final String line = lines.get(i);
            String right = "";
            String left;
            if (line.length() < 17) {
                left = line;
            }
            else {
                left = line.substring(0, 16);
                right = line.substring(16, line.length());
                if (left.endsWith("§")) {
                    left = left.substring(0, left.length() - 1);
                    right = "§" + right;
                }
                final String lastColors = ChatColor.getLastColors(left);
                right = lastColors + right;
            }
            final PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam();
            ReflectionConstants.SCOREBOARD_TEAM_NAME.set(team, PlayerScoreboard.TEAM_NAMES[i]);
            ReflectionConstants.SCOREBOARD_TEAM_PREFIX.set(team, left);
            ReflectionConstants.SCOREBOARD_TEAM_SUFFIX.set(team, StringUtils.left(right, 16));
            ReflectionConstants.SCOREBOARD_TEAM_ACTION.set(team, 2);
            this.connection.sendPacket((Packet)team);
            final PacketPlayOutScoreboardScore score2 = new PacketPlayOutScoreboardScore();
            ReflectionConstants.SCOREBOARD_SCORE_NAME.set(score2, PlayerScoreboard.TEAM_NAMES[i]);
            ReflectionConstants.SCOREBOARD_SCORE_SCORE.set(score2, 15 - i);
            ReflectionConstants.SCOREBOARD_SCORE_OBJECTIVE.set(score2, this.objectiveName);
            this.connection.sendPacket((Packet)score2);
        }
        this.lastSent = lines.size();
    }
    
    public void clean() {
        for (int i = 0; i < 15; ++i) {
            final PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam();
            ReflectionConstants.SCOREBOARD_TEAM_NAME.set(team, PlayerScoreboard.TEAM_NAMES[i]);
            ReflectionConstants.SCOREBOARD_TEAM_ACTION.set(team, 4);
            this.connection.sendPacket((Packet)team);
        }
        this.player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        this.valid = false;
    }
    
    static {
        TEAM_NAMES = new String[15];
        final ChatColor[] colors = ChatColor.values();
        for (int i = 0; i < 15; ++i) {
            final ChatColor left = colors[i];
            final ChatColor right = colors[15 - i];
            PlayerScoreboard.TEAM_NAMES[i] = left.toString() + ChatColor.RESET + right.toString() + ChatColor.RESET;
        }
    }
}
