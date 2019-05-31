package me.rainny.reaper.args;

import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import net.minecraft.util.org.apache.commons.io.output.ByteArrayOutputStream;

public class HubCommand implements CommandExecutor {

	public HubCommand(final HCF plugin) {
	}

	public static void teleport(Player pl, String input) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		@SuppressWarnings("resource")
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(input);
		} catch (IOException localIOException) {
		}
		pl.sendPluginMessage(HCF.getPlugin(), "BungeeCord", b.toByteArray());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player p = (Player) sender;
		teleport(p, "lobby");
		p.sendMessage(ChatColor.translateAlternateColorCodes('&',
				HCF.PREFIX + "Teleporting you to the " + HCF.M + "Hub" + HCF.O + "."));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HCF.getPlugin(), new Runnable() {
			public void run() {
				if (p.isOnline()) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							HCF.PREFIX + "&cFailed to connect you to the " + HCF.M + "Hub " + HCF.E + "server."));
				}
			}
		}, 20 * 5);
		return true;
	}

}
