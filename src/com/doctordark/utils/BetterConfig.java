package com.doctordark.utils;

/**
 * Created by Missionary (missionarymc@gmail.com) on 8/9/2017.
 */

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BetterConfig {

    private JavaPlugin plugin;
    private String configName;
    private String folder;

    private File configurationFile;
    private FileConfiguration configuration;

    public BetterConfig(JavaPlugin plugin, String configName, String folderName) {

        if (plugin == null) {

            throw new IllegalStateException("Plugin must not be null!");

        }

        this.plugin = plugin;
        this.configName = configName;
        folder = folderName;

        if (folder == null) {

            this.configurationFile = new File(plugin.getDataFolder(), configName);

        } else {

            this.configurationFile = new File(plugin.getDataFolder() + "/" + folder, configName);

        }

    }

    public FileConfiguration getConfiguration() {

        if (configuration == null) {

            this.reloadConfig();

        }

        return configuration;

    }

    public File getFile() {

        return configurationFile;

    }

    public void reloadConfig() {

        configuration = YamlConfiguration.loadConfiguration(configurationFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource(configName);

        if (defConfigStream != null) {

            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            configuration.setDefaults(defConfig);

        }

    }

    public void saveConfig() {

        if (configuration != null && configurationFile != null) {

            try {

                getConfiguration().save(configurationFile);

            } catch (IOException ex) {

                plugin.getLogger().info("Configuration save failed!");

            }

        }

    }

    public void saveDefaultConfig() {

        if (!configurationFile.exists()) {

            this.plugin.saveResource(configName, false);

        }

    }

    public void deleteConfig() {

        configurationFile.delete();

    }
}