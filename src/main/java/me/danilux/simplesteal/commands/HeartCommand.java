package me.danilux.simplesteal.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.utils.FormatUtils;
import me.danilux.simplesteal.utils.Placeholder;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HeartCommand implements Command {

    private final FormatUtils format;
    private final LifeStealUtils lifesteal;

    public HeartCommand(SimpleSteal plugin) {
        this.format = plugin.getFormatUtils();
        this.lifesteal = plugin.getLifeStealUtils();
    }

    @Override
    public String getDescription() {
        return "Perform heart operations on players.";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("heart")
                .requires(stack -> stack.getSender().hasPermission("ss.admin"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .then(Commands.literal("query")
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();
                                    if(!sender.hasPermission("ss.admin")) {
                                        this.format.sendNoPermissionMessage(sender);
                                        return 0;
                                    }
                                    String playerName = StringArgumentType.getString(ctx, "player");
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
                                    int hearts = this.lifesteal.getHearts(player);
                                    this.format.sendSenderMessage(sender, this.format.formatConfigText("hearts.queried", this.format.getPlayerPlaceholder(player), this.getHeartsPlaceholder(hearts)));
                                    return 1;
                                })
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            CommandSender sender = ctx.getSource().getSender();
                                            if(!sender.hasPermission("ss.admin")) {
                                                this.format.sendNoPermissionMessage(sender);
                                                return 0;
                                            }
                                            String playerName = StringArgumentType.getString(ctx, "player");
                                            Player player = Bukkit.getPlayer(playerName);
                                            if(player == null) {
                                                this.format.sendNotOnlineMessage(sender, playerName);
                                                return 0;
                                            }
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            boolean success = this.lifesteal.addHearts(player, amount, true);
                                            if(success) {
                                                this.format.sendSenderMessage(sender, this.format.formatConfigText("hearts.added", this.format.getPlayerPlaceholder(player), this.getHeartsPlaceholder(amount)));
                                                return 1;
                                            }
                                            this.sendFailMessage(sender, player);
                                            return 0;
                                        })
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            CommandSender sender = ctx.getSource().getSender();
                                            if(!sender.hasPermission("ss.admin")) {
                                                this.format.sendNoPermissionMessage(sender);
                                                return 0;
                                            }
                                            String playerName = StringArgumentType.getString(ctx, "player");
                                            Player player = Bukkit.getPlayer(playerName);
                                            if(player == null) {
                                                this.format.sendNotOnlineMessage(sender, playerName);
                                                return 0;
                                            }
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            boolean success = this.lifesteal.subHearts(player, amount, true);
                                            if(success) {
                                                this.format.sendSenderMessage(sender, this.format.formatConfigText("hearts.removed", this.format.getPlayerPlaceholder(player), this.getHeartsPlaceholder(amount)));
                                                return 1;
                                            }
                                            this.sendFailMessage(sender, player);
                                            return 0;
                                        })
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            CommandSender sender = ctx.getSource().getSender();
                                            if(!sender.hasPermission("ss.admin")) {
                                                this.format.sendNoPermissionMessage(sender);
                                                return 0;
                                            }
                                            String playerName = StringArgumentType.getString(ctx, "player");
                                            Player player = Bukkit.getPlayer(playerName);
                                            if(player == null) {
                                                this.format.sendNotOnlineMessage(sender, playerName);
                                                return 0;
                                            }
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            boolean success = this.lifesteal.setHearts(player, amount, true);
                                            if(success) {
                                                this.format.sendSenderMessage(sender, this.format.formatConfigText("hearts.set", this.format.getPlayerPlaceholder(player), this.getHeartsPlaceholder(amount)));
                                                return 1;
                                            }
                                            this.sendFailMessage(sender, player);
                                            return 0;
                                        })
                                )
                        )
                        .suggests((ctx, builder) -> {
                            List<String> onlinePlayers = this.getOnlinePlayers();
                            onlinePlayers.forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                ).build();
    }

    private List<String> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }

    private Placeholder<Integer> getHeartsPlaceholder(int hearts) {
        return new Placeholder<>("hearts", hearts);
    }

    private void sendFailMessage(CommandSender sender, Player player) {
        this.format.sendSenderMessage(sender, this.format.formatConfigText("cannot-change", this.format.getPlayerPlaceholder(player)));
    }
}
