package me.danilux.simplesteal.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import me.danilux.simplesteal.SimpleSteal;
import net.kyori.adventure.text.Component;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Set;

public class BanUtils {

    private final SimpleSteal plugin;
    private final BanList<PlayerProfile> bans;

    public BanUtils(SimpleSteal plugin) {
        this.plugin = plugin;
        this.bans = Bukkit.getBanList(BanListType.PROFILE);
    }

    public Set<BanEntry<PlayerProfile>> getBans() {
        return this.bans.getEntries();
    }

    public boolean isBanned(OfflinePlayer target) {
        return this.bans.isBanned(target.getPlayerProfile());
    }

    public void tempBan(OfflinePlayer target, Component reason, Date expires, String source) {
        this.bans.addBan(target.getPlayerProfile(), this.plugin.getFormatUtils().stringifyColor(reason), expires, source);
        if(!target.isOnline()) return;
        Player onlineTarget = (Player) target;
        onlineTarget.kick(reason);
    }

    public void ban(OfflinePlayer target, Component reason, String source) {
        this.tempBan(target, reason, null, source);
    }

    public void unban(OfflinePlayer target) {
        this.bans.pardon(target.getPlayerProfile());
    }

    public String getBanSource(OfflinePlayer target) {
        if(!this.isBanned(target)) return null;
        BanEntry<PlayerProfile> entry = this.bans.getBanEntry(target.getPlayerProfile());
        return entry.getSource();
    }
}
