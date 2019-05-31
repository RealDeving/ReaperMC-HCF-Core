package me.rainny.reaper.listener;


import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Cooldowns
{
    private static HashMap<String, HashMap<UUID, Long>> cooldown;
    
    public static void createCooldown(final String k) {
        if (Cooldowns.cooldown.containsKey(k)) {
            throw new IllegalArgumentException("Cooldown already exists.");
        }
        Cooldowns.cooldown.put(k, new HashMap<UUID, Long>());
    }
    
    public static HashMap<UUID, Long> getCooldownMap(final String k) {
        if (Cooldowns.cooldown.containsKey(k)) {
            return Cooldowns.cooldown.get(k);
        }
        return null;
    }
    
    public static void addCooldown(final String k, final Player p, final int seconds) {
        if (!Cooldowns.cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        final long next = System.currentTimeMillis() + seconds * 1000L;
        Cooldowns.cooldown.get(k).put(p.getUniqueId(), next);
    }
    
    public static boolean isOnCooldown(final String k, final Player p) {
        return Cooldowns.cooldown.containsKey(k) && Cooldowns.cooldown.get(k).containsKey(p.getUniqueId()) && System.currentTimeMillis() <= Cooldowns.cooldown.get(k).get(p.getUniqueId());
    }
    
    public static int getCooldownForPlayerInt(final String k, final Player p) {
        return (int)(Cooldowns.cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }
    
    public static long getCooldownForPlayerLong(final String k, final Player p) {
        return (int)(Cooldowns.cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis());
    }
    
    public static void removeCooldown(final String k, final Player p) {
        if (!Cooldowns.cooldown.containsKey(k)) {
            throw new IllegalArgumentException(k + " does not exist");
        }
        Cooldowns.cooldown.get(k).remove(p.getUniqueId());
    }
    
    static {
        Cooldowns.cooldown = new HashMap<String, HashMap<UUID, Long>>();
    }
}
