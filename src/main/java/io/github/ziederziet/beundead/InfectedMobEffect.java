package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.zombie_capability.InfectionZombieCapabilityProvider;
import io.github.ziederziet.beundead.zombie_capability.ZombiePlayerCapabilityProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class InfectedMobEffect extends MobEffect {
    public InfectedMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        pLivingEntity.getCapability(InfectionZombieCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
            zombiePlayerCapability.infectBy(null, 30, 30);
        });
    }
}
