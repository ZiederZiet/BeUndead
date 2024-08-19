package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BaseCommand {
    protected LiteralArgumentBuilder<CommandSourceStack> builder;

    public BaseCommand(String name, int permission) {
        this.builder = Commands.literal(name).requires(source -> source.hasPermission(permission));
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
    }

    public LiteralArgumentBuilder getBaseBuilder(){
        return this.builder;
    }
}
