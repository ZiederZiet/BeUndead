package io.github.ziederziet.beundead.common;

import net.minecraft.world.effect.MobEffectInstance;

public class SerializizedServerUndeadType extends AbstractServerUndeadType {
    final MobEffectInstance[] hitMobEffects;

    public SerializizedServerUndeadType(String name, float r, float g, float b, float rOffset, float gOffset, float bOffset, boolean canSwimInWater, boolean breathUnderwater, boolean burnsInTheSun, boolean fireImmune, boolean freezeImmune, MobEffectInstance[] mobEffects, String stepSound, String hurtSound, String deathSound, String ambientSound, String overlayTexture) {
        super(name, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune, stepSound, hurtSound, deathSound, ambientSound, overlayTexture);
        this.hitMobEffects = mobEffects;
    }

    public MobEffectInstance[] mobEffects(){
        return hitMobEffects;
    }
}