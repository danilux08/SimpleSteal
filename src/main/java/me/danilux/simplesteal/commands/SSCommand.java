package me.danilux.simplesteal.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.utils.FormatUtils;
import me.danilux.simplesteal.utils.lifesteal.LSUnbanResult;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SSCommand implements Command {

    private final SimpleSteal plugin;
    private final FormatUtils format;
    private final LifeStealUtils lifesteal;

    public SSCommand(SimpleSteal plugin) {
        this.plugin = plugin;
        this.format = plugin.getFormatUtils();
        this.lifesteal = plugin.getLifeStealUtils();
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
                                    List<String> bannedPlayers = this.lifesteal.getLSBannedPlayers()
                                            .stream().map(PlayerProfile::getName).toList();
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
}
