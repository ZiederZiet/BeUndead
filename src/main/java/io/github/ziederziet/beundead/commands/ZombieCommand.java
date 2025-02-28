package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.zombie_capability.InfectionZombieCapability;
import io.github.ziederziet.beundead.zombie_capability.InfectionZombieCapabilityProvider;
import io.github.ziederziet.beundead.zombie_capability.ZombiePlayerCapability;
import io.github.ziederziet.beundead.zombie_capability.ZombiePlayerCapabilityProvider;
import io.github.ziederziet.beundead.zombie_settings.ZombieSettingsSavedData;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collection;
import java.util.Iterator;

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
        }).then(Commands.literal("revive").executes(sourceStack -> {
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
        }))).then(Commands.literal("type")
                        .then(Commands.literal("husks").then(Commands.literal("enable").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setHusks(true, sourceStack.getSource().getServer());
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.husks.enable"), true);
                            return Command.SINGLE_SUCCESS;
                        })).then(Commands.literal("disable").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setHusks(false, sourceStack.getSource().getServer());
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.husks.disable"), true);
                            return Command.SINGLE_SUCCESS;
                        })))
                        .then(Commands.literal("drowned").then(Commands.literal("enable").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setDrowned(true, sourceStack.getSource().getServer());
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.drowned.enable"), true);
                            return Command.SINGLE_SUCCESS;
                        })).then(Commands.literal("disable").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setDrowned(false, sourceStack.getSource().getServer());
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.drowned.disable"), true);
                            return Command.SINGLE_SUCCESS;
                        })))

                        .then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.literal("human").then(Commands.argument("convert", BoolArgumentType.bool()).executes(sourceStack -> {
                    Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
                    return setTypeForPlayers(sourceStack.getSource(), players, 0, Component.translatable("entity.minecraft.player"), BoolArgumentType.getBool(sourceStack, "convert"));
                })).executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return setTypeForPlayers(sourceStack.getSource(), players, 0, Component.translatable("entity.minecraft.player"), false);
        })).then(Commands.literal("zombie").executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return setTypeForPlayers(sourceStack.getSource(), players, 1, Component.translatable("entity.minecraft.zombie"), false);
        })).then(Commands.literal("husk").executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return setTypeForPlayers(sourceStack.getSource(), players, 2, Component.translatable("entity.minecraft.husk"), false);
        })).then(Commands.literal("drowned").executes(sourceStack -> {
            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
            return setTypeForPlayers(sourceStack.getSource(), players, 3, Component.translatable("entity.minecraft.drowned"), false);
        })))).then(Commands.literal("get").executes(sourceStack -> {
                    if (!sourceStack.getSource().isPlayer()){
                        return 0;
                    }
                    return getZombie(sourceStack.getSource(), sourceStack.getSource().getPlayer());
                }).then(Commands.argument("target", EntityArgument.player()).executes(sourceStack -> {
                    if (!sourceStack.getSource().isPlayer()){
                        return 0;
                    }
                    return getZombie(sourceStack.getSource(), EntityArgument.getPlayer(sourceStack, "target"));
                }))))
                .then(Commands.literal("vision").then(Commands.literal("nightvision").then(Commands.literal("enabled").executes(sourceStack -> {
                    ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieNightVision(true);
                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.nightvision.enable"), true);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("disabled").executes(sourceStack -> {
                    ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieNightVision(false);
                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.nightvision.disable"), true);
                    return Command.SINGLE_SUCCESS;
                })))
                        .then(Commands.literal("maxrenderdistance").then(Commands.literal("set").then(Commands.argument("maxrenderdistance", IntegerArgumentType.integer()).executes(sourceStack -> {
                            int maxrenderdistance = IntegerArgumentType.getInteger(sourceStack, "maxrenderdistance");
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieMaxViewDistance(maxrenderdistance);
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.maxrenderdistance.setto", new Object[] { maxrenderdistance }), true);
                            return Command.SINGLE_SUCCESS;
                        }))).then(Commands.literal("disable").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieMaxViewDistance(0);
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.maxrenderdistance.setto", new Object[] { 0 }), true);
                            return Command.SINGLE_SUCCESS;
                        }))))
                        .then(Commands.literal("speed").then(Commands.literal("set").then(Commands.argument("speed", DoubleArgumentType.doubleArg()).executes(sourceStack -> {
                            double speed = DoubleArgumentType.getDouble(sourceStack, "speed");
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieSpeed(speed);
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.speed.setto", new Object[] { speed }), true);
                            return Command.SINGLE_SUCCESS;
                        }))).then(Commands.literal("default").executes(sourceStack -> {
                            double speed = 0.46D;
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieSpeed(speed);
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.speed.default", new Object[] { speed }), true);
                            return Command.SINGLE_SUCCESS;
                        })).then(Commands.literal("player").executes(sourceStack -> {
                            double speed = 1D;
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieSpeed(speed);
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.speed.player", new Object[] { speed }), true);
                            return Command.SINGLE_SUCCESS;
                        })))

                .then(Commands.literal("infection").executes(sourceStack -> {
                            boolean infection = ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).hasInfection();
                            if (infection){
                                sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.infection.get.enabled"), false);
                            } else {
                                sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.infection.get.disabled"), false);
                            }
                            return infection ? 1 : 0;
                        })
                        .then(Commands.literal("enable").executes(sourceStack -> {
                    ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setInfection(true, sourceStack.getSource().getServer());
                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.infection.enabled"), true);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("disable").executes(sourceStack -> {
                    ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setInfection(false, sourceStack.getSource().getServer());
                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.infection.disabled"), true);
                    return Command.SINGLE_SUCCESS;
                })).then(Commands.literal("cure").then(Commands.argument("targets", EntityArgument.players()).executes(sourceStack -> {
                    Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
                    if (players.isEmpty()){
                        return 0;
                    }
                    players.forEach(serverPlayer -> {
                        serverPlayer.getCapability(InfectionZombieCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
                            zombiePlayerCapability.removeInfection(serverPlayer);
                        });
                    });

                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.infection.cure.players"), true);
                    return Command.SINGLE_SUCCESS;
                }))).then(Commands.literal("get").then(Commands.argument("targets", EntityArgument.players()).executes(sourceStack -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(sourceStack, "targets");
                            if (players.isEmpty()){
                                return 0;
                            }
                            if (players.size() > 1){
                                int amount = (int) players.stream().filter(serverPlayer -> {
                                    LazyOptional<InfectionZombieCapability> capabilityLazyOptional = serverPlayer.getCapability(InfectionZombieCapabilityProvider.ZOMBIE_CAPABILITY);
                                    if (capabilityLazyOptional.isPresent()){
                                        return capabilityLazyOptional.resolve().get().isInfected();
                                    }
                                    return false;
                                }).count();
                                sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.infection.get.players", new Object[] {amount}), false);
                                return amount;
                            }
                            else {
                                ServerPlayer serverPlayer = players.iterator().next();
                                LazyOptional<InfectionZombieCapability> capabilityLazyOptional = serverPlayer.getCapability(InfectionZombieCapabilityProvider.ZOMBIE_CAPABILITY);
                                if (capabilityLazyOptional.isPresent()){
                                    int amount = capabilityLazyOptional.resolve().get().getInfected();
                                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.infection.get.amount", new Object[] {serverPlayer.getName(), amount}), false);
                                    return amount;
                                }

                            }
                            return 0;
                        })))
                )
                        .then(Commands.literal("jump").then(Commands.literal("enable").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieJumpOnTheirOwn(true);
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.jump.enable"), true);
                            return Command.SINGLE_SUCCESS;
                        })).then(Commands.literal("onlyauto").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieJumpOnTheirOwn(false);
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.jump.onlyauto"), true);
                            return Command.SINGLE_SUCCESS;
                        })))

                        .then(Commands.literal("inventory").then(Commands.literal("oneslot").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieInvState(0, sourceStack.getSource().getServer());
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.inventory.setto.oneslot"), true);
                            return Command.SINGLE_SUCCESS;
                        })).then(Commands.literal("hotbar").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieInvState(1, sourceStack.getSource().getServer());
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.inventory.setto.hotbar"), true);
                            return Command.SINGLE_SUCCESS;
                        })).then(Commands.literal("fullinventory").executes(sourceStack -> {
                            ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieInvState(2, sourceStack.getSource().getServer());
                            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.inventory.setto.fullinventory"), true);
                            return Command.SINGLE_SUCCESS;
                        })).then(Commands.literal("chests").executes(sourceStack -> {
                                    boolean canChestExtension = ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).canChestExtension();
                                    if (canChestExtension){
                                        sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.chests.get.enabled"), false);
                                    } else {
                                        sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.chests.get.disabled"), false);
                                    }
                                    return canChestExtension ? 1 : 0;
                                })
                                .then(Commands.literal("enable").executes(sourceStack -> {
                                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.chests.enable"), true);
                                    ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieCanChestExtension(true, sourceStack.getSource().getServer());
                                    return 0;
                                })).then(Commands.literal("disable").executes(sourceStack -> {
                                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.chests.disable"), true);
                                    ZombieSettingsSavedData.getZombieSettingsSavedData(sourceStack.getSource().getServer()).setZombieCanChestExtension(false, sourceStack.getSource().getServer());
                                    return 0;
                                }))))
                .then(Commands.literal("conversion").then(Commands.literal("start").then(Commands.argument("target", EntityArgument.player()).executes(sourceStack -> {
            Player player = EntityArgument.getPlayer(sourceStack, "target");
            if (player == null){
                return 0;
            }
            if (BeUndead.getZombieConversionTime(player) > 0){
                sourceStack.getSource().sendFailure(Component.translatable("commands.zombie.conversion_start.fail", new Object[] {player.getName()}));
                return 0;
            }
            BeUndead.startConverting(player, 0, null);
            int conversion = BeUndead.getZombieConversionTime(player);
            sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.conversion_start", new Object[] {player.getName(), conversion}), true);
            return Command.SINGLE_SUCCESS;
        }))).then(
                Commands.literal("get").then(Commands.argument("target", EntityArgument.player()).executes(sourceStack -> {
                    Player player = EntityArgument.getPlayer(sourceStack, "target");
                    Component playerName = player.getName();
                    int conversionTime = BeUndead.getZombieConversionTime(player);
                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.get.conversion_time", new Object[]{playerName, conversionTime}), false);
                    return conversionTime;
        }))).then(
                Commands.literal("set").then(Commands.argument("target", EntityArgument.player()).then(Commands.argument("conversion_time", IntegerArgumentType.integer()).executes(sourceStack -> {
                    Player player = EntityArgument.getPlayer(sourceStack, "target");
                    Component playerName = player.getName();
                    int conversionTime = IntegerArgumentType.getInteger(sourceStack, "conversion_time");
                    if (conversionTime <= 0){
                        conversionTime = 1;
                    }
                    if (BeUndead.getZombieConversionTime(player) <= 0){
                        BeUndead.startConverting(player, 0, null);
                    }
                    BeUndead.setZombieConversionTime(player, conversionTime);
                    int finalConversionTime = conversionTime;
                    sourceStack.getSource().sendSuccess(() -> Component.translatable("commands.zombie.set.conversion_time", new Object[]{playerName, finalConversionTime}), true);
                    return Command.SINGLE_SUCCESS;
                }))))));
    }

    private static int setTypeForPlayers(CommandSourceStack sourceStack, Collection<ServerPlayer> players, int type, Component typeName, boolean convert){
        int playersSet = 0;
        Iterator<ServerPlayer> playerIterator = players.iterator();
        while (playerIterator.hasNext()){
            ServerPlayer player = playerIterator.next();
            if (BeUndead.getZombieType(player) != type){
                int oldInvState = BeUndead.getInvStateOfPlayer(player);
                playersSet++;
                if (convert){
                    BeUndead.startConverting(player, type, null);
                } else {
                    BeUndead.setZombieType(player, type);
                }
                if (oldInvState > BeUndead.getInvStateOfPlayer(player)){
                    BeUndead.checkItemsInNewState(player, true);
                }
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
        BeUndead.revive(player, false);
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
                BeUndead.revive(player, false);
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