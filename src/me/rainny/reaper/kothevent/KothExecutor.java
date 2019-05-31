package me.rainny.reaper.kothevent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.utils.other.command.ArgumentExecutor;

import me.rainny.reaper.HCF;
import me.rainny.reaper.kothevent.args.KothHelpArgument;
import me.rainny.reaper.kothevent.args.KothNextArgument;
import me.rainny.reaper.kothevent.args.KothScheduleArgument;
import me.rainny.reaper.kothevent.args.KothSetCapDelayArgument;

/**
 * Command used to handle KingOfTheHills.
 */
public class KothExecutor extends ArgumentExecutor {

    private final KothScheduleArgument kothScheduleArgument;

    public KothExecutor(HCF plugin) {
        super("koth");

        addArgument(new KothHelpArgument(this));
        addArgument(new KothNextArgument(plugin));
        addArgument(this.kothScheduleArgument = new KothScheduleArgument(plugin));
        addArgument(new KothSetCapDelayArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            this.kothScheduleArgument.onCommand(sender, command, label, args);
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }
}
