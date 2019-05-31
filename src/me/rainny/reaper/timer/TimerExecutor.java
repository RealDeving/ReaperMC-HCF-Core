package me.rainny.reaper.timer;

import com.doctordark.utils.other.command.ArgumentExecutor;

import me.rainny.reaper.HCF;
import me.rainny.reaper.timer.args.TimerCheckArgument;
import me.rainny.reaper.timer.args.TimerSetArgument;

/**
 * Handles the execution and tab completion of the timer command.
 */
public class TimerExecutor extends ArgumentExecutor {

    public TimerExecutor(HCF plugin) {
        super("timer");

        addArgument(new TimerCheckArgument(plugin));
        addArgument(new TimerSetArgument(plugin));
    }
}