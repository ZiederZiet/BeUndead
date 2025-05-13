package io.github.ziederziet.beundead.common;

public interface UndeadAccessor {
    boolean hasZombieChest();
    void setZombieChest(boolean has);
    long getZombieRespawnTimer();
    void setZombieRespawnTimer(long respawnTimer);
    long getZombieConversionTime();
    void setZombieConversionTime(long conversionTime);
    int getZombieConversionType();
    void setZombieConversionType(int conversionType);
    boolean getConverting();
    void setConverting(boolean converting);
    int getType();
    void setType(int type);
}