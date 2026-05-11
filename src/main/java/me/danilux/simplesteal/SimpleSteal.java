package me.danilux.simplesteal;

import me.danilux.simplesteal.database.DBManager;
import me.danilux.simplesteal.listeners.PlayerListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSteal extends JavaPlugin {

    private final Utils utils = new Utils(this);
    private final DBManager dbManager = new DBManager(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerListeners(new PlayerListener(this));
        dbManager.connectAll();
    }

    @Override
    public void onDisable() {
        dbManager.disconnectAll();
    }

    private void registerListeners(Listener... listeners) {
        for(Listener listener : listeners) this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public Utils getUtils() {
        return this.utils;
    }

    public DBManager getDBManager() {
        return this.dbManager;
    }
}
