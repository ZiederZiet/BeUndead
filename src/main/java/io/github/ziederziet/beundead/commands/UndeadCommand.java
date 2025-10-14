package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.UndeadType;
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
                                        .then(Commands.argument("type", StringArgumentType.string())
                                                .suggests((commandContext, suggestionsBuilder) -> {
                                                    suggestionsBuilder = suggestionsBuilder.suggest("human");
                                                    for (String types : BeUndead.UNDEAD_DATA.undeadKeys()){
                                                        suggestionsBuilder = suggestionsBuilder.suggest(types);
                                                    }
                                                    return suggestionsBuilder.buildFuture();
                                                })
                                                .executes(commandContext -> typeSet(commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), StringArgumentType.getString(commandContext, "type"))))))
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

    public static int typeSet(CommandSourceStack sourceStack, Collection<ServerPlayer> players, String typeName){
        UndeadType type = BeUndeadHelper.getServerUndeadType(typeName);
        if (type != null || typeName.equals("human")){
            for (ServerPlayer serverPlayer : players){
                BeUndeadHelper.setUndeadType(serverPlayer, typeName.equals("human") ? "" : typeName);
            }

            if (players.size() == 1){
                sourceStack.sendSuccess(Component.translatable("commands.undead.type.set.single", new Object[]{players.iterator().next().getName(), type != null ? type.name() : "Human"}), true);
            }
            else {
                sourceStack.sendSuccess(Component.translatable("commands.undead.type.set.multiple", new Object[]{players.size(), type != null ? type.name() : "Human"}), true);
            }
        }
        else {
            sourceStack.sendFailure(Component.translatable("commands.undead.type.fail", new Object[]{typeName}));
        }

        return players.size();
    }

    public static int typeGet(CommandSourceStack sourceStack, ServerPlayer player){
        String type = BeUndeadHelper.getUndeadTypeName(player);

        sourceStack.sendSuccess(Component.translatable("commands.undead.type.get", new Object[]{player.getName(), type}), false);

        return Command.SINGLE_SUCCESS;
    }

    public static int cure(CommandSourceStack sourceStack, Collection<ServerPlayer> players){
        for (ServerPlayer serverPlayer : players){
            BeUndeadHelper.startConverting(serverPlayer, null);
        }

        if (players.size() == 1){
            sourceStack.sendSuccess(Component.translatable("commands.undead.curing.start.single", new Object[]{players.iterator().next().getName()}), true);
        }
        else {
            sourceStack.sendSuccess(Component.translatable("commands.undead.curing.start.multiple", new Object[]{players.size()}), true);
        }

        return players.size();
    }

    public static int stopCure(CommandSourceStack sourceStack, Collection<ServerPlayer> players){
        for (ServerPlayer serverPlayer : players){
            BeUndeadHelper.stopConverting(serverPlayer);
        }

        if (players.size() == 1){
            sourceStack.sendSuccess(Component.translatable("commands.undead.curing.stop.single", new Object[]{players.iterator().next().getName()}), true);
        }
        else {
            sourceStack.sendSuccess(Component.translatable("commands.undead.curing.stop.multiple", new Object[]{players.size()}), true);
        }

        return players.size();
    }

    public static int setChest(CommandSourceStack sourceStack, Collection<ServerPlayer> players, boolean on){
        for (ServerPlayer serverPlayer : players){
            BeUndeadHelper.setZombieChest(serverPlayer, on);
        }

        if (players.size() == 1){
            sourceStack.sendSuccess(Component.translatable("commands.undead.chestextension.set.single", new Object[]{players.iterator().next().getName(), String.valueOf(on)}), true);
        }
        else {
            sourceStack.sendSuccess(Component.translatable("commands.undead.chestextension.set.multiple", new Object[]{players.size(), String.valueOf(on)}), true);
        }

        return players.size();
    }

    public static int getChest(CommandSourceStack sourceStack, ServerPlayer player){
        boolean has = BeUndeadHelper.hasZombieChest(player);

        if (has){
            sourceStack.sendSuccess(Component.translatable("commands.undead.chestextension.get.has", new Object[]{player.getName()}), false);
        }
        else {
            sourceStack.sendSuccess(Component.translatable("commands.undead.chestextension.get.has_not", new Object[]{player.getName()}), false);
        }

        return has ? Command.SINGLE_SUCCESS : 0;
    }
}