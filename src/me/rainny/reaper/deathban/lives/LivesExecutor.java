package me.rainny.reaper.deathban.lives;

import com.doctordark.utils.other.command.ArgumentExecutor;

import me.rainny.reaper.HCF;
import me.rainny.reaper.deathban.lives.args.*;

/**
 * Handles the execution and tab completion of the lives command.
 */
public class LivesExecutor extends ArgumentExecutor {

    public LivesExecutor(HCF plugin) {
        super("lives");

        addArgument(new LivesCheckArgument(plugin));
        addArgument(new LivesCheckDeathbanArgument(plugin));
        addArgument(new LivesClearDeathbansArgument(plugin));
        addArgument(new LivesGiveArgument(plugin));
        addArgument(new LivesReviveArgument(plugin));
        addArgument(new LivesSetArgument(plugin));
        addArgument(new LivesSetDeathbanTimeArgument());
        addArgument(new LivesTopArgument(plugin));
    }
}