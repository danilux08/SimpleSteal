package me.danilux.simplesteal.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.utils.BanUtils;
import me.danilux.simplesteal.utils.FormatUtils;
import me.danilux.simplesteal.utils.lifesteal.LSUnbanResult;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SSCommand implements Command {

    private final SimpleSteal plugin;
    private final FormatUtils format;
    private final BanUtils bans;

    public SSCommand(SimpleSteal plugin) {
        this.plugin = plugin;
        this.format = plugin.getFormatUtils();
        this.bans = plugin.getBanUtils();
    }

    @Override
    public String getDescription() {
        return "SimpleSteal main command.";
    }

    @Override
    public List<String> getAliases() {
        return List.of("simples", "ssteal", "ss");
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("simplesteal")
                .requires(stack -> stack.getSender().hasPermission("ss.admin"))
                .then(Commands.literal("unban")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();
                                    if(!sender.hasPermission("ss.admin")) {
                                        this.format.sendNoPermissionMessage(sender);
                                        return 0;
                                    }
                                    String playerName = StringArgumentType.getString(ctx, "player");
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
                                    LSUnbanResult result = this.plugin.getLifeStealUtils().unban(player);
                                    this.format.sendSenderMessage(sender, this.format.formatConfigText(result.messageKey, this.format.getPlayerPlaceholder(player)));
                                    return 1;
                                })
                                .suggests((ctx, builder) -> {
                                    List<String> bannedPlayers = this.getLSBannedPlayers();
                                    bannedPlayers.forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                        )
                ).then(Commands.literal("reload")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            if(!sender.hasPermission("ss.admin")) {
                                this.format.sendNoPermissionMessage(sender);
                                return 0;
                            }
                            this.plugin.reload();
                            this.format.sendSenderMessage(sender, this.format.formatConfigText("reloaded"));
                            return 1;
                        })
                ).build();
    }

    private List<String> getLSBannedPlayers() {
        Set<BanEntry<PlayerProfile>> bans = this.bans.getBans();
        /*
            Get the list of banned players for LifeSteal purposes.
        */
        Set<BanEntry<PlayerProfile>> lsBans = bans.stream()
                .filter(ban -> ban.getSource().equals(this.plugin.getName()))
                .collect(Collectors.toSet());
        return lsBans.stream()
                .map(entry -> entry.getBanTarget().getName())
                .toList();
    }
}
