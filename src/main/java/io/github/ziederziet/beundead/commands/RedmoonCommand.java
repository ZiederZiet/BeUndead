package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.fogandredmoon.FogAndRedMoonSavedData;
import io.github.ziederziet.beundead.networking.ClientGetFogAndRedMoonPacket;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;

public class RedmoonCommand extends BaseCommand {
    public static final IntProvider FOG_DURATION = UniformInt.of(12000, 24000);
    public RedmoonCommand(String name, int permission) {
        super(name, permission);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) (getBaseBuilder())).then(Commands.literal("set").executes(sourceStack -> {
            return set(sourceStack.getSource(), true);
        }).then(Commands.argument("to", BoolArgumentType.bool()).executes(sourceStack -> {
            return set(sourceStack.getSource(), BoolArgumentType.getBool(sourceStack, "to"));
        }))).then(Commands.literal("get").executes(sourceStack -> {
            if (BeUndead.Mod.getRedMoon()){
                sourceStack.getSource().sendSuccess(() -> {
                    return Component.translatable("commands.redmoon.get.yes");
                }, true);
                return 1;
            } else {
                sourceStack.getSource().sendSuccess(() -> {
                    return Component.translatable("commands.redmoon.get.no");
                }, true);
                return 0;
            }
        })));
    }

    private static int set(CommandSourceStack sourceStack, boolean set){
        FogAndRedMoonSavedData savedData = FogAndRedMoonSavedData.getFogAndRedMoonSavedData(sourceStack.getServer());
        sourceStack.sendSuccess(() -> {
            return Component.translatable(set ? "commands.redmoon.get.yes" : "commands.redmoon.get.no");
        }, true);
        savedData.setRedMoonTimer(sourceStack.getLevel().getGameTime() + (24000 - sourceStack.getLevel().getDayTime()));
        boolean fog = BeUndead.Mod.getFoggyDay();
        boolean redMoon = !fog;
        if (!set){
            redMoon = false;
        }
        if (redMoon != BeUndead.Mod.getRedMoon()){
            BeUndead.Mod.setRedmoon(redMoon);
            ModNetworking.sendToAllClients(new ClientGetFogAndRedMoonPacket(fog, redMoon));
        }
        return Command.SINGLE_SUCCESS;
    }
}