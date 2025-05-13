package io.github.ziederziet.beundead.config;

import io.github.ziederziet.beundead.networking.ZombieSettingsPacket;

public interface ConfigAccessor {
    static ConfigAccessor getConfig(){
        return ModConfig.Instance;
    }

    public static ZombieSettingsPacket getPacket(){
        ConfigAccessor config = getConfig();
        return new ZombieSettingsPacket(config.getZombieInvState(),
                config.getZombieCanChestExtension(),
                config.getZombieNightVision(),
                config.getZombieJumpOnTheirOwn(),
                config.getZombieMaxViewDistance(),
                config.getZombieWalkSpeed());
    }

    int getZombieInvState(); // 0 = one slot | 1 = hotbar | 2 = full inv            CLIENT SIDE
    boolean getZombieCanChestExtension();
    boolean areHusksEnabled();
    boolean areDrownedEnabled();
    boolean getZombieNightVision();
    boolean getZombieJumpOnTheirOwn();
    boolean getZombieCanCrit();
    int getZombieMaxViewDistance();
    boolean getInfection();
    boolean getOnlyTurnWhenInfected();
    boolean getForceTurnWhenInfected();
    // Timer in seconds
    long getRespawnTimer();
    long getRespawnTimerToZombie();
    double getZombieWalkSpeed(); // 0.46   ZOMBIE
}
