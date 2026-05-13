package me.danilux.simplesteal.database.impl;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.database.QueryParameter;
import me.danilux.simplesteal.database.types.SQLiteDB;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;

public class DataDB extends SQLiteDB {

    private final SimpleSteal plugin;

    public DataDB(SimpleSteal plugin) {
        super(plugin, "data");
        this.plugin = plugin;
    }

    public void generatePlayersTable() {
        this.mutate("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY NOT NULL, hearts INTEGER NOT NULL DEFAULT 10);");
    }

    /**
     * Inserts the player into the DB.
     */
    public void registerPlayer(OfflinePlayer player) {
        this.mutate("INSERT OR IGNORE INTO players (uuid) VALUES (?);", new QueryParameter<>(1, player.getUniqueId()));
    }

    public int getHearts(OfflinePlayer player) {
        return this.query("SELECT hearts FROM players WHERE uuid = ?;", result -> {
            try {
                return result.getInt("hearts");
            } catch(SQLException e) {
                this.plugin.getLogger().severe(String.format("Cannot get %s's hearts: %s", player.getName(), e.getMessage()));
                return 10;
            }
        }, new QueryParameter<>(1, player.getUniqueId()));
    }

    public void setHearts(OfflinePlayer player, int amount) {
        this.mutate("UPDATE players SET hearts = ? WHERE uuid = ?;", new QueryParameter<>(1, amount), new QueryParameter<>(2, player.getUniqueId()));
    }
}
