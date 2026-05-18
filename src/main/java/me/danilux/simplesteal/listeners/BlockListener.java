package me.danilux.simplesteal.listeners;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockListener implements Listener {

    private final LifeStealUtils lifesteal;

    public BlockListener(SimpleSteal plugin) {
        this.lifesteal = plugin.getLifeStealUtils();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String data = container.get(this.lifesteal.getPersistentDataKey(), PersistentDataType.STRING);
        if(data == null) return;
        if(!data.equals("unban-anchor-item")) return;
        Block block = event.getBlock();
        BlockState state = block.getState();
        if(!(state instanceof TileState tile)) return;
        PersistentDataContainer container1 = tile.getPersistentDataContainer();
        container1.set(this.lifesteal.getPersistentDataKey(), PersistentDataType.STRING, "unban-anchor-block");
        state.update();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        BlockState state = block.getState();
        if(!(state instanceof TileState tile)) return;
        PersistentDataContainer container = tile.getPersistentDataContainer();
        String data = container.get(this.lifesteal.getPersistentDataKey(), PersistentDataType.STRING);
        if(data == null) return;
        if(!data.equals("unban-anchor-block")) return;
        Player player = event.getPlayer();
        if(player.getGameMode().equals(GameMode.CREATIVE)) return;
        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), this.lifesteal.getUnbanAnchorStack(1));
    }
}
