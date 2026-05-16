package me.danilux.simplesteal.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.danilux.simplesteal.SimpleSteal;
import me.danilux.simplesteal.utils.FormatUtils;
import me.danilux.simplesteal.utils.lifesteal.LifeStealUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand implements Command {

    private final FormatUtils format;
    private final LifeStealUtils lifesteal;

    public WithdrawCommand(SimpleSteal plugin) {
        this.format = plugin.getFormatUtils();
        this.lifesteal = plugin.getLifeStealUtils();
    }

    @Override
    public String getDescription() {
        return "Convert hearts into tradable items.";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("withdraw")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            if(!(sender instanceof Player player)) {
                                this.format.sendOnlyPlayersMessage(sender);
                                return 0;
                            }
                            int hearts = IntegerArgumentType.getInteger(ctx, "amount");
                            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                            int availableHearts = (attribute != null ? (int) attribute.getValue() / 2 : hearts) - 1;
                            int finalHearts = Math.min(hearts, availableHearts);
                            this.lifesteal.subHearts(player, finalHearts, false);
                            player.getInventory().addItem(this.lifesteal.getHeartStack(finalHearts));
                            return 1;
                        })
                        .suggests((ctx, builder) -> {
                            builder.suggest("<amount>");
                            return builder.buildFuture();
                        })
                ).build();
    }
}
