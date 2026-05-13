package me.danilux.simplesteal.config;

import me.danilux.simplesteal.SimpleSteal;

import java.util.List;

public class ConfigManager {

    private final SimpleSteal plugin;
    private final Config langConfig;

    public ConfigManager(SimpleSteal plugin) {
        this.plugin = plugin;
        this.langConfig = new Config(plugin, "lang");
    }

    /**
     * Load all available configurations.
     */
    public void loadAll() {
        this.plugin.saveDefaultConfig();
        this.langConfig.load(
                new ConfigEntry<>("unban-anchor.display-name", "<gold><bold>Unban Anchor</bold></gold>"),
                new ConfigEntry<>("unban-anchor.lore", List.of("<gray>Use it to resurrect</gray>", "<gray>banned players.</gray>")),
                new ConfigEntry<>("heart.display-name", "<red><bold>Heart</bold></red>"),
                new ConfigEntry<>("heart.lore", List.of("<gray>Pick it up to</gray>", "<gray>gain a heart.</gray>")),
                new ConfigEntry<>("no-permission", "<red>You have no permission to do this.</red>"),
                new ConfigEntry<>("doomed", "<dark_red>{player}</dark_red> <red>cannot be unbanned.</red>"),
                new ConfigEntry<>("banned", "<red>You have no hearts left.</red>"),
                new ConfigEntry<>("unban.already", "<dark_red>{player}</dark_red> <red>is already unbanned.</red>"),
                new ConfigEntry<>("unban.not-ls-related", "<dark_red>{player}</dark_red> <red>is banned for not-LifeSteal-related reasons.</red>"),
                new ConfigEntry<>("unban.success", "<dark_green>{player}</dark_green> <green>unbanned successfully.</green>")
        );
    }

    /**
     * Save all available configurations.
     */
    public void saveAll() {
        this.langConfig.save();
    }

    public Config getLang() {
        return this.langConfig;
    }
}
