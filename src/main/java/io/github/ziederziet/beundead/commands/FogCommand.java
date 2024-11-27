package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.fogandredmoon.FogAndRedMoonSavedData;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;

public class FogCommand extends BaseCommand {
    public static final IntProvider FOG_DURATION = UniformInt.of(12000, 24000);
    public FogCommand(String name, int permission) {
        super(name, permission);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) (getBaseBuilder())).then(Commands.literal("add").executes(sourceStack -> {
            FogAndRedMoonSavedData savedData = FogAndRedMoonSavedData.getFogAndRedMoonSavedData(sourceStack.getSource().getServer());
            savedData.setFogTimer(sourceStack.getSource().getLevel().getGameTime() + getDuration(sourceStack.getSource(), -1, FOG_DURATION));
            sourceStack.getSource().sendSuccess(() -> {
                return Component.translatable("commands.fog.add");
            }, true);
            return Command.SINGLE_SUCCESS;
        }).then(Commands.argument("duration", IntegerArgumentType.integer()).executes(sourceStack -> {
            FogAndRedMoonSavedData savedData = FogAndRedMoonSavedData.getFogAndRedMoonSavedData(sourceStack.getSource().getServer());
            savedData.setFogTimer(sourceStack.getSource().getLevel().getGameTime() + getDuration(sourceStack.getSource(), IntegerArgumentType.getInteger(sourceStack, "duration"), FOG_DURATION));
            sourceStack.getSource().sendSuccess(() -> {
                return Component.translatable("commands.fog.add");
            }, true);
            return Command.SINGLE_SUCCESS;
        }))).then(Commands.literal("get").executes(sourceStack -> {
            int success = BeUndead.Mod.getRedMoon() ? Command.SINGLE_SUCCESS : 0;
            if (success == 1){
                FogAndRedMoonSavedData savedData = FogAndRedMoonSavedData.getFogAndRedMoonSavedData(sourceStack.getSource().getServer());
                sourceStack.getSource().sendSuccess(() -> {
                    return Component.translatable("commands.fog.get.yes", new Object[] {savedData.getFogTimer() - sourceStack.getSource().getLevel().getGameTime()});
                }, false);
            }
            else {
                sourceStack.getSource().sendSuccess(() -> {
                    return Component.translatable("commands.fog.get.no");
                }, false);
            }
            return success;
        })));
    }

    private static int getDuration(CommandSourceStack pSource, int pTime, IntProvider pTimeProvider) {
        return pTime == -1 ? pTimeProvider.sample(pSource.getServer().overworld().getRandom()) : pTime;
    }
}