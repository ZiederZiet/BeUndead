package io.github.ziederziet.beundead.common;

import java.util.UUID;

public interface UndeadAccessor {
    boolean hasZombieChest();
    void setZombieChest(boolean has);
    long getZombieRespawnTimer();
    void setZombieRespawnTimer(long respawnTimer);
    long getZombieConversionTime();
    void setZombieConversionTime(long conversionTime);
    boolean getConverting();
    void setConverting(boolean converting);
    String getType();
    void setType(String type);
    UUID getConversionStarter();
    void setConversionStarter(UUID conversionStarter);
    boolean wasLoaded();
}