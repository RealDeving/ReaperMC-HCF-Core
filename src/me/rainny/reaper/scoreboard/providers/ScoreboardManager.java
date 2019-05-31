package me.rainny.reaper.scoreboard.providers;

import java.util.*;
import org.bukkit.plugin.java.*;
import java.util.concurrent.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;
import org.bukkit.event.*;

public class ScoreboardManager implements Listener
{
    public static ScoreboardManager instance;
    public final ScoreboardEntryProvider provider;
    private final String title;
    private final Map<UUID, PlayerScoreboard> scoreboards;
    private final JavaPlugin plugin;
    public boolean debugging;
    
    @SuppressWarnings("deprecation")
	public ScoreboardManager(final JavaPlugin plugin, final ScoreboardEntryProvider provider, final String title) {
        ScoreboardManager.instance = this;
        this.plugin = plugin;
        this.provider = provider;
        this.title = title;
        this.scoreboards = new ConcurrentHashMap<UUID, PlayerScoreboard>();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        UUID uniqueId;
        for(Player pp : Bukkit.getOnlinePlayers()) {
        	if (this.debugging) {
                plugin.getLogger().info("[ScoreboardManager] Loaded scoreboard for " + pp.getName() + "[" + pp.getUniqueId().toString() + "]");
            }
            uniqueId = pp.getUniqueId();
            this.scoreboards.putIfAbsent(uniqueId, new PlayerScoreboard(pp));
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)plugin, this::update, 0L, 1L);
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (this.debugging) {
                this.plugin.getLogger().info("[ScoreboardManager] Loaded scoreboard for " + player.getName() + "[" + player.getUniqueId().toString() + "]");
            }
            this.scoreboards.putIfAbsent(uuid, new PlayerScoreboard(player));
        }, 3L);
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        final PlayerScoreboard scoreboard;
        if ((scoreboard = this.scoreboards.remove(uniqueId)) != null) {
            if (this.debugging) {
                this.plugin.getLogger().info("[ScoreboardManager] Deleted scoreboard of " + player.getName() + "[" + player.getUniqueId().toString() + "]");
            }
            scoreboard.clean();
        }
    }
    
    @EventHandler
    public void onDisable(final PluginDisableEvent event) {
        if (event.getPlugin() == this.plugin) {
            this.scoreboards.forEach((id, board) -> board.clean());
            this.scoreboards.clear();
            HandlerList.unregisterAll((Listener)this);
        }
    }
    
    public void update() {
        this.scoreboards.forEach((id, board) -> board.send());
    }
    
    public static ScoreboardManager getInstance() {
        return ScoreboardManager.instance;
    }
    
    public ScoreboardEntryProvider getProvider() {
        return this.provider;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public Map<UUID, PlayerScoreboard> getScoreboards() {
        return this.scoreboards;
    }
}
