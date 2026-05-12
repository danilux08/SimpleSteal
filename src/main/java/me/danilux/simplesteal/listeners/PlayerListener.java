package me.danilux.simplesteal.listeners;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.config.Config;
import me.danilux.simplesteal.database.impl.DataDB;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final SimpleSteal plugin;
    private final Config lang;
    private final DataDB db;

    public PlayerListener(SimpleSteal plugin) {
        this.plugin = plugin;
        this.lang = this.plugin.getConfigManager().getLang();
        this.db = plugin.getDBManager().getDataDB();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.db.registerPlayer(player);
        int currentHearts = this.plugin.getUtils().getHearts(player);
        this.plugin.getUtils().updateHearts(player, currentHearts);
        boolean banned = this.plugin.getUtils().isBanned(player);
        if(!banned) return;
        player.setGameMode(GameMode.SPECTATOR);
        this.plugin.getUtils().sendMessage(player, this.plugin.getUtils().formatText(this.lang, "banned"));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        this.plugin.getUtils().subHearts(victim, 1);
        EntityDamageEvent damageEvent = victim.getLastDamageCause();
        if(damageEvent == null) return;
        if(damageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            Player killer = victim.getKiller();
            if(killer == null) return;
            this.plugin.getUtils().addHearts(killer, 1);
        }else {
            this.plugin.getUtils().dropHearts(victim.getLocation(), 1);
        }
    }
}
