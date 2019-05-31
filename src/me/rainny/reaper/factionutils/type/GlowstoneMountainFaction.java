package me.rainny.reaper.factionutils.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.doctordark.utils.BukkitUtils;

import me.rainny.reaper.factionutils.claim.Claim;
import me.rainny.reaper.ymls.SettingsYML;

import java.util.Map;

/**
 * Represents the {@link SpawnFaction}.
 */
public class GlowstoneMountainFaction extends ClaimableFaction implements ConfigurationSerializable {

    public GlowstoneMountainFaction() {
        super("GlowstoneMountain");

        this.safezone = false;
        for (World world : Bukkit.getWorlds()) {
            int radius = SettingsYML.SPAWN_RADIUS_MAP.get(world.getEnvironment());
            if (radius > 0) {
                addClaim(new Claim(this, new Location(world, radius, 0, radius), new Location(world, -radius, world.getMaxHeight(), -radius)), null);
                final World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {

                }
            }
        }
    }

    public GlowstoneMountainFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.GOLD + "Glowstone Mountain";
    }

    @Override
    public void printDetails(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(' ' + getDisplayName(sender));
        sender.sendMessage(ChatColor.GRAY + "  Location: " + ChatColor.RED + this.getClaims());
        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}