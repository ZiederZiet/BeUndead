package io.github.ziederziet.beundead.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientConfigScreen implements ModMenuApi {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Client Be Undead Config"));

        ConfigCategory audio = builder.getOrCreateCategory(Component.literal("Audio"));

        ClientModConfig config = AutoConfig.getConfigHolder(ClientModConfig.class).getConfig();

        audio.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Component.literal("Zombie Sounds for Undead Players"), config.zombieSoundsPlayers)
                        .setDefaultValue(true)
                        .setSaveConsumer(newValue -> config.zombieSoundsPlayers = newValue)
                        .build()
        );

        builder.setGlobalized(false);

        builder.setSavingRunnable(() -> AutoConfig.getConfigHolder(ClientModConfig.class).save());

        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClientConfigScreen::create;
    }
}