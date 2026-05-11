package me.danilux.simplesteal.database;

import me.danilux.simplesteal.SimpleSteal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public abstract class Database {

    protected final SimpleSteal plugin;
    protected Connection connection;

    public Database(SimpleSteal plugin) {
        this.plugin = plugin;
    }

    public abstract void connect();

    /**
     * Executes actions returning nothing.
     */
    public void mutate(String sql, QueryParameter<?>... parameters) {
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            applyParameters(stmt, parameters);
            stmt.execute();
        } catch(SQLException e) {
            this.plugin.getLogger().severe(String.format("Cannot execute statement: %s", e.getMessage()));
        }
    }

    /**
     * Execute actions returning something.
     */
    public <T> T query(String sql, Function<ResultSet, T> callback, QueryParameter<?>... parameters) {
        T returnValue = null;
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            applyParameters(stmt, parameters);
            ResultSet result = stmt.executeQuery();
            returnValue = callback.apply(result);
            result.close();
        } catch(SQLException e) {
            this.plugin.getLogger().severe(String.format("Cannot execute statement: %s", e.getMessage()));
        }
        return returnValue;
    }

    public abstract void disconnect();

    private void applyParameters(PreparedStatement stmt, QueryParameter<?>... parameters) {
        if(parameters.length < 1) return;
        try {
            for(QueryParameter<?> param : parameters) stmt.setObject(param.index(), param.value().toString());
        } catch(SQLException e) {
            this.plugin.getLogger().severe(String.format("Cannot apply query parameters: %s", e.getMessage()));
        }
    }
}
