package me.danilux.simplesteal.database;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.database.impl.DataDB;

public class DBManager {

    private final DataDB dataDB;

    public DBManager(SimpleSteal plugin) {
        this.dataDB = new DataDB(plugin);
    }

    /**
     * Connect all available databases.
     */
    public void connectAll() {
        this.dataDB.connect();
        this.dataDB.generatePlayersTable();
    }

    /**
     * Disconnect all available databases.
     */
    public void disconnectAll() {
        this.dataDB.disconnect();
    }

    public DataDB getDataDB() {
        return this.dataDB;
    }
}
