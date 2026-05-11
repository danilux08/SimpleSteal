package me.danilux.simplesteal.database;

import me.danilux.simplesteal.SimpleSteal;

import java.io.File;
import java.sql.*;

public class SQLiteDB extends Database {

    protected final String name;

    public SQLiteDB(SimpleSteal plugin, String name) {
        super(plugin);
        this.name = name;
    }

    @Override
    public void connect() {
        try {
            this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", String.format("%s/%s.db", this.plugin.getDataFolder(), this.name)));
        } catch(SQLException e) {
            this.plugin.getLogger().severe(String.format("Cannot connect database %s.", this.name));
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.close();
        } catch(SQLException e) {
            this.plugin.getLogger().severe(String.format("Cannot disconnect database %s.", this.name));
        }
    }
}
