package me.rainny.reaper.conquestevent;

import com.doctordark.utils.other.command.ArgumentExecutor;

import me.rainny.reaper.HCF;

public class ConquestExecutor extends ArgumentExecutor {

    public ConquestExecutor(HCF plugin) {
        super("conquest");
        addArgument(new ConquestSetpointsArgument(plugin));
    }
}
