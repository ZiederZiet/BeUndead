package io.github.ziederziet.beundead.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ZOMBIESOUNDSPLAYERS = BUILDER
            .comment("If enabled, zombie sounds will play for undead players")
            .define("zombieSoundsPlayers", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean hasZombieSoundsPlayers() {
        return ZOMBIESOUNDSPLAYERS.getAsBoolean();
    }
}
