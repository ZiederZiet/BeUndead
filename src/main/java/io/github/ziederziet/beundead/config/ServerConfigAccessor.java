package io.github.ziederziet.beundead.config;

import io.github.ziederziet.beundead.networking.ZombieSettingsPacket;

public interface ServerConfigAccessor {
    static ServerConfigAccessor getConfig(){
        return ServerModConfig.get();
    }

    static ZombieSettingsPacket getPacket(){
        ServerConfigAccessor config = getConfig();
        return new ZombieSettingsPacket(config.getZombieInvState(),
                config.getZombieCanChestExtension(),
                config.getZombieNightVision(),
                config.getZombieJumpOnTheirOwn(),
                config.getZombieMaxViewDistance(),
                config.getZombieWalkSpeed(),
                config.isZombieSprintingEnabled());
    }

    int getZombieInvState(); // 0 = one slot | 1 = hotbar | 2 = full inv            CLIENT SIDE
    boolean getZombieCanChestExtension();
    boolean areHusksEnabled();
    boolean areDrownedEnabled();
    boolean getZombieNightVision();
    boolean getZombieJumpOnTheirOwn();
    boolean getZombieCanCrit();
    int getZombieMaxViewDistance();
    boolean isInfectionEnabled();
    boolean getOnlyTurnWhenInfected();
    boolean getForceTurnWhenInfected();
    boolean isZombieSprintingEnabled();
    int getCureRequirements();
    // Timer in seconds
    long getRespawnTimer();
    long getRespawnTimerToZombie();
    double getZombieWalkSpeed(); // 0.46   ZOMBIE
    float getZombieBreakSpeed();
    boolean getZombieOnlyKillExperience();

    boolean hasUndeadMode();
}
