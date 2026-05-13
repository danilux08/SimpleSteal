package me.danilux.simplesteal.utils;

import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FormatUtils {

    private final MiniMessage mm;
    private final Config lang;

    public FormatUtils(SimpleSteal plugin) {
        this.mm = MiniMessage.miniMessage();
        this.lang = plugin.getConfigManager().getLang();
    }

    public Component formatColor(String str) {
        return this.mm.deserialize(str);
    }

    public String stringifyColor(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public Placeholder<String> getPlayerPlaceholder(OfflinePlayer player) {
        return new Placeholder<>("player", player.getName());
    }

    public <T> String formatPlaceholder(String str, Placeholder<T> placeholder) {
        return str.replaceAll(String.format("\\{%s}", placeholder.name()), placeholder.value().toString());
    }

    public Component formatText(String text, Placeholder<?>... placeholders) {
        String message = text;
        if(message.isEmpty()) return null;
        for(Placeholder<?> p : placeholders) message = formatPlaceholder(message, p);
        return this.formatColor(message);
    }

    public Component formatConfigText(String key, Placeholder<?>... placeholders) {
        return this.formatText(this.lang.get().getString(key, this.lang.getDefault(key).toString()), placeholders);
    }

    public List<Component> formatConfigTextList(String key, Placeholder<?>... placeholders) {
        List<String> lore = this.lang.get().getStringList(key);
        List<Component> formattedLore = new ArrayList<>();
        if(!lore.isEmpty()) for(String row : lore) formattedLore.add(this.formatText(row, placeholders));
        return formattedLore;
    }

    public void sendMessage(Player player, Component message) {
        if(message.equals(Component.empty())) return;
        player.sendMessage(message);
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        Component message = this.formatConfigText("no-permission");
        if(message.equals(Component.empty())) return;
        sender.sendMessage(message);
    }
}
