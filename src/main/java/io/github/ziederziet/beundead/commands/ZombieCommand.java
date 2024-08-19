package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sun.jdi.connect.Connector;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ZombieCommand extends BaseCommand {
    public ZombieCommand(String name, int permission) {
        super(name, permission);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) (getBaseBuilder())).executes(sourceStack -> {
//            if (!sourceStack.getSource().isPlayer()){
//                return 0;
//            }
            if (!sourceStack.getSource().isPlayer() || BeUndead.getZombieType(sourceStack.getSource().getPlayer()) > 0){
                return 0;
            }
            return zombiefy(sourceStack.getSource(), sourceStack.getSource().getPlayer(), 1);
        }).then(Commands.literal("get").executes(sourceStack -> {
            if (!sourceStack.getSource().isPlayer()){
                return 0;
            }
            return getZombie(sourceStack.getSource(), sourceStack.getSource().getPlayer());
        }).then(Commands.argument("target", EntityArgument.player()).executes(sourceStack -> {
            if (!sourceStack.getSource().isPlayer()){
                return 0;
            }
            return getZombie(sourceStack.getSource(), EntityArgument.getPlayer(sourceStack, "target"));
        }))).then(Commands.literal("revive").executes(sourceStack -> {
            if (!sourceStack.getSource().isPlayer()){
                return 0;
            }
            return reviveZombie(sourceStack.getSource(), sourceStack.getSource().getPlayer());
        }).then(Commands.argument("targets", EntityArgument.players()).executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            if (players.isEmpty()){
                return 0;
            }
            return reviveZombies(sourceStack.getSource(), EntityArgument.getPlayers(sourceStack, "targets"));
        }))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("human").executes(sourceStack -> {
            int type = IntegerArgumentType.getInteger(sourceStack, "type");
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return reviveZombies(sourceStack.getSource(), players);
        })).then(Commands.literal("zombie").executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return setTypeForPlayers(sourceStack.getSource(), players, 1, Component.translatable("entity.minecraft.zombie"));
        })).then(Commands.literal("husk").executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return setTypeForPlayers(sourceStack.getSource(), players, 2, Component.translatable("entity.minecraft.husk"));
        })).then(Commands.literal("drowned").executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return setTypeForPlayers(sourceStack.getSource(), players, 3, Component.translatable("entity.minecraft.drowned"));
        })))));
    }

    private static int setTypeForPlayers(CommandSourceStack sourceStack, Collection<ServerPlayer> players, int type, Component typeName){
        int playersSet = 0;
        Iterator<ServerPlayer> playerIterator = players.iterator();
        while (playerIterator.hasNext()){
            ServerPlayer player = playerIterator.next();
            if (BeUndead.getZombieType(player) == 0){
                player.getInventory().dropAll();
            }
            if (BeUndead.getZombieType(player) != type){
                playersSet++;
                BeUndead.setZombieType(player, type);
            }
        }
        int finalRevived = playersSet;
        sourceStack.sendSuccess(() -> {
            return Component.translatable("commands.zombie.set.players", new Object[]{finalRevived, typeName});
        }, playersSet > 0);
        return Command.SINGLE_SUCCESS;
    }

    private static int reviveZombie(CommandSourceStack sourceStack, ServerPlayer player){
        if (BeUndead.getZombieType(player) == 0){
            if (player == sourceStack.getPlayer()){
                sourceStack.sendFailure(Component.translatable("commands.zombie.revive.not_zombie.self"));
            } else {
                sourceStack.sendFailure(Component.translatable("commands.zombie.revive.not_zombie"));
            }
            return 0;
        }
        BeUndead.revive(player);
        if (player == sourceStack.getPlayer()){
            sourceStack.sendSuccess(() -> {
                return Component.translatable("commands.zombie.revive.self", new Object[]{});
            }, true);
        }
        else {
            sourceStack.sendSuccess(() -> {
                return Component.translatable("commands.zombie.revive", new Object[]{player.getName()});
            }, true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int reviveZombies(CommandSourceStack sourceStack, Collection<ServerPlayer> players){
        int revived = 0;
        Iterator<ServerPlayer> playerIterator = players.iterator();
        while (playerIterator.hasNext()){
            ServerPlayer player = playerIterator.next();
            if (BeUndead.getZombieType(player) > 0){
                revived++;
                BeUndead.revive(player);
            }
        }
        int finalRevived = revived;
        sourceStack.sendSuccess(() -> {
            return Component.translatable("commands.zombie.revive.players", new Object[]{finalRevived});
        }, revived > 0);
        return revived;
    }

    private static int getZombie(CommandSourceStack sourceStack, ServerPlayer player){
        String translatable = "commands.zombie.get.not_zombie";
        if (BeUndead.getZombieType(player) == 1){
            translatable = "commands.zombie.get.zombie";
        }
        if (BeUndead.getZombieType(player) == 2){
            translatable = "commands.zombie.get.husk";
        }
        if (BeUndead.getZombieType(player) == 3){
            translatable = "commands.zombie.get.drowned";
        }
        if (player == sourceStack.getPlayer()){
            translatable += ".self";
        }
        String finalTranslatable = translatable;
        sourceStack.sendSuccess(() -> {
            return Component.translatable(finalTranslatable, new Object[]{player.getName()});
        }, false);


        return BeUndead.getZombieType(player);
    }

    private static int zombiefy(CommandSourceStack sourceStack, ServerPlayer player, int to){
        player.getInventory().dropAll();
        BeUndead.setZombieType(player, to);
        if (player == sourceStack.getPlayer()){
            sourceStack.sendSuccess(() -> {
                return Component.translatable("commands.zombiefy.self", new Object[]{});
            }, true);
        } else {
            sourceStack.sendSuccess(() -> {
                return Component.translatable("commands.zombiefy", new Object[]{player.getName()});
            }, true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int getPos(CommandSourceStack sourceStack, ServerPlayer player){
        if (sourceStack.getPlayer() == player){
            sourceStack.sendSuccess(() -> {
                return Component.translatable("commands.getpos.self", new Object[]{(int)player.getX(), (int)player.getY(), (int)player.getZ(), player.level().dimension().location().toString()});
            }, true);
        }
        else {
            sourceStack.sendSuccess(() -> {
                return Component.translatable("commands.getpos", new Object[]{player.getDisplayName(), (int)player.getX(), (int)player.getY(), (int)player.getZ(), player.level().dimension().location().toString()});
            }, true);
        }

        return Command.SINGLE_SUCCESS;
    }
}