package fr.tofuxia.zaap.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class Command {

    public Command(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> literal = Commands.literal(getCommandName());
        buildCommand(literal, context);
        dispatcher.register(literal);
    }

    abstract void buildCommand(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext context);

    abstract String getCommandName();
}
