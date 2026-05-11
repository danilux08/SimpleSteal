package me.danilux.simplesteal.listeners;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.database.impl.DataDB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final SimpleSteal plugin;
    private final DataDB db;

    public PlayerListener(SimpleSteal plugin) {
        this.plugin = plugin;
        this.db = plugin.getDBManager().getDataDB();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.db.registerPlayer(player);
        player.sendMessage(String.format("You have %s hearts.", this.db.getHearts(player)));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        this.db.subHearts(victim, 1);
        EntityDamageEvent damageEvent = victim.getLastDamageCause();
        if(damageEvent == null) return;
        if(damageEvent.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        Player killer = victim.getKiller();
        if(killer == null) return;
        this.db.addHearts(killer, 1);
        victim.sendMessage(String.format("You were killed by %s.", killer.getName()));
    }
}
