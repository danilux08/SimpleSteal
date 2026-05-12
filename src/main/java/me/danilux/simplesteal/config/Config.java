package me.danilux.simplesteal.config;

import me.danilux.simplesteal.SimpleSteal;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Config {

    private final SimpleSteal plugin;
    private final File file;
    private final HashMap<String, Object> defaults;
    private YamlConfiguration yaml;

    public Config(SimpleSteal plugin, String name) {
        this.plugin = plugin;
        this.defaults = new HashMap<>();
        this.file = new File(plugin.getDataFolder(), String.format("%s.yml", name));
    }

    public void load(ConfigEntry<?>... defaults) {
        try {
            this.yaml = YamlConfiguration.loadConfiguration(this.file);
            if(defaults.length < 1) return;
            for(ConfigEntry<?> def : defaults) {
                this.defaults.put(def.key(), def.value());
                if(this.yaml.contains(def.key())) continue;
                this.yaml.set(def.key(), def.value());
            }
        } catch(Exception e) {
            this.plugin.getLogger().severe(String.format("Cannot load config: %s", e.getMessage()));
        }
        save();
    }

    public void save() {
        try {
            this.yaml.save(this.file);
        } catch(IOException e) {
            this.plugin.getLogger().severe(String.format("Cannot save config: %s", e.getMessage()));
        }
    }

    public void reload() {
        this.save();
        this.load();
    }

    public FileConfiguration get() {
        return this.yaml;
    }

    public Object getDefault(String key) {
        return this.defaults.get(key);
    }
}
