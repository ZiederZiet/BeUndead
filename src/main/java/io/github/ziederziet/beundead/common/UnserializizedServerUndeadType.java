package io.github.ziederziet.beundead.common;

public class UnserializizedServerUndeadType extends AbstractServerUndeadType {
    final UnserializedUndeadMobEffect[] hitMobEffects;
    private Float moistness;
    private Float heat;
    private String[] damageTypes;

    public UnserializizedServerUndeadType(String name, float r, float g, float b, float rOffset, float gOffset, float bOffset, boolean canSwimInWater, boolean breathUnderwater, boolean burnsInTheSun, boolean fireImmune, boolean freezeImmune, UnserializedUndeadMobEffect[] hitMobEffects, String stepSound, String hurtSound, String deathSound, String ambientSound, Float moistness, Float heat, String[] damageTypes, String overlayTexture) {
        super(name, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune, stepSound, hurtSound, deathSound, ambientSound, overlayTexture);
        this.hitMobEffects = hitMobEffects;
        this.moistness = moistness;
        this.heat = heat;
        this.damageTypes = damageTypes;
    }

    public UnserializedUndeadMobEffect[] mobEffects(){
        return hitMobEffects;
    }

    public Float moistness(){
        return moistness;
    }

    public Float heat(){
        return heat;
    }

    public String[] damageTypes(){
        return damageTypes;
    }
}