package io.github.ziederziet.beundead.common;

public class UnserializizedServerUndeadType extends AbstractServerUndeadType {
    final String[] mobEffects;

    public UnserializizedServerUndeadType(String name, float r, float g, float b, float rOffset, float gOffset, float bOffset, boolean canSwimInWater, boolean breathUnderwater, boolean burnsInTheSun, boolean fireImmune, boolean freezeImmune, String[] mobEffects, String stepSound, String hurtSound, String deathSound, String ambientSound) {
        super(name, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune, stepSound, hurtSound, deathSound, ambientSound);
        this.mobEffects = mobEffects;
    }

    public String[] mobEffects(){
        return mobEffects;
    }
}