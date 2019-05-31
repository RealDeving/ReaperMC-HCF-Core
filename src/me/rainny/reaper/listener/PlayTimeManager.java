package me.rainny.reaper.listener;

import net.minecraft.util.gnu.trove.map.*;
import org.bukkit.plugin.java.*;

import com.doctordark.utils.Config;

import me.rainny.reaper.HCF;
import net.minecraft.util.gnu.trove.map.hash.*;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.configuration.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.entity.*;

public class PlayTimeManager implements Listener
{
    private final TObjectLongMap<UUID> totalPlaytimeMap;
    private final TObjectLongMap<UUID> sessionTimestamps;
    private final Config config;

	public PlayTimeManager(final JavaPlugin plugin) {
        this.totalPlaytimeMap = new TObjectLongHashMap<UUID>();
        this.sessionTimestamps = new TObjectLongHashMap<UUID>();
        this.config = new Config((HCF) plugin, "play-times");
        this.reloadPlaytimeData();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.sessionTimestamps.put((UUID) event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.totalPlaytimeMap.put((UUID) uuid, this.getTotalPlayTime(uuid));
        this.sessionTimestamps.remove((Object)uuid);
    }

    @SuppressWarnings("deprecation")
	public void reloadPlaytimeData() {
        final Object object = this.config.get("playing-times");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            for (final Object id : section.getKeys(false)) {
                this.totalPlaytimeMap.put((UUID) UUID.fromString((String)id), this.config.getLong("playing-times." + (String)id, 0L));
            }
        }
        final long millis = System.currentTimeMillis();
            for (Player target : Bukkit.getOnlinePlayers()){
            this.sessionTimestamps.put((UUID) target.getUniqueId(), millis);
        }
    }

    @SuppressWarnings("deprecation")
	public void savePlaytimeData() {
        for (Player player : Bukkit.getOnlinePlayers()){
            this.totalPlaytimeMap.put((UUID) player.getUniqueId(), this.getTotalPlayTime(player.getUniqueId()));
        }
        this.totalPlaytimeMap.forEachEntry((uuid, l) -> {
            this.config.set("playing-times." + uuid.toString(), (Object)l);
            return true;
        });
        this.config.save();
    }

    public long getSessionPlayTime(final UUID uuid) {
        final long session = this.sessionTimestamps.get((Object)uuid);
        return (session != this.sessionTimestamps.getNoEntryValue()) ? (System.currentTimeMillis() - session) : 0L;
    }

    public long getPreviousPlayTime(final UUID uuid) {
        final long stamp = this.totalPlaytimeMap.get((Object)uuid);
        return (stamp == this.totalPlaytimeMap.getNoEntryValue()) ? 0L : stamp;
    }

    public long getTotalPlayTime(final UUID uuid) {
        return this.getSessionPlayTime(uuid) + this.getPreviousPlayTime(uuid);
    }
}
