package me.rainny.reaper.listener.fixes;

import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawn implements Listener
{
    @EventHandler
    public void onSpawn(final CreatureSpawnEvent e) {
        if ((e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN) && e.getEntity() instanceof Monster) {
            e.setCancelled(true);
        }
    }
}
