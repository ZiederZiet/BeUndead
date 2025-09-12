package io.github.ziederziet.beundead.config;

import io.github.ziederziet.beundead.BeUndead;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = BeUndead.MODID)
public class ModConfig implements ConfigData, ConfigAccessor {
    public InventoryState zombieInvState = InventoryState.ONE_SLOT;
    public boolean zombieCanChestExtension = true;
    public boolean husksEnabled = true;
    public boolean drownedEnabled = true;
    public boolean zombieNightVision = true;
    public boolean zombieJumpOnTheirOwn = true;
    public boolean zombieCanCrit = true;
    public int zombieMaxViewDistance = 4;
    public double zombieWalkSpeed = 1D;
    public float zombieBreakSpeed = 0.5F;
    public boolean zombieSprintEnabled = false;
    public CureRequirements cureRequirements = CureRequirements.WEAKNESS_AND_APPLE;
    public boolean infectionEnabled = false;
    public boolean onlyTurnWhenInfected = false;
    public boolean forceTurnWhenInfected = true;
    public long respawnTimerToHuman = 0L;
    public long respawnTimerToZombie = 0L;

    public boolean undeadMode;

    @Override
    public int getZombieInvState() {
        return zombieInvState.getId();
    }

    @Override
    public boolean getZombieCanChestExtension() {
        return zombieCanChestExtension;
    }

    @Override
    public boolean areHusksEnabled() {
        return husksEnabled;
    }

    @Override
    public boolean areDrownedEnabled() {
        return drownedEnabled;
    }

    @Override
    public boolean getZombieNightVision() {
        return zombieNightVision;
    }

    @Override
    public boolean getZombieJumpOnTheirOwn() {
        return zombieJumpOnTheirOwn;
    }

    @Override
    public boolean getZombieCanCrit() {
        return zombieCanCrit;
    }

    @Override
    public int getZombieMaxViewDistance() {
        return zombieMaxViewDistance;
    }

    @Override
    public boolean isInfectionEnabled() {
        return infectionEnabled;
    }

    @Override
    public boolean getOnlyTurnWhenInfected() {
        return onlyTurnWhenInfected;
    }

    @Override
    public boolean getForceTurnWhenInfected() {
        return forceTurnWhenInfected;
    }

    @Override
    public long getRespawnTimer() {
        return respawnTimerToHuman;
    }

    @Override
    public long getRespawnTimerToZombie() {
        return respawnTimerToZombie;
    }

    @Override
    public double getZombieWalkSpeed() {
        return zombieWalkSpeed;
    }

    @Override
    public float getZombieBreakSpeed() {
        return zombieBreakSpeed;
    }

    @Override
    public boolean isZombieSprintingEnabled() {
        return zombieSprintEnabled;
    }

    @Override
    public int getCureRequirements() {
        return cureRequirements.getId();
    }

    @Override
    public boolean hasUndeadMode() {
        return undeadMode;
    }

//    public static ModConfig load(Path path) {
//        if (Files.exists(path)) {
//            try {
//                return GSON.fromJson(Files.readString(path), ModConfig.class);
//            } catch (IOException e) {
//                System.out.println("Loading config for Be Undead failed!");
//                e.printStackTrace();
//            }
//        }
//        return new ModConfig();
//    }
//
//    public void save(Path path) {
//        try {
//            Files.createDirectories(path.getParent());
//            Files.writeString(path, GSON.toJson(this));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}