package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.config.*;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.function.BiConsumer;

public class UndeadModeCommand extends BaseCommand {
    public UndeadModeCommand(String name, int permission) {
        super(name, permission);
    }

    private static void changed(MinecraftServer server){
        ModNetworking.sendToAllClients(ServerModConfig.getPacket());
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) (getBaseBuilder())).executes(commandContext -> getBoolean(commandContext.getSource())).then(Commands.argument("value", BoolArgumentType.bool()).executes(commandContext -> changeBoolean(commandContext.getSource(), BoolArgumentType.getBool(commandContext, "value")))));
    }

    public static int changeBoolean(CommandSourceStack sourceStack, boolean value){
        PerWorldConfig.get().undeadMode = value;
        sourceStack.sendSuccess(() -> Component.translatable("commands.undeadmode.changed", new Object[]{String.valueOf(value)}), true);
        changed(sourceStack.getServer());
        return Command.SINGLE_SUCCESS;
    }

    public static int getBoolean(CommandSourceStack sourceStack){
        boolean value = PerWorldConfig.get().undeadMode;
        sourceStack.sendSuccess(() -> Component.translatable("commands.undeadmode.get", new Object[]{String.valueOf(value)}), false);
        return value ? Command.SINGLE_SUCCESS : 0;
    }
}