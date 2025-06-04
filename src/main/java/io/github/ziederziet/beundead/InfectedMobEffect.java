package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.common.InfectionAccessor;
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
        ((InfectionAccessor)pLivingEntity).infectBy(null, 30, 30);
    }
}
