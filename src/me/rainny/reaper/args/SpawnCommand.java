package me.rainny.reaper.args;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.doctordark.utils.BukkitUtils;

import me.rainny.reaper.HCF;

public class SpawnCommand implements CommandExecutor, TabCompleter
{
    HCF plugin;

    public SpawnCommand(final HCF plugin) {
        super();
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        World world = player.getWorld();
        Location spawn = world.getSpawnLocation().clone().add(0.5, 0.5, 0.5);
        if (!sender.hasPermission(command.getPermission() + ".teleport")) {
            sender.sendMessage(ChatColor.RED + "This server does not have a spawn command, you must travel there. " + "Spawn can be found at " + ChatColor.GRAY + '(' + spawn.getBlockX() + ", " + spawn.getBlockZ() + ')');
            return true;
        }
        if (!sender.hasPermission(command.getPermission() + ".teleport")) {
            this.plugin.getTimerManager().teleportTimer.teleport(player, Bukkit.getWorld("world").getSpawnLocation(), TimeUnit.SECONDS.toMillis(15L), ChatColor.GRAY + "Teleporting to spawn in " + 15 + " seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
            return true;
        }
        if (args.length > 0) {
            world = Bukkit.getWorld(args[0]);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "There is not a world named " + args[0] + '.');
                return true;
            }
            spawn = world.getSpawnLocation().clone().add(0.5, 0.0, 0.5);
        }
        player.teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 1 || !sender.hasPermission(command.getPermission() + ".teleport")) {
            return Collections.emptyList();
        }
        return BukkitUtils.getCompletions(args, Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
    }
}