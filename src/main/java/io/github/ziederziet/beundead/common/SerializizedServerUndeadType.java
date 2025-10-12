package io.github.ziederziet.beundead.common;

import net.minecraft.world.effect.MobEffectInstance;

public class SerializizedServerUndeadType extends AbstractServerUndeadType {
    final MobEffectInstance[] mobEffects;

    public SerializizedServerUndeadType(String name, float r, float g, float b, float rOffset, float gOffset, float bOffset, boolean canSwimInWater, boolean breathUnderwater, boolean burnsInTheSun, boolean fireImmune, boolean freezeImmune, MobEffectInstance[] mobEffects, String stepSound, String hurtSound, String deathSound, String ambientSound) {
        super(name, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune, stepSound, hurtSound, deathSound, ambientSound);
        this.mobEffects = mobEffects;
    }

    public MobEffectInstance[] mobEffects(){
        return mobEffects;
    }
}