package com.doctordark.utils;

import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.*;
import java.io.*;

public class Config extends YamlConfiguration {
    private final String fileName;
    private final JavaPlugin plugin;

    public Config(final JavaPlugin plugin, final String fileName) {
        this(plugin, fileName, ".yml");
    }

    public Config(final JavaPlugin plugin, final String fileName, final String fileExtension) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        this.createFile();
    }

    public String getFileName() {
        return this.fileName;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    private void createFile() {
        final File folder = this.plugin.getDataFolder();
        try {
            final File file = new File(folder, this.fileName);
            if (!file.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    this.save(file);
                }
                this.load(file);
            } else {
                this.load(file);
                this.save(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        final File folder = this.plugin.getDataFolder();
        try {
            this.save(new File(folder, this.fileName));//nvm im just stuipid
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }
        final Config config = (Config) o;
        Label_0054: {
            if (this.fileName != null) {
                if (this.fileName.equals(config.fileName)) {
                    break Label_0054;
                }
            } else if (config.fileName == null) {
                break Label_0054;
            }
            return false;
        }
        if (this.plugin != null) {
            if (!this.plugin.equals((Object) config.plugin)) {
                return false;
            }
        } else if (config.plugin != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = (this.fileName != null) ? this.fileName.hashCode() : 0;
        result = 31 * result + ((this.plugin != null) ? this.plugin.hashCode() : 0);
        return result;
    }
}
