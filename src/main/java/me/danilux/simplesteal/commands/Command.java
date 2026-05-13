package me.danilux.simplesteal.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Collections;
import java.util.List;

public interface Command {

    default String getDescription() {
        return null;
    }

    default List<String> getAliases() {
        return Collections.emptyList();
    }

    LiteralCommandNode<CommandSourceStack> build();
}
