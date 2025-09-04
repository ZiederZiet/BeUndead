package io.github.ziederziet.beundead.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.networking.ModNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;

public class ConfigScreen implements ModMenuApi {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Clicked Config"));

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        general.addEntry(
                builder.entryBuilder()
                        .startEnumSelector(Component.literal("Default Inventory State"), InventoryState.class, config.zombieInvState)
                        .setDefaultValue(InventoryState.ONE_SLOT)
                        .setSaveConsumer(newValue -> config.zombieInvState = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Zombies Can Add Inventory Chest Extension"), config.zombieCanChestExtension)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.zombieCanChestExtension = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Husks Enabled"), config.husksEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.husksEnabled = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Drowned Enabled"), config.drownedEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.drownedEnabled = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Zombie Night Vision"), config.zombieNightVision)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.zombieNightVision = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Can Zombie Jump On Their Own"), config.zombieJumpOnTheirOwn)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.zombieJumpOnTheirOwn = newValue)
                        .setTooltip(Component.literal("If turned off, they can only jump through auto jump"))
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Zombie Can Crit Attack"), config.zombieCanCrit)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.zombieCanCrit = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startIntField(Component.literal("Zombie Max View Distance"), config.zombieMaxViewDistance)
                        .setDefaultValue(4)
                        .setTooltip(Component.literal("0 = No Limit"), Component.literal("Distance in chunks"))
                        .setSaveConsumer(newValue -> config.zombieMaxViewDistance = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startDoubleField(Component.literal("Zombie Walk Speed"), config.zombieWalkSpeed)
                        .setDefaultValue(1D)
                        .setTooltip(Component.literal("1 is player default, 0.46 is for zombies"))
                        .setSaveConsumer(newValue -> config.zombieWalkSpeed = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Zombie Can Sprint"), config.zombieSprintEnabled)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> config.zombieSprintEnabled = newValue)
                        .build()
        );

        general.addEntry(
                builder.entryBuilder()
                        .startEnumSelector(Component.literal("Cure Requirements"), CureRequirements.class, config.cureRequirements)
                        .setDefaultValue(CureRequirements.WEAKNESS_AND_APPLE)
                        .setSaveConsumer(newValue -> config.cureRequirements = newValue)
                        .build()
        );

        ConfigCategory infection = builder.getOrCreateCategory(Component.literal("Infection"));

        infection.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Infection Enabled"), config.infectionEnabled)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> config.infectionEnabled = newValue)
                        .build()
        );

        infection.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Only turn to zombie on death when infected"), config.onlyTurnWhenInfected)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> config.onlyTurnWhenInfected = newValue)
                        .build()
        );

        infection.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Force turn to zombie when infected"), config.forceTurnWhenInfected)
                        .setDefaultValue(true)
                        .setTooltip(Component.literal("The carrier dies automatically when infected for to long"))
                        .setSaveConsumer(newValue -> config.forceTurnWhenInfected = newValue)
                        .build()
        );

        ConfigCategory respawnTimer = builder.getOrCreateCategory(Component.literal("Respawn Timer"));

        respawnTimer.addEntry(
                builder.entryBuilder()
                        .startLongField(Component.literal("Respawn timer when respawning as human"), config.respawnTimerToHuman)
                        .setDefaultValue(0L)
                        .setSaveConsumer(newValue -> config.respawnTimerToHuman = newValue)
                        .build()
        );

        respawnTimer.addEntry(
                builder.entryBuilder()
                        .startLongField(Component.literal("Respawn timer when respawning as zombie"), config.respawnTimerToZombie)
                        .setDefaultValue(3600L)
                        .setSaveConsumer(newValue -> config.respawnTimerToZombie = newValue)
                        .build()
        );

        builder.setGlobalized(true);

        builder.setSavingRunnable(() -> {
            AutoConfig.getConfigHolder(ModConfig.class).save();
            MinecraftServer server = BeUndead.getServer();
            if (server != null){
                ModNetworking.sendToAllClients(server, ConfigAccessor.getPacket());
            }
            // TODO SENT TO ALL
        });

        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::create;
    }
}
