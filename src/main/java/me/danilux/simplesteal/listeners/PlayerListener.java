package me.danilux.simplesteal.listeners;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.database.impl.DataDB;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final DataDB db;
    private final LifeStealUtils lifesteal;

    public PlayerListener(SimpleSteal plugin) {
        this.db = plugin.getDBManager().getDataDB();
        this.lifesteal = plugin.getLifeStealUtils();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.db.registerPlayer(player);
        int currentHearts = this.lifesteal.getHearts(player);
        this.lifesteal.updateHearts(player, currentHearts);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        this.lifesteal.subHearts(victim, 1);
        EntityDamageEvent damageEvent = victim.getLastDamageCause();
        if(damageEvent == null) return;
        if(damageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            Player killer = victim.getKiller();
            if(killer == null) return;
            this.lifesteal.addHearts(killer, 1);
        }else {
            /*
                A non-player killed the victim, so he
                drops the heart he lost.
             */
            this.lifesteal.dropHearts(victim.getLocation(), 1);
        }
    }
}
