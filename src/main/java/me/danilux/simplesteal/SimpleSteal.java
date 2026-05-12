package me.danilux.simplesteal;

import me.danilux.simplesteal.config.ConfigManager;
import me.danilux.simplesteal.database.DBManager;
import me.danilux.simplesteal.listeners.EntityListener;
import me.danilux.simplesteal.listeners.PlayerListener;
import me.danilux.simplesteal.utils.Utils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSteal extends JavaPlugin {

    private final ConfigManager configManager = new ConfigManager(this);
    private final DBManager dbManager = new DBManager(this);
    private final Utils utils = new Utils(this);

    @Override
    public void onEnable() {
        this.getLogger().info("Registering events...");
        registerListeners(new EntityListener(this), new PlayerListener(this));
        this.getLogger().info("Loading configurations...");
        this.configManager.loadAll();
        this.getLogger().info("Connecting databases...");
        this.dbManager.connectAll();
        this.getLogger().info("Done!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disconnecting databases...");
        this.dbManager.disconnectAll();
        this.getLogger().info("Saving configurations...");
        this.configManager.saveAll();
        this.getLogger().info("Bye, bye!");
    }

    private void registerListeners(Listener... listeners) {
        for(Listener listener : listeners) this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public Utils getUtils() {
        return this.utils;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public DBManager getDBManager() {
        return this.dbManager;
    }
}
