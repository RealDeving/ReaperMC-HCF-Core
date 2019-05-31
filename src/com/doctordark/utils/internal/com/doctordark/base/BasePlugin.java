package com.doctordark.utils.internal.com.doctordark.base;

import java.io.IOException;
import java.util.Random;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.doctordark.utils.PersistableLocation;
import com.doctordark.utils.SignHandler;
import com.doctordark.utils.other.chat.Lang;
import com.doctordark.utils.other.cuboid.Cuboid;
import com.doctordark.utils.other.cuboid.NamedCuboid;
import com.doctordark.utils.other.itemdb.ItemDb;
import com.doctordark.utils.other.itemdb.SimpleItemDb;

public class BasePlugin {
    private Random random;
    private ItemDb itemDb;
    private SignHandler signHandler;
    private static BasePlugin plugin;
    private JavaPlugin javaPlugin;

    private BasePlugin() {
        this.random = new Random();
    }

    public void init(final JavaPlugin plugin) {
        this.javaPlugin = plugin;
        ConfigurationSerialization.registerClass(PersistableLocation.class);
        ConfigurationSerialization.registerClass(Cuboid.class);
        ConfigurationSerialization.registerClass(NamedCuboid.class);
        this.itemDb = new SimpleItemDb(plugin);
        this.signHandler = new SignHandler(plugin);
        try {
            Lang.initialize("en_US");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void disable() {
        this.signHandler.cancelTasks(null);
        this.javaPlugin = null;
        BasePlugin.plugin = null;
    }

    public Random getRandom() {
        return this.random;
    }

    public ItemDb getItemDb() {
        return this.itemDb;
    }

    public SignHandler getSignHandler() {
        return this.signHandler;
    }

    public static BasePlugin getPlugin() {
        return BasePlugin.plugin;
    }

    public JavaPlugin getJavaPlugin() {
        return this.javaPlugin;
    }

    static {
        BasePlugin.plugin = new BasePlugin();
    }
}
