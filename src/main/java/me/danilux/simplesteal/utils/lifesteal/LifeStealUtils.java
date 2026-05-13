package me.danilux.simplesteal.utils.lifesteal;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.database.impl.DataDB;
import me.danilux.simplesteal.utils.FormatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class LifeStealUtils {

    private final SimpleSteal plugin;
    private final DataDB db;
    private final FormatUtils format;
    private final NamespacedKey persistentDataKey;

    public LifeStealUtils(SimpleSteal plugin) {
        this.plugin = plugin;
        this.db = plugin.getDBManager().getDataDB();
        this.format = plugin.getFormatUtils();
        this.persistentDataKey = new NamespacedKey(plugin, "persistentData");
    }

    public int getUnbanHearts() {
        return this.plugin.getConfig().getInt("unban-hearts", 4);
    }

    public int getMaxHearts() {
        int maxHearts = this.plugin.getConfig().getInt("max-hearts", 30);
        return maxHearts == 0 ? Integer.MAX_VALUE : maxHearts;
    }

    public void updateHearts(Player player, int amount) {
        AttributeInstance att = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(att == null) return;
        att.setBaseValue(amount * 2);
    }

    private ItemStack getHeartStack(int amount) {
        ItemStack stack = new ItemStack(Material.APPLE, amount);
        ItemMeta meta = stack.getItemMeta();
        Component displayName = this.format.formatConfigText("heart.display-name");
        if(displayName != null) meta.displayName(displayName);
        meta.lore(this.format.formatConfigTextList("heart.lore"));
        meta.getPersistentDataContainer().set(persistentDataKey, PersistentDataType.STRING, "heart-item");
        stack.setItemMeta(meta);
        return stack;
    }

    public void dropHearts(Location loc, int amount) {
        if(amount < 1) return;
        for(int i = 0; i < amount; i++) loc.getWorld().dropItem(loc, this.getHeartStack(1));
    }

    public int getHearts(Player player) {
        return this.db.getHearts(player);
    }

    public void addHearts(Player player, int amount) {
        if(player.hasPermission("ss.immune")) return;
        int maxHearts = getMaxHearts();
        int currentHearts = this.getHearts(player);
        int newHearts = currentHearts + amount;
        if(newHearts > maxHearts) {
            int heartsToDrop = newHearts - maxHearts;
            this.dropHearts(player.getLocation(), amount);
            newHearts -= heartsToDrop;
        }
        this.db.setHearts(player, newHearts);
        this.updateHearts(player, newHearts);
    }

    public void subHearts(Player player, int amount) {
        if(player.hasPermission("ss.god")) return;
        if(player.hasPermission("ss.immune")) return;
        int currentHearts = this.getHearts(player);
        int newHearts = currentHearts - amount;
        if(newHearts <= 0) {
            this.ban(player);
            return;
        }
        this.db.setHearts(player, newHearts);
        this.updateHearts(player, newHearts);
    }

    /**
     * Bans players for LifeSteal reasons.
     */
    public void ban(OfflinePlayer player) {
        this.db.setHearts(player, 0);
        this.plugin.getBanUtils().ban(player, this.format.formatConfigText("banned"), this.plugin.getName());
    }

    /**
     * Unbans players for LifeSteal reasons.
     */
    public LSUnbanResult unban(OfflinePlayer player) {
        String banSource = this.plugin.getBanUtils().getBanSource(player);
        if(banSource == null) return LSUnbanResult.ALREADY;
        if(!banSource.equals(this.plugin.getName())) return LSUnbanResult.NOT_LS_RELATED;
        this.db.setHearts(player, this.getUnbanHearts());
        this.plugin.getBanUtils().unban(player);
        return LSUnbanResult.SUCCESS;
    }

    public NamespacedKey getPersistentDataKey() {
        return this.persistentDataKey;
    }
}
