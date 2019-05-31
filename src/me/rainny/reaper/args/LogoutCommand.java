package me.rainny.reaper.args;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.rainny.reaper.HCF;
import me.rainny.reaper.timer.type.LogoutTimer;
import me.rainny.reaper.ymls.LangYML;

import java.util.Collections;
import java.util.List;

public class LogoutCommand implements CommandExecutor, TabCompleter {

    private final HCF plugin;

    public LogoutCommand(HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        LogoutTimer logoutTimer = plugin.getTimerManager().getLogoutTimer();

        if (!logoutTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(LangYML.LOGGING_OUT);
            return true;
        }

        sender.sendMessage(LangYML.LOG_OUT);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
