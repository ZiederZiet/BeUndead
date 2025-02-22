package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class RespawnTimerCommand extends BaseCommand {
    public RespawnTimerCommand(String name, int permission) {
        super(name, permission);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) (getBaseBuilder())).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("duration", LongArgumentType.longArg()).executes(sourceStack -> {
            long currentGameTime = sourceStack.getSource().getLevel().getGameTime();
            long duration = LongArgumentType.getLong(sourceStack, "duration");
            long time = currentGameTime + duration;
            Collection<ServerPlayer> serverPlayers = EntityArgument.getPlayers(sourceStack, "targets");
            for (ServerPlayer serverPlayer : serverPlayers){
                BeUndead.setZombieRespawnTimer(serverPlayer, time);
            }
            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.respawntimer.set", new Object[] { duration, serverPlayers.size() }), true);
            return Command.SINGLE_SUCCESS;
        })))).then(Commands.literal("get").then(Commands.argument("target", EntityArgument.player()).executes(sourceStack -> {
            ServerPlayer serverPlayer = EntityArgument.getPlayer(sourceStack, "target");
            long currentGameTime = serverPlayer.level().getGameTime();
            long duration = BeUndead.getZombieRespawnTimer(serverPlayer) - currentGameTime;
            if (duration > 0) {
                long seconds = Math.round(duration / 20D);
                sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.respawntimer.get", new Object[] { serverPlayer.getName(), duration, seconds }), false);
            }
            else {
                sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.respawntimer.get.not_respawning", new Object[] { serverPlayer.getName() }), false);
            }
            return Command.SINGLE_SUCCESS;
        }))));
    }
}
