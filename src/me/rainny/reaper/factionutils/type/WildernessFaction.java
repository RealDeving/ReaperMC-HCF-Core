package me.rainny.reaper.factionutils.type;

import org.bukkit.command.CommandSender;

import me.rainny.reaper.ymls.SettingsYML;

import java.util.Map;

/**
 * Represents the {@link WildernessFaction}.
 */
public class WildernessFaction extends Faction {

    public WildernessFaction() {
        super("Wilderness");
    }

    public WildernessFaction(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getDisplayName(CommandSender sender) {
        return SettingsYML.WILDERNESS_COLOUR + getName();
    }
}
