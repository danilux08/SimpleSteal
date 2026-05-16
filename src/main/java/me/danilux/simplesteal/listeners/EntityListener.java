package me.danilux.simplesteal.listeners;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EntityListener implements Listener {

    private final SimpleSteal plugin;
    private final LifeStealUtils lifesteal;

    public EntityListener(SimpleSteal plugin) {
        this.plugin = plugin;
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
        this.lifesteal.addHearts(player, 1, false);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if(!(damaged instanceof Player damagedPlayer)) return;
        Entity damager = event.getDamager();
        if(!(damager instanceof Player damagerPlayer)) return;
        if(!this.isWeapon(damagerPlayer.getInventory().getItemInMainHand())) return;
        if(!this.plugin.getConfig().getBoolean("highlight-maxed", false)) return;
        this.lifesteal.tempHighlightPlayer(damagedPlayer, 1);
    }

    private boolean isWeapon(ItemStack item) {
        if(item == null) return false;
        return switch(item.getType()) {
            case WOODEN_SWORD,
                 STONE_SWORD,
                 IRON_SWORD,
                 GOLDEN_SWORD,
                 DIAMOND_SWORD,
                 NETHERITE_SWORD,
                 WOODEN_AXE,
                 STONE_AXE,
                 IRON_AXE,
                 GOLDEN_AXE,
                 DIAMOND_AXE,
                 NETHERITE_AXE -> true;
            default -> false;
        };
    }
}
