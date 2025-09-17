package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class UndeadCommand extends BaseCommand {
    public UndeadCommand(String name, int permission) {
        super(name, permission);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) (getBaseBuilder()))
                .then(Commands.literal("type")
                        .then(Commands.literal("set")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.literal("human")
                                                .executes(commandContext -> typeSet(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), 0)))
                                        .then(Commands.literal("zombie")
                                                .executes(commandContext -> typeSet(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), 1)))
                                        .then(Commands.literal("husk")
                                                .executes(commandContext -> typeSet(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), 2)))
                                        .then(Commands.literal("drowned")
                                                .executes(commandContext -> typeSet(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), 3)))))

                        .then(Commands.literal("get")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(commandContext -> typeGet(commandContext.getSource(), EntityArgument.getPlayer(commandContext, "target"))))))

                .then(Commands.literal("curing")
                        .then(Commands.literal("start")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(commandContext -> cure(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets")))))

                        .then(Commands.literal("stop")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(commandContext -> stopCure(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"))))))

                .then(Commands.literal("chestextension")
                        .then(Commands.literal("set")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("chest", BoolArgumentType.bool())
                                                .executes(commandContext -> setChest(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), BoolArgumentType.getBool(commandContext, "chest"))))))

                        .then(Commands.literal("has")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(commandContext -> getChest(commandContext.getSource(), EntityArgument.getPlayer(commandContext, "target")))))));
    }

    public static int typeSet(CommandSourceStack sourceStack, Collection<ServerPlayer> players, int type){
        for (ServerPlayer serverPlayer : players){
            BeUndeadApi.setZombieType(serverPlayer, type);
        }

        String typeName = typeToString(type);

        if (players.size() == 1){
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.type.set.single", new Object[]{players.iterator().next().getName(), typeName}), true);
        }
        else {
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.type.set.multiple", new Object[]{players.size(), typeName}), true);
        }

        return players.size();
    }

    public static int typeGet(CommandSourceStack sourceStack, ServerPlayer player){
        int type = BeUndeadApi.getZombieType(player);

        String typeName = typeToString(type);

        sourceStack.sendSuccess(() -> Component.translatable("commands.undead.type.get", new Object[]{player.getName(), typeName}), false);

        return type;
    }

    public static int cure(CommandSourceStack sourceStack, Collection<ServerPlayer> players){
        for (ServerPlayer serverPlayer : players){
            BeUndeadApi.startConverting(serverPlayer, 0, null);
        }

        if (players.size() == 1){
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.curing.start.single", new Object[]{players.iterator().next().getName()}), true);
        }
        else {
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.curing.start.multiple", new Object[]{players.size()}), true);
        }

        return players.size();
    }

    public static int stopCure(CommandSourceStack sourceStack, Collection<ServerPlayer> players){
        for (ServerPlayer serverPlayer : players){
            BeUndeadApi.stopConverting(serverPlayer);
        }

        if (players.size() == 1){
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.curing.stop.single", new Object[]{players.iterator().next().getName()}), true);
        }
        else {
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.curing.stop.multiple", new Object[]{players.size()}), true);
        }

        return players.size();
    }

    public static int setChest(CommandSourceStack sourceStack, Collection<ServerPlayer> players, boolean on){
        for (ServerPlayer serverPlayer : players){
            BeUndeadApi.setZombieChest(serverPlayer, on);
        }

        if (players.size() == 1){
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.chestextension.set.single", new Object[]{players.iterator().next().getName(), String.valueOf(on)}), true);
        }
        else {
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.chestextension.set.multiple", new Object[]{players.size(), String.valueOf(on)}), true);
        }

        return players.size();
    }

    public static int getChest(CommandSourceStack sourceStack, ServerPlayer player){
        boolean has = BeUndeadApi.hasZombieChest(player);

        if (has){
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.chestextension.get.has", new Object[]{player.getName()}), false);
        }
        else {
            sourceStack.sendSuccess(() -> Component.translatable("commands.undead.chestextension.get.has_not", new Object[]{player.getName()}), false);
        }



        return has ? Command.SINGLE_SUCCESS : 0;
    }

    private static String typeToString(int type){
        return switch (type){
            case 1 -> "Zombie";
            case 2 -> "Husk";
            case 3 -> "Drowned";
            default -> "Human";
        };
    }
}