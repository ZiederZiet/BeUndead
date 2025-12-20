package io.github.ziederziet.beundead.config;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.networking.ZombieSettingsPacket;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerModConfig {
    public static ZombieSettingsPacket getPacket(){
        return new ZombieSettingsPacket(ServerModConfig.getZombieInvState(),
                ServerModConfig.getZombieCanChestExtension(),
                ServerModConfig.getZombieNightVision(),
                ServerModConfig.getZombieJumpOnTheirOwn(),
                ServerModConfig.getZombieMaxViewDistance(),
                ServerModConfig.getZombieWalkSpeed(),
                ServerModConfig.getZombieBreakSpeed(),
                ServerModConfig.isZombieSprintingEnabled(),
                BeUndead.UNDEAD_DATA.map());
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<InventoryState> ZOMBIE_INV_STATE;
    public static final ModConfigSpec.BooleanValue ZOMBIE_CAN_CHEST_EXTENSION;
    public static final ModConfigSpec.EnumValue<CureRequirements> CURE_REQUIREMENTS;
    public static final ModConfigSpec.BooleanValue HUSKS_ENABLED;
    public static final ModConfigSpec.BooleanValue DROWNED_ENABLED;
    public static final ModConfigSpec.ConfigValue<Double> ZOMBIE_BREAK_SPEED;
    public static final ModConfigSpec.BooleanValue ZOMBIE_NIGHT_VISION;
    public static final ModConfigSpec.BooleanValue ZOMBIE_JUMP_ON_THEIR_OWN;
    public static final ModConfigSpec.BooleanValue ZOMBIE_CAN_CRIT;
    public static final ModConfigSpec.BooleanValue ZOMBIE_ONLY_KILL_EXPERIENCE;
    public static final ModConfigSpec.ConfigValue<Integer> ZOMBIE_MAX_VIEW_DISTANCE;
    public static final ModConfigSpec.ConfigValue<Double> ZOMBIE_WALK_SPEED;
    public static final ModConfigSpec.BooleanValue ZOMBIE_SPRINT_ENABLED;
    public static final ModConfigSpec.BooleanValue INFECTION_ENABLED;
    public static final ModConfigSpec.BooleanValue ONLY_TURN_WHEN_INFECTED;
    public static final ModConfigSpec.BooleanValue FORCE_TURN_WHEN_INFECTED;
    public static final ModConfigSpec.ConfigValue<Integer> RESPAWN_TIMER_TO_HUMAN;
    public static final ModConfigSpec.ConfigValue<Integer> RESPAWN_TIMER_TO_ZOMBIE;

    public static final ModConfigSpec SPEC;


    static {
        BUILDER.push("commonconfig");

        ZOMBIE_INV_STATE = BUILDER
                .comment("Determents the default inventory capacity for zombie players")
                .defineEnum("zombieInvState", InventoryState.ONE_SLOT);

        ZOMBIE_CAN_CHEST_EXTENSION = BUILDER
                .comment("If zombie players can use a chest as an extension to their inventory")
                .define("zombieCanChestExtension", true);

        CURE_REQUIREMENTS = BUILDER
                .defineEnum("cureRequirements", CureRequirements.WEAKNESS_AND_APPLE);

        HUSKS_ENABLED = BUILDER
                .define("husksEnabled", true);

        DROWNED_ENABLED = BUILDER
                .define("drownedEnabled", true);

        ZOMBIE_BREAK_SPEED = BUILDER
                .define("zombieBreakSpeed", 0.5D);

        ZOMBIE_NIGHT_VISION = BUILDER
                .define("zombieNightVision", true);

        ZOMBIE_JUMP_ON_THEIR_OWN = BUILDER
                .define("zombieJumpOnTheirOwn", true);

        ZOMBIE_CAN_CRIT = BUILDER
                .define("zombieCanCrit", true);

        ZOMBIE_ONLY_KILL_EXPERIENCE = BUILDER
                .comment("There will not be any experience orbs for zombie players if this is enabled")
                .define("zombieOnlyKillExperience", true);

        ZOMBIE_MAX_VIEW_DISTANCE = BUILDER
                .define("zombieMaxViewDistance", 4);

        ZOMBIE_WALK_SPEED = BUILDER
                .define("zombieWalkSpeed", 1D);

        ZOMBIE_SPRINT_ENABLED = BUILDER
                .define("zombieSprintEnabled", false);

        INFECTION_ENABLED = BUILDER
                .define("infectionEnabled", false);

        ONLY_TURN_WHEN_INFECTED = BUILDER
                .define("onlyTurnWhenInfected", false);

        FORCE_TURN_WHEN_INFECTED = BUILDER
                .define("forceTurnWhenInfected", true);

        RESPAWN_TIMER_TO_HUMAN = BUILDER
                .comment("How long it will take for a player to respawn when not respawning as a zombie")
                .comment("Time in seconds")
                .define("respawnTimerToHuman", 0);

        RESPAWN_TIMER_TO_ZOMBIE = BUILDER
                .comment("How long it will take for a player to respawn when respawning as a zombie")
                .comment("Time in seconds")
                .define("respawnTimerToZombie", 0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static int getZombieInvState() {
         return ZOMBIE_INV_STATE.get().getId();
    }

    public static boolean getZombieCanChestExtension() {
        return ZOMBIE_CAN_CHEST_EXTENSION.get();
    }

    public static boolean areHusksEnabled() {
        return HUSKS_ENABLED.get();
    }

    public static boolean areDrownedEnabled() {
        return DROWNED_ENABLED.get();
    }

    public static boolean getZombieNightVision() {
        return ZOMBIE_NIGHT_VISION.get();
    }

    public static boolean getZombieJumpOnTheirOwn() {
        return ZOMBIE_JUMP_ON_THEIR_OWN.get();
    }

    public static boolean getZombieCanCrit() {
        return ZOMBIE_CAN_CRIT.get();
    }

    public static int getZombieMaxViewDistance() {
        return ZOMBIE_MAX_VIEW_DISTANCE.get();
    }

    public static boolean isInfectionEnabled() {
        return INFECTION_ENABLED.get();
    }

    public static boolean getOnlyTurnWhenInfected() {
        return ONLY_TURN_WHEN_INFECTED.get();
    }

    public static boolean getForceTurnWhenInfected() {
        return FORCE_TURN_WHEN_INFECTED.get();
    }

    public static long getRespawnTimer() {
        return RESPAWN_TIMER_TO_HUMAN.get();
    }

    public static long getRespawnTimerToZombie() {
        return RESPAWN_TIMER_TO_ZOMBIE.get();
    }

    public static double getZombieWalkSpeed() {
        return ZOMBIE_WALK_SPEED.get();
    }

    public static float getZombieBreakSpeed() {
        return ZOMBIE_BREAK_SPEED.get().floatValue();
    }

    public static boolean getZombieOnlyKillExperience() {
        return ZOMBIE_ONLY_KILL_EXPERIENCE.get();
    }

    public static boolean isZombieSprintingEnabled() {
        return ZOMBIE_SPRINT_ENABLED.get();
    }

    public static int getCureRequirements() {
        return CURE_REQUIREMENTS.get().getId();
    }
}