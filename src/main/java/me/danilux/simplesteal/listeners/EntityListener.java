package me.danilux.simplesteal.listeners;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EntityListener implements Listener {

    private final LifeStealUtils lifesteal;

    public EntityListener(SimpleSteal plugin) {
        this.lifesteal = plugin.getLifeStealUtils();
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if(!(entity instanceof Player player)) return;
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        ItemMeta meta = stack.getItemMeta();
        String data = meta.getPersistentDataContainer().get(this.lifesteal.getPersistentDataKey(), PersistentDataType.STRING);
        if(data == null) return;
        if(!data.equals("heart-item")) return;
        event.setCancelled(true);
        item.remove();
        this.lifesteal.addHearts(player, 1);
    }
}
