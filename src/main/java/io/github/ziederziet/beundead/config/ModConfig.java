package io.github.ziederziet.beundead.config;

import io.github.ziederziet.beundead.BeUndead;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = BeUndead.MODID)
public class ModConfig implements ConfigData, ClientConfigAccessor, ServerConfigAccessor {
    public boolean zombieSoundsPlayers = true;

    public InventoryState zombieInvState = InventoryState.ONE_SLOT;
    public boolean zombieCanChestExtension = true;
    public CureRequirements cureRequirements = CureRequirements.WEAKNESS_AND_APPLE;
    public boolean husksEnabled = true;
    public boolean drownedEnabled = true;
    public float zombieBreakSpeed = 0.5F;
    public boolean zombieNightVision = true;
    public boolean zombieJumpOnTheirOwn = true;
    public boolean zombieCanCrit = true;
    public boolean zombieOnlyKillExperience = true;
    public int zombieMaxViewDistance = 4;
    public double zombieWalkSpeed = 1D;
    public boolean zombieSprintEnabled = false;
    public boolean infectionEnabled = false;
    public boolean onlyTurnWhenInfected = false;
    public boolean forceTurnWhenInfected = true;
    public long respawnTimerToHuman = 0L;
    public long respawnTimerToZombie = 0L;

    @Override
    public boolean hasZombieSoundsPlayers() {
        return zombieSoundsPlayers;
    }

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
    public boolean getZombieOnlyKillExperience() {
        return zombieOnlyKillExperience;
    }

    @Override
    public boolean isZombieSprintingEnabled() {
        return zombieSprintEnabled;
    }

    @Override
    public int getCureRequirements() {
        return cureRequirements.getId();
    }
}
