package io.github.ziederziet.beundead.common;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface InfectionAccessor {
    int infectBy(Player infecter, int amount);
    void removeInfection(Player player);
    void setInInfection(int amount);
    void setOutInfection(int amount);
    void setInfectionKillTicks(int ticks);
    int getInInfection();
    int getOutInfection();
    int getInfectionKillTicks();
    UUID getMainInfecterUUID();
}
