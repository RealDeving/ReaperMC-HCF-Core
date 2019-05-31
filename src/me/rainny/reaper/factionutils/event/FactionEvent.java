package me.rainny.reaper.factionutils.event;

import com.google.common.base.Preconditions;

import me.rainny.reaper.factionutils.type.Faction;

import org.bukkit.event.Event;

/**
 * Represents a faction related event
 */
public abstract class FactionEvent extends Event {

    protected final Faction faction;

    public FactionEvent(final Faction faction) {
        this.faction = Preconditions.checkNotNull(faction, "Faction cannot be null");
    }

    FactionEvent(final Faction faction, boolean async) {
        super(async);
        this.faction = Preconditions.checkNotNull(faction, "Faction cannot be null");
    }

    /**
     * Returns the {@link Faction} involved in this event
     *
     * @return the {@link Faction} that is involved in this event
     */
    public Faction getFaction() {
        return faction;
    }
}