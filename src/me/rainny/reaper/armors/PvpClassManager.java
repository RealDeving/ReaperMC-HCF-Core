package me.rainny.reaper.armors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.rainny.reaper.HCF;
import me.rainny.reaper.armors.archer.ArcherClass;
import me.rainny.reaper.armors.bard.BardClass;
import me.rainny.reaper.armors.chain.AssassinClass;
import me.rainny.reaper.armors.chain.RogueClass;
import me.rainny.reaper.armors.event.PvpClassEquipEvent;
import me.rainny.reaper.armors.event.PvpClassUnequipEvent;
import me.rainny.reaper.armors.fishermen.FishermenClass;
import me.rainny.reaper.armors.miner.MinerClass;

import javax.annotation.Nullable;
import java.util.*;

public class PvpClassManager implements Listener {

    // Mapping to get the PVP Class a player has equipped.
    private final Map<UUID, PvpClass> equippedClassMap = new HashMap<>();

    private final List<PvpClass> pvpClasses = new ArrayList<>();

    public PvpClassManager(HCF plugin) {
        pvpClasses.add(new ArcherClass(plugin));
        pvpClasses.add(new BardClass(plugin));
        pvpClasses.add(new MinerClass(plugin));
        pvpClasses.add(new AssassinClass(plugin));
        pvpClasses.add(new RogueClass(plugin));
        pvpClasses.add(new FishermenClass(plugin));

        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (PvpClass pvpClass : pvpClasses) {
            if (pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, plugin);
            }
        }
    }

    public void onDisable() {
        for (Map.Entry<UUID, PvpClass> entry : new HashMap<>(equippedClassMap).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer(entry.getKey()), null);
        }

        this.pvpClasses.clear();
        this.equippedClassMap.clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        setEquippedClass(event.getEntity(), null);
    }

    /**
     * Gets the {@link PvpClass}es held by this manager
     *
     * @return set of {@link PvpClass}es
     */
    public Collection<PvpClass> getPvpClasses() {
        return pvpClasses;
    }

    /**
     * Gets the equipped {@link PvpClass} of a {@link Player}.
     *
     * @param player
     *            the {@link Player} to get for
     * @return the equipped {@link PvpClass}
     */
    public PvpClass getEquippedClass(Player player) {
        synchronized (equippedClassMap) {
            return equippedClassMap.get(player.getUniqueId());
        }
    }

    public boolean hasClassEquipped(Player player, PvpClass pvpClass) {
        return getEquippedClass(player) == pvpClass;
    }

    /**
     * Sets the equipped {@link PvpClass} of a {@link Player}.
     *
     * @param player
     *            the {@link Player} to set for
     * @param pvpClass
     *            the class to equip or null to un-equip active
     */
    public void setEquippedClass(Player player, @Nullable PvpClass pvpClass) {
        if (pvpClass == null) {
            PvpClass equipped = this.equippedClassMap.remove(player.getUniqueId());
            if (equipped != null) {
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent(new PvpClassUnequipEvent(player, equipped));
            }
        } else if (pvpClass.onEquip(player) && pvpClass != this.getEquippedClass(player)) {
            equippedClassMap.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent(new PvpClassEquipEvent(player, pvpClass));
        }
    }
}
