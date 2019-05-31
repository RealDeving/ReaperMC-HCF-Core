package me.rainny.reaper.scoreboard.providers.reflection;

import java.util.*;

import me.rainny.reaper.scoreboard.providers.Reflection;



public class ReflectionConstants
{
    public static final Class<?> SCOREBOARD_OBJECTIVE_CLASS;
    public static final Reflection.ConstructorInvoker SCOREBOARD_OBJECTIVE_CONSTRUCTOR;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_OBJECTIVE_NAME;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_OBJECTIVE_TITLE;
    public static final Reflection.FieldAccessor<Integer> SCOREBOARD_OBJECTIVE_ACTION;
    public static final Class<?> SCOREBOARD_DISPLAY_OBJECTIVE_CLASS;
    public static final Reflection.ConstructorInvoker SCOREBOARD_DISPLAY_OBJECTIVE_CONSTRUCTOR;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_DISPLAY_OBJECTIVE_NAME;
    public static final Reflection.FieldAccessor<Integer> SCOREBOARD_DISPLAY_OBJECTIVE_POSITION;
    public static final Class<?> SCOREBOARD_TEAM_CLASS;
    public static final Reflection.ConstructorInvoker SCOREBOARD_TEAM_CONSTRUCTOR;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_TEAM_NAME;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_TEAM_DISPLAY_NAME;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_TEAM_PREFIX;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_TEAM_SUFFIX;
    @SuppressWarnings("rawtypes")
	public static final Reflection.FieldAccessor<Collection> SCOREBOARD_TEAM_PLAYERS;
    public static final Reflection.FieldAccessor<Integer> SCOREBOARD_TEAM_ACTION;
    public static final Reflection.FieldAccessor<Integer> SCOREBOARD_TEAM_OPTIONS;
    public static final Class<?> SCOREBOARD_SCORE_CLASS;
    public static final Reflection.ConstructorInvoker SCOREBOARD_SCORE_CONSTRUCTOR;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_SCORE_NAME;
    public static final Reflection.FieldAccessor<String> SCOREBOARD_SCORE_OBJECTIVE;
    public static final Reflection.FieldAccessor<Integer> SCOREBOARD_SCORE_SCORE;
    public static final Reflection.FieldAccessor<?> SCOREBOARD_SCORE_ACTION;
    
    static {
        SCOREBOARD_OBJECTIVE_CLASS = Reflection.getMinecraftClass("PacketPlayOutScoreboardObjective");
        SCOREBOARD_OBJECTIVE_CONSTRUCTOR = Reflection.getConstructor(ReflectionConstants.SCOREBOARD_OBJECTIVE_CLASS, (Class<?>[])new Class[0]);
        SCOREBOARD_OBJECTIVE_NAME = Reflection.getField(ReflectionConstants.SCOREBOARD_OBJECTIVE_CLASS, String.class, 0);
        SCOREBOARD_OBJECTIVE_TITLE = Reflection.getField(ReflectionConstants.SCOREBOARD_OBJECTIVE_CLASS, String.class, 1);
        SCOREBOARD_OBJECTIVE_ACTION = Reflection.getField(ReflectionConstants.SCOREBOARD_OBJECTIVE_CLASS, Integer.TYPE, 0);
        SCOREBOARD_DISPLAY_OBJECTIVE_CLASS = Reflection.getMinecraftClass("PacketPlayOutScoreboardDisplayObjective");
        SCOREBOARD_DISPLAY_OBJECTIVE_CONSTRUCTOR = Reflection.getConstructor(ReflectionConstants.SCOREBOARD_DISPLAY_OBJECTIVE_CLASS, (Class<?>[])new Class[0]);
        SCOREBOARD_DISPLAY_OBJECTIVE_NAME = Reflection.getField(ReflectionConstants.SCOREBOARD_DISPLAY_OBJECTIVE_CLASS, String.class, 0);
        SCOREBOARD_DISPLAY_OBJECTIVE_POSITION = Reflection.getField(ReflectionConstants.SCOREBOARD_DISPLAY_OBJECTIVE_CLASS, Integer.TYPE, 0);
        SCOREBOARD_TEAM_CLASS = Reflection.getMinecraftClass("PacketPlayOutScoreboardTeam");
        SCOREBOARD_TEAM_CONSTRUCTOR = Reflection.getConstructor(ReflectionConstants.SCOREBOARD_TEAM_CLASS, (Class<?>[])new Class[0]);
        SCOREBOARD_TEAM_NAME = Reflection.getField(ReflectionConstants.SCOREBOARD_TEAM_CLASS, String.class, 0);
        SCOREBOARD_TEAM_DISPLAY_NAME = Reflection.getField(ReflectionConstants.SCOREBOARD_TEAM_CLASS, String.class, 1);
        SCOREBOARD_TEAM_PREFIX = Reflection.getField(ReflectionConstants.SCOREBOARD_TEAM_CLASS, String.class, 2);
        SCOREBOARD_TEAM_SUFFIX = Reflection.getField(ReflectionConstants.SCOREBOARD_TEAM_CLASS, String.class, 3);
        SCOREBOARD_TEAM_PLAYERS = Reflection.getField(ReflectionConstants.SCOREBOARD_TEAM_CLASS, Collection.class, 0);
        SCOREBOARD_TEAM_ACTION = Reflection.getField(ReflectionConstants.SCOREBOARD_TEAM_CLASS, Integer.TYPE, 0);
        SCOREBOARD_TEAM_OPTIONS = Reflection.getField(ReflectionConstants.SCOREBOARD_TEAM_CLASS, Integer.TYPE, 1);
        SCOREBOARD_SCORE_CLASS = Reflection.getMinecraftClass("PacketPlayOutScoreboardScore");
        SCOREBOARD_SCORE_CONSTRUCTOR = Reflection.getConstructor(ReflectionConstants.SCOREBOARD_SCORE_CLASS, (Class<?>[])new Class[0]);
        SCOREBOARD_SCORE_NAME = Reflection.getField(ReflectionConstants.SCOREBOARD_SCORE_CLASS, String.class, 0);
        SCOREBOARD_SCORE_OBJECTIVE = Reflection.getField(ReflectionConstants.SCOREBOARD_SCORE_CLASS, String.class, 1);
        SCOREBOARD_SCORE_SCORE = Reflection.getField(ReflectionConstants.SCOREBOARD_SCORE_CLASS, Integer.TYPE, 0);
        SCOREBOARD_SCORE_ACTION = Reflection.getField(ReflectionConstants.SCOREBOARD_SCORE_CLASS, (Class<?>)Integer.TYPE, 1);
    }
}
