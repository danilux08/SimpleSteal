package me.danilux.simplesteal.utils.lifesteal;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.database.impl.DataDB;
import me.danilux.simplesteal.utils.FormatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
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

    private Material getConfigMaterial(String key) {
        return Registry.MATERIAL.get(NamespacedKey.minecraft(this.plugin.getConfig().getString(key, "stone")));
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

    public ItemStack getHeartStack(int amount) {
        ItemStack stack = new ItemStack(this.getConfigMaterial("heart.material"), amount);
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

    public ItemStack getUnbanAnchorStack(int amount) {
        ItemStack stack = new ItemStack(this.getConfigMaterial("unban-anchor"), amount);
        ItemMeta meta = stack.getItemMeta();
        Component displayName = this.format.formatConfigText("unban-anchor.display-name");
        if(displayName != null) meta.displayName(displayName);
        meta.lore(this.format.formatConfigTextList("unban-anchor.lore"));
        meta.getPersistentDataContainer().set(persistentDataKey, PersistentDataType.STRING, "unban-anchor-item");
        stack.setItemMeta(meta);
        return stack;
    }

    public int getHearts(OfflinePlayer player) {
        return this.db.getHearts(player);
    }

    public boolean setHearts(Player player, int amount, boolean bypassBan) {
        if(player.hasPermission("ss.immune")) return false;
        int finalHearts = Math.max(0, amount);
        this.db.setHearts(player, finalHearts);
        if(!bypassBan && amount <= 0) this.ban(player);
        this.updateHearts(player, finalHearts);
        return true;
    }

    public boolean addHearts(Player player, int amount, boolean bypassBan) {
        int maxHearts = getMaxHearts();
        int currentHearts = this.getHearts(player);
        int newHearts = currentHearts + amount;
        int delta = newHearts - maxHearts;
        boolean success = this.setHearts(player, Math.min(newHearts, maxHearts), bypassBan);
        if(success && delta > 0) this.dropHearts(player.getLocation(), delta);
        return success;
    }

    public boolean subHearts(Player player, int amount, boolean bypassBan) {
        if(player.hasPermission("ss.god")) return false;
        int currentHearts = this.getHearts(player);
        return this.setHearts(player, currentHearts - amount, bypassBan);
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

    /**
     * Highlight a player for a specific time.
     */
    public void tempHighlightPlayer(Player player, int minutes) {
        player.setGlowing(true);
        int seconds = minutes * 60;
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            player.setGlowing(false);
        }, 20L * seconds);
    }

    public NamespacedKey getPersistentDataKey() {
        return this.persistentDataKey;
    }
}
