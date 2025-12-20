package io.github.ziederziet.beundead.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ClientModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<Boolean> ZOMBIESOUNDSPLAYERS;

    static {
        BUILDER.push("Client Config for Be Undead");

        ZOMBIESOUNDSPLAYERS = BUILDER
                .comment("If enabled, zombie sounds will play for undead players")
                .define("zombieSoundsPlayers", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec config, Path path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

    public static boolean hasZombieSoundsPlayers() {
        return ZOMBIESOUNDSPLAYERS.get();
    }
}
