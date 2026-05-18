package me.danilux.simplesteal.listeners;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.database.impl.DataDB;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.LocalDateTime;

public class PlayerListener implements Listener {

    private final SimpleSteal plugin;
    private final DataDB db;
    private final LifeStealUtils lifesteal;

    public PlayerListener(SimpleSteal plugin) {
        this.plugin = plugin;
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
        LocalDateTime lastUnban = this.db.getLastUnban(victim);
        if(lastUnban != null) {
            LocalDateTime now = LocalDateTime.now();
            /*
                Check if the victim is in the 24-hour grace period.
            */
            if(now.isAfter(lastUnban) && now.isBefore(lastUnban.plusDays(1))) return;
        }
        this.lifesteal.subHearts(victim, 1, false);
        EntityDamageEvent damageEvent = victim.getLastDamageCause();
        if(damageEvent == null) return;
        if(damageEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            Player killer = victim.getKiller();
            if(killer == null) return;
            this.lifesteal.addHearts(killer, 1, false);
        }else {
            /*
                A non-player killed the victim, so he
                drops the heart he lost.
             */
            this.lifesteal.dropHearts(victim.getLocation(), 1);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block block = event.getClickedBlock();
        if(block == null) return;
        BlockState state = block.getState();
        if(!(state instanceof TileState tile)) return;
        PersistentDataContainer container = tile.getPersistentDataContainer();
        String data = container.get(this.lifesteal.getPersistentDataKey(), PersistentDataType.STRING);
        if(data == null) return;
        if(!data.equals("unban-anchor-block")) return;
        Player player = event.getPlayer();
        player.sendMessage("TODO: Open unban menu.");
    }
}
