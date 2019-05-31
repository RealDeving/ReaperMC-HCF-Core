package me.rainny.reaper.factionutils.args;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.utils.DurationFormatter;
import com.doctordark.utils.other.command.CommandArgument;

import me.rainny.reaper.HCF;
import me.rainny.reaper.factionutils.type.SpawnFaction;
import me.rainny.reaper.timer.type.StuckTimer;

public class FactionStuckArgument extends CommandArgument {
    private final HCF plugin;

    public FactionStuckArgument(final HCF plugin) {
        super("stuck", "Teleport to a safe position.", new String[]{"trap", "trapped"});
        this.plugin = plugin;
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player) sender;
        if(player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return true;
        }
        if(plugin.getFactionManager().getFactionAt(((Player) sender).getLocation()) instanceof SpawnFaction){
            sender.sendMessage(ChatColor.RED + "You cannot " + label + " " + this.getName() + " inside of Spawn");
            return true;
        }
        final StuckTimer stuckTimer = this.plugin.getTimerManager().getStuckTimer();
        if(!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.GRAY + "Your " + stuckTimer.getDisplayName() + ChatColor.GRAY + " timer has a remaining " + ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(stuckTimer.getRemaining(player), true, true)+ChatColor.GRAY + '.');
            return true;
        }
        sender.sendMessage(ChatColor.GRAY + stuckTimer.getDisplayName() + ChatColor.GRAY + " timer has started. " + "\nTeleportation will commence in " + ChatColor.LIGHT_PURPLE + DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.GRAY + ". " + "\nThis will cancel if you move more than " + 5 + " blocks.");
        return true;
    }
}
