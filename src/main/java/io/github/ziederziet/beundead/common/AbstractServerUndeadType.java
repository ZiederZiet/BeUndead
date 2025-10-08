package io.github.ziederziet.beundead.common;

public abstract class AbstractServerUndeadType extends UndeadType {
    final String stepSound;
    final String hurtSound;
    final String deathSound;
    final String ambientSound;

    public AbstractServerUndeadType(String name, float r, float g, float b, float rOffset, float gOffset, float bOffset, boolean canSwimInWater, boolean breathUnderwater, boolean burnsInTheSun, boolean fireImmune, boolean freezeImmune, String stepSound, String hurtSound, String deathSound, String ambientSound) {
        super(name, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune);
        this.stepSound = stepSound;
        this.hurtSound = hurtSound;
        this.deathSound = deathSound;
        this.ambientSound = ambientSound;
    }

    public String stepSound(){
        return stepSound;
    }

    public String hurtSound(){
        return hurtSound;
    }

    public String deathSound(){
        return deathSound;
    }

    public String ambientSound(){
        return ambientSound;
    }
}