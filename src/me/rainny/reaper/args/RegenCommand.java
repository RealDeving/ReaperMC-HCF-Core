package me.rainny.reaper.args;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.struct.RegenStatus;
import me.rainny.reaper.factionutils.type.PlayerFaction;
import me.rainny.reaper.ymls.SettingsYML;

import java.util.Collections;
import java.util.List;

public class RegenCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public RegenCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }

        RegenStatus regenStatus = playerFaction.getRegenStatus();
        switch (regenStatus) {
        case FULL:
            sender.sendMessage(ChatColor.RED + "Your faction currently has full DTR.");
            return true;
        case PAUSED:
            sender.sendMessage(ChatColor.RED + "Your faction is currently on DTR freeze for another " + ChatColor.WHITE
                    + DurationFormatUtils.formatDurationWords(playerFaction.getRemainingRegenerationTime(), true, true) + ChatColor.RED + '.');

            return true;
        case REGENERATING:
            sender.sendMessage(ChatColor.GRAY + "Your faction currently has " + ChatColor.GRAY + regenStatus.getSymbol() + ' ' + playerFaction.getDeathsUntilRaidable() + ChatColor.GRAY
                    + " DTR and is regenerating at a rate of " + ChatColor.GOLD + SettingsYML.DTR_INCREMENT_BETWEEN_UPDATES + ChatColor.GRAY + " every " + ChatColor.GOLD
                    + SettingsYML.DTR_WORDS_BETWEEN_UPDATES + ChatColor.GRAY + ". Your ETA for maximum DTR is " + ChatColor.RED
                    + DurationFormatUtils.formatDurationWords(getRemainingRegenMillis(playerFaction), true, true) + ChatColor.GRAY + '.');

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unrecognised regeneration status, please inform an Developer or an System Admin.");
        return true;
    }

    public long getRemainingRegenMillis(PlayerFaction faction) {
        long millisPassedSinceLastUpdate = System.currentTimeMillis() - faction.getLastDtrUpdateTimestamp();
        double dtrRequired = faction.getMaximumDeathsUntilRaidable() - faction.getDeathsUntilRaidable();
        return (long) ((SettingsYML.DTR_MILLIS_BETWEEN_UPDATES / SettingsYML.DTR_INCREMENT_BETWEEN_UPDATES) * dtrRequired) - millisPassedSinceLastUpdate;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
