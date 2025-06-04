package io.github.ziederziet.beundead.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface InfectionAccessor {
    void infectBy(@Nullable Player playerInfecter, int infect, int max);
    void removeInfection(Player player);
    void tick(LivingEntity livingEntity);
    boolean isInfected();
    int getInfected();
}
