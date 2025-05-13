package io.github.ziederziet.beundead.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ModConfig implements ConfigAccessor {
    public static ModConfig Instance = new ModConfig();

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<Integer> ZOMBIE_INV_STATE;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ZOMBIE_CHEST_EXTENSION;
    private static final ForgeConfigSpec.ConfigValue<Boolean> HUSKS_ENABLED;
    private static final ForgeConfigSpec.ConfigValue<Boolean> DROWNED_ENABLED;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ZOMBIE_NIGHT_VISION;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ZOMBIE_JUMP_ON_THEIR_OWN;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ZOMBIE_CAN_CRIT;
    private static final ForgeConfigSpec.ConfigValue<Integer> ZOMBIE_MAX_VIEW_DISTANCE;
    private static final ForgeConfigSpec.ConfigValue<Boolean> INFECTION_ENABLED;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ONLY_TURN_WHEN_INFECTED;
    private static final ForgeConfigSpec.ConfigValue<Boolean> FORCE_TURN_WHEN_INFECTED;
    private static final ForgeConfigSpec.ConfigValue<Long> RESPAWN_TIMER;
    private static final ForgeConfigSpec.ConfigValue<Long> RESPAWN_TIMER_TO_ZOMBIE;
    private static final ForgeConfigSpec.ConfigValue<Double> ZOMBIE_WALK_SPEED;

    static {
        BUILDER.push("Configs for Be Undead");

        ZOMBIE_INV_STATE = BUILDER//.comment("0 = one slot | 1 = hotbar | 2 = full inv")
                .defineInRange("Undead Default Inv State", 0, 0, 2);

        ZOMBIE_CHEST_EXTENSION = BUILDER//.comment("If an undead can extend their inventory by right-clicking a chest (adds 1 to the inv state)")
                .define("Undead Chest Extension", true);

        HUSKS_ENABLED = BUILDER.define("Undead Husks enabled", true);

        DROWNED_ENABLED = BUILDER.define("Undead Drowned enabled", true);

        ZOMBIE_NIGHT_VISION = BUILDER.define("Undead Night Vision", true);

        ZOMBIE_JUMP_ON_THEIR_OWN = BUILDER.define("Undead jump on their own", true);

        ZOMBIE_CAN_CRIT = BUILDER.define("Undead can crit attack", true);

        ZOMBIE_MAX_VIEW_DISTANCE = BUILDER.define("Undead max view distance", 4);

        INFECTION_ENABLED = BUILDER.define("Infection enabled", false);

        ONLY_TURN_WHEN_INFECTED = BUILDER.define("Only turn to undead when died while infected", false);

        FORCE_TURN_WHEN_INFECTED = BUILDER.define("Force to undead when infected", true);

        RESPAWN_TIMER = BUILDER.define("Respawn timer to not undead", 0L);

        RESPAWN_TIMER_TO_ZOMBIE = BUILDER.define("Respawn timer to undead", 3600L);

        ZOMBIE_WALK_SPEED = BUILDER.define("Undead walk speed", 1.0D);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec config, Path path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

    @Override
    public int getZombieInvState() {
        return ZOMBIE_INV_STATE.get();
    }

    @Override
    public boolean getZombieCanChestExtension() {
        if (getZombieInvState() >= 2){
            return false;
        }
        return ZOMBIE_CHEST_EXTENSION.get();
    }

    @Override
    public boolean areHusksEnabled() {
        return HUSKS_ENABLED.get();
    }

    @Override
    public boolean areDrownedEnabled() {
        return DROWNED_ENABLED.get();
    }

    @Override
    public boolean getZombieNightVision() {
        return ZOMBIE_NIGHT_VISION.get();
    }

    @Override
    public boolean getZombieJumpOnTheirOwn() {
        return ZOMBIE_JUMP_ON_THEIR_OWN.get();
    }

    @Override
    public boolean getZombieCanCrit() {
        return ZOMBIE_CAN_CRIT.get();
    }

    @Override
    public int getZombieMaxViewDistance() {
        return ZOMBIE_MAX_VIEW_DISTANCE.get();
    }

    @Override
    public boolean getInfection() {
        return INFECTION_ENABLED.get();
    }

    @Override
    public boolean getOnlyTurnWhenInfected() {
        return ONLY_TURN_WHEN_INFECTED.get();
    }

    @Override
    public boolean getForceTurnWhenInfected() {
        return FORCE_TURN_WHEN_INFECTED.get();
    }

    @Override
    public long getRespawnTimer() {
        return RESPAWN_TIMER.get();
    }

    @Override
    public long getRespawnTimerToZombie() {
        return RESPAWN_TIMER_TO_ZOMBIE.get();
    }

    @Override
    public double getZombieWalkSpeed() {
        return ZOMBIE_WALK_SPEED.get();
    }
}
