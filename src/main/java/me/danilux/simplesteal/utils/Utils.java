package me.danilux.simplesteal.utils;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.config.Config;
import me.danilux.simplesteal.database.impl.DataDB;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private final SimpleSteal plugin;
    private final MiniMessage mm;
    private final Config lang;
    private final DataDB db;
    private final NamespacedKey persistentDataKey;

    public Utils(SimpleSteal plugin) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();
        this.lang = this.plugin.getConfigManager().getLang();
        this.db = this.plugin.getDBManager().getDataDB();
        this.persistentDataKey = new NamespacedKey(plugin, "persistentData");
    }

    public Component formatColor(String str) {
        return this.mm.deserialize(str);
    }

    public Placeholder<String> getPlayerPlaceholder(Player player) {
        return new Placeholder<>("player", player.getName());
    }

    public <T> String formatPlaceholder(String str, Placeholder<T> placeholder) {
        return str.replaceAll(String.format("{%s}", placeholder.name()), placeholder.value().toString());
    }

    public Component formatText(String text, Placeholder<?>... placeholders) {
        String message = text;
        if(message.isEmpty()) return null;
        for(Placeholder<?> p : placeholders) message = formatPlaceholder(message, p);
        return this.formatColor(message);
    }

    public Component formatText(Config config, String key, Placeholder<?>... placeholders) {
        return this.formatText(config.get().getString(key, config.getDefault(key).toString()), placeholders);
    }

    public List<Component> formatTextList(Config config, String key, Placeholder<?>... placeholders) {
        List<String> lore = this.lang.get().getStringList(key);
        List<Component> formattedLore = new ArrayList<>();
        if(!lore.isEmpty()) for(String row : lore) formattedLore.add(this.formatText(row, placeholders));
        return formattedLore;
    }

    public void sendMessage(Player player, Component message) {
        if(message.equals(Component.empty())) return;
        player.sendMessage(message);
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
        Component displayName = this.formatText(this.lang, "heart.display-name");
        if(displayName != null) meta.displayName(displayName);
        meta.lore(this.formatTextList(this.lang, "heart.lore"));
        meta.getPersistentDataContainer().set(persistentDataKey, PersistentDataType.STRING, "heart-item");
        stack.setItemMeta(meta);
        return stack;
    }

    public void dropHearts(Location loc, int amount) {
        loc.getWorld().dropItem(loc, this.getHeartStack(amount));
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

    public boolean isBanned(Player player) {
        return this.db.isBanned(player);
    }

    public void ban(Player player) {
        this.db.ban(player);
        this.db.setHearts(player, 0);
        Component message = this.formatText(this.lang, "banned");
        if(message == null) return;
        player.kick(message);
    }

//    public Component unban(Player player, boolean ignoreDoomed) {
//        if(!ignoreDoomed && player.hasPermission("ss.doomed")) return this.formatText(this.lang, "doomed", this.getPlayerPlaceholder(player));
//        this.db.unban(player);
//        this.db.setHearts(player, this.getUnbanHearts());
//        return null;
//    }

    public NamespacedKey getPersistentDataKey() {
        return this.persistentDataKey;
    }
}
