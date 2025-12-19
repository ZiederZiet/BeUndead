package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.config.CureRequirements;
import io.github.ziederziet.beundead.config.InventoryState;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import io.github.ziederziet.beundead.config.ServerModConfig;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConfigCommand extends BaseCommand {
    public ConfigCommand(String name, int permission) {
        super(name, permission);
    }

    private static void changed(MinecraftServer server){
        ModNetworking.sendToAllClients(ServerConfigAccessor.getPacket());
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(((LiteralArgumentBuilder<CommandSourceStack>) (getBaseBuilder()))
                .then(zombieInvStateConfig("zombieInvState"))
                .then(booleanConfig("zombieCanChestExtension", (config, aBool) -> { config.zombieCanChestExtension = aBool; BeUndeadHelper.checkNotSupposedItemsAndDropChestExtensionForAllPlayers(); }, (config) -> config.zombieCanChestExtension))
                .then(cureRequirementsConfig("cureRequirements"))
                .then(booleanConfig("husksEnabled", (config, aBool) -> config.husksEnabled = aBool, (config) -> config.husksEnabled))
                .then(booleanConfig("drownedEnabled", (config, aBool) -> config.drownedEnabled = aBool, (config) -> config.drownedEnabled))
                .then(floatConfig("zombieBreakSpeed", (config, aFloat) -> config.zombieBreakSpeed = aFloat, (config) -> config.zombieBreakSpeed))
                .then(booleanConfig("zombieNightVision", (config, aBool) -> config.zombieNightVision = aBool, (config) -> config.zombieNightVision))
                .then(booleanConfig("zombieJumpOnTheirOwn", (config, aBool) -> config.zombieJumpOnTheirOwn = aBool, (config) -> config.zombieJumpOnTheirOwn))
                .then(booleanConfig("zombieCanCrit", (config, aBool) -> config.zombieCanCrit = aBool, (config) -> config.zombieCanCrit))
                .then(booleanConfig("zombieOnlyKillExperience", (config, aBool) -> config.zombieOnlyKillExperience = aBool, (config) -> config.zombieOnlyKillExperience))
                .then(intConfig("zombieMaxViewDistance", (config, aInt) -> config.zombieMaxViewDistance = aInt, (config) -> (int)config.zombieMaxViewDistance))
                .then(doubleConfig("zombieWalkSpeed", (config, aDouble) -> config.zombieWalkSpeed = aDouble, (config) -> config.zombieWalkSpeed))
                .then(booleanConfig("zombieSprintEnabled", (config, aBool) -> config.zombieSprintEnabled = aBool, (config) -> config.zombieSprintEnabled))
                .then(booleanConfig("infectionEnabled", (config, aBool) -> config.infectionEnabled = aBool, (config) -> config.infectionEnabled))
                .then(booleanConfig("onlyTurnWhenInfected", (config, aBool) -> config.onlyTurnWhenInfected = aBool, (config) -> config.onlyTurnWhenInfected))
                .then(booleanConfig("forceTurnWhenInfected", (config, aBool) -> config.forceTurnWhenInfected = aBool, (config) -> config.forceTurnWhenInfected))
                .then(longConfig("respawnTimerToHuman", (config, aLong) -> config.respawnTimerToHuman = aLong, (config) -> config.respawnTimerToHuman))
                .then(longConfig("respawnTimerToZombie", (config, aLong) -> config.respawnTimerToZombie = aLong, (config) -> config.respawnTimerToZombie))
                .then(booleanConfig("undeadMode", (config, aBool) -> config.undeadMode = aBool, (config) -> config.undeadMode)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> booleanConfig(String name, BiConsumer<ServerModConfig, Boolean> changeValue, Function<ServerModConfig, Boolean> valueFunction){
        return Commands.literal(name).executes(commandContext -> getBoolean(commandContext.getSource(), name, valueFunction)).then(Commands.argument("value", BoolArgumentType.bool()).executes(commandContext -> changeBoolean(commandContext.getSource(), name, BoolArgumentType.getBool(commandContext, "value"), changeValue)));
    }

    public static int changeBoolean(CommandSourceStack sourceStack, String name, boolean value, BiConsumer<ServerModConfig, Boolean> changeValue){
        changeValue.accept(ServerModConfig.get(), value);
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.changed", new Object[]{name, String.valueOf(value)}), true);
        changed(sourceStack.getServer());
        return Command.SINGLE_SUCCESS;
    }

    public static int getBoolean(CommandSourceStack sourceStack, String name, Function<ServerModConfig, Boolean> valueFunction){
        boolean value = valueFunction.apply(ServerModConfig.get());
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.get", new Object[]{name, String.valueOf(value)}), false);
        return value ? 1 : 0;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> intConfig(String name, BiConsumer<ServerModConfig, Integer> changeValue, Function<ServerModConfig, Integer> valueFunction){
        return Commands.literal(name).executes(commandContext -> getInt(commandContext.getSource(), name, valueFunction)).then(Commands.argument("value", IntegerArgumentType.integer()).executes(commandContext -> changeInt(commandContext.getSource(), name, IntegerArgumentType.getInteger(commandContext, "value"), changeValue)));
    }

    public static int changeInt(CommandSourceStack sourceStack, String name, int value, BiConsumer<ServerModConfig, Integer> changeValue){
        changeValue.accept(ServerModConfig.get(), value);
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.changed", new Object[]{name, String.valueOf(value)}), true);
        changed(sourceStack.getServer());
        return Command.SINGLE_SUCCESS;
    }

    public static int getInt(CommandSourceStack sourceStack, String name, Function<ServerModConfig, Integer> valueFunction){
        int value = valueFunction.apply(ServerModConfig.get());
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.get", new Object[]{name, String.valueOf(value)}), false);
        return value;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> longConfig(String name, BiConsumer<ServerModConfig, Long> changeValue, Function<ServerModConfig, Long> valueFunction){
        return Commands.literal(name).executes(commandContext -> getLong(commandContext.getSource(), name, valueFunction)).then(Commands.argument("value", LongArgumentType.longArg()).executes(commandContext -> changeLong(commandContext.getSource(), name, LongArgumentType.getLong(commandContext, "value"), changeValue)));
    }

    public static int changeLong(CommandSourceStack sourceStack, String name, long value, BiConsumer<ServerModConfig, Long> changeValue){
        changeValue.accept(ServerModConfig.get(), value);
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.changed", new Object[]{name, String.valueOf(value)}), true);
        changed(sourceStack.getServer());
        return Command.SINGLE_SUCCESS;
    }

    public static int getLong(CommandSourceStack sourceStack, String name, Function<ServerModConfig, Long> valueFunction){
        long value = valueFunction.apply(ServerModConfig.get());
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.get", new Object[]{name, String.valueOf(value)}), false);
        return (int)value;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> floatConfig(String name, BiConsumer<ServerModConfig, Float> changeValue, Function<ServerModConfig, Float> valueFunction){
        return Commands.literal(name).executes(commandContext -> getFloat(commandContext.getSource(), name, valueFunction)).then(Commands.argument("value", FloatArgumentType.floatArg()).executes(commandContext -> changeFloat(commandContext.getSource(), name, FloatArgumentType.getFloat(commandContext, "value"), changeValue)));
    }

    public static int changeFloat(CommandSourceStack sourceStack, String name, float value, BiConsumer<ServerModConfig, Float> changeValue){
        changeValue.accept(ServerModConfig.get(), value);
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.changed", new Object[]{name, String.valueOf(value)}), true);
        changed(sourceStack.getServer());
        return Command.SINGLE_SUCCESS;
    }

    public static int getFloat(CommandSourceStack sourceStack, String name, Function<ServerModConfig, Float> valueFunction){
        float value = valueFunction.apply(ServerModConfig.get());
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.get", new Object[]{name, String.valueOf(value)}), false);
        return Math.round(value);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> doubleConfig(String name, BiConsumer<ServerModConfig, Double> changeValue, Function<ServerModConfig, Double> valueFunction){
        return Commands.literal(name).executes(commandContext -> getDouble(commandContext.getSource(), name, valueFunction)).then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes(commandContext -> changeDouble(commandContext.getSource(), name, DoubleArgumentType.getDouble(commandContext, "value"), changeValue)));
    }

    public static int changeDouble(CommandSourceStack sourceStack, String name, double value, BiConsumer<ServerModConfig, Double> changeValue){
        changeValue.accept(ServerModConfig.get(), value);
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.changed", new Object[]{name, String.valueOf(value)}), true);
        changed(sourceStack.getServer());
        return Command.SINGLE_SUCCESS;
    }

    public static int getDouble(CommandSourceStack sourceStack, String name, Function<ServerModConfig, Double> valueFunction){
        double value = valueFunction.apply(ServerModConfig.get());
        sourceStack.sendSuccess(() -> Component.translatable("commands.beundeadconfig.get", new Object[]{name, String.valueOf(value)}), false);
        return (int)Math.round(value);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> zombieInvStateConfig(String name){
        InventoryState[] values = InventoryState.values();
        LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal(name).executes(commandContext -> {
            InventoryState value = ServerModConfig.get().zombieInvState;

            String valueString = value.toString();

            commandContext.getSource().sendSuccess(() -> Component.translatable("commands.beundeadconfig.get", new Object[]{name, valueString}), false);

            return value.getId();
        });
        for (int i = 0; i < values.length; i++) {
            int finalI = i;
            argumentBuilder = argumentBuilder.then(Commands.literal(stringificationOfEnum(values[i].toString())).executes(commandContext -> {
                ServerModConfig.get().zombieInvState = values[finalI];

                commandContext.getSource().sendSuccess(() -> Component.translatable("commands.beundeadconfig.changed", new Object[]{"zombieInvState", values[finalI].toString()}), true);

                changed(commandContext.getSource().getServer());

                BeUndeadHelper.checkNotSupposedItemsAndDropChestExtensionForAllPlayers();

                return Command.SINGLE_SUCCESS;
            }));
        }
        return argumentBuilder;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> cureRequirementsConfig(String name){
        CureRequirements[] values = CureRequirements.values();
        LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal(name).executes(commandContext -> {
            CureRequirements value = ServerModConfig.get().cureRequirements;

            String valueString = value.toString();

            commandContext.getSource().sendSuccess(() -> Component.translatable("commands.beundeadconfig.get", new Object[]{name, valueString}), false);

            return value.getId();
        });
        for (int i = 0; i < values.length; i++) {
            int finalI = i;
            argumentBuilder = argumentBuilder.then(Commands.literal(stringificationOfEnum(values[i].toString())).executes(commandContext -> {
                ServerModConfig.get().cureRequirements = values[finalI];

                commandContext.getSource().sendSuccess(() -> Component.translatable("commands.beundeadconfig.changed", new Object[]{"cureRequirements", values[finalI].toString()}), true);

                changed(commandContext.getSource().getServer());

                return Command.SINGLE_SUCCESS;
            }));
        }
        return argumentBuilder;
    }

    private static String stringificationOfEnum(String enumToString){
        return enumToString.replace(" ", "_").replace("&", "and").toLowerCase();
    }
}