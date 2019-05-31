package me.rainny.reaper.factionutils.type;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.doctordark.utils.other.cuboid.Cuboid;

import me.rainny.reaper.eventutils.CaptureZone;
import me.rainny.reaper.eventutils.EventType;
import me.rainny.reaper.factionutils.claim.Claim;
import me.rainny.reaper.factionutils.claim.ClaimHandler;

import java.util.List;
import java.util.Map;

public abstract class EventFaction extends ClaimableFaction {

    public EventFaction(String name) {
        super(name);
        this.setDeathban(true); // make cappable factions death-ban between reloads.
    }

    public EventFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(Faction faction) {
        return ChatColor.BLUE + getName() + ' ' + ChatColor.GOLD + getEventType().getDisplayName();
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return ChatColor.BLUE + getName() + " KoTH".replace("Candy", "§d§lCandy §7(KoTH)").replace("Ship", "§b§lShip §7(KoTH)").replace("Tower", "§7§lTower §7(KoTH)").replace("End", "§9§lEnd §7(KoTH)");
    }

    public String getScoreboardName() {
        return ChatColor.BLUE  + ChatColor.BOLD.toString() + getName().replace("Candy", "§d§lCandy").replace("Ship", "§b§lShip").replace("Tower", "§7§lTower").replace("End", "§9§lEnd");
    }

    /**
     * Sets the {@link Cuboid} area of this {@link KothFaction}.
     *
     * @param cuboid
     *            the {@link Cuboid} to set
     * @param sender
     *            the {@link CommandSender} setting the claim
     */
    public void setClaim(Cuboid cuboid, CommandSender sender) {
        removeClaims(getClaims(), sender);

        // Now add the new claim.
        Location min = cuboid.getMinimumPoint();
        min.setY(ClaimHandler.MIN_CLAIM_HEIGHT);

        Location max = cuboid.getMaximumPoint();
        max.setY(ClaimHandler.MAX_CLAIM_HEIGHT);

        addClaim(new Claim(this, min, max), sender);
    }

    /**
     * Gets the {@link EventType} of this {@link CapturableFaction}.
     *
     * @return the {@link EventType}
     */
    public abstract EventType getEventType();

    /**
     * Gets the {@link CaptureZone}s for this {@link CapturableFaction}.
     *
     * @return list of {@link CaptureZone}s
     */
    public abstract List<CaptureZone> getCaptureZones();
}
