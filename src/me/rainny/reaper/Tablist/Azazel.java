package me.rainny.reaper.Tablist;

import lombok.Getter;
import lombok.Setter;
import me.rainny.reaper.Tablist.tab.Tab;
import me.rainny.reaper.Tablist.tab.TabAdapter;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Azazel implements Listener {

    @Getter private final JavaPlugin plugin;
    private final Map<UUID, Tab> tabs;
    @Setter private TabAdapter adapter;

    @SuppressWarnings("deprecation")
	public Azazel(JavaPlugin plugin) {
        this.plugin = plugin;
        this.tabs = new ConcurrentHashMap<>();

        if (Bukkit.getMaxPlayers() < 60) {
            Bukkit.getLogger().severe("There aren't 60 player slots, this will fuck up the tab list."); //TODO: Possibly set max players to 60?
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() < 47) {
                if (!(tabs.containsKey(player.getUniqueId()))) {
                    tabs.put(player.getUniqueId(), new Tab(player, true, this));
                }
            }
        }

        new AzazelTask(this, plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Azazel(JavaPlugin plugin, TabAdapter adapter) {
        this(plugin);

        this.adapter = adapter;
    }

    public Tab getTabByPlayer(Player player) {
        return tabs.get(player.getUniqueId());
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)event.getPlayer()).getHandle());
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                tabs.put(event.getPlayer().getUniqueId(), new Tab(event.getPlayer(), true, Azazel.this));
            }
        }.runTaskLater(plugin, 20L);
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        tabs.remove(event.getPlayer().getUniqueId());

        for (Player other : Bukkit.getOnlinePlayers()) {
            EntityPlayer entityPlayer = ((CraftPlayer)other).getHandle();

            if (entityPlayer.playerConnection.networkManager.getVersion() >= 47) {
                Tab tab = getTabByPlayer(event.getPlayer());

                if (tab != null && tab.getElevatedTeam() != null) {
                    tab.getElevatedTeam().removeEntry(event.getPlayer().getName());
                }
            }

        }
    }

	public TabAdapter getAdapter() {
		return adapter;
	}
}
