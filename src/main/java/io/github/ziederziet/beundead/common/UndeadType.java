package io.github.ziederziet.beundead.common;

public abstract class UndeadType{
    final String name;
    final float r;
    final float g;
    final float b;
    final float rOffset;
    final float gOffset;
    final float bOffset;
    final boolean canSwimInWater;
    final boolean breathUnderwater;
    final boolean burnsInTheSun;
    final boolean fireImmune;
    final boolean freezeImmune;

    public UndeadType(String name, float r, float g, float b, float rOffset, float gOffset, float bOffset, boolean canSwimInWater, boolean breathUnderwater, boolean burnsInTheSun, boolean fireImmune, boolean freezeImmune) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
        this.rOffset = rOffset;
        this.gOffset = gOffset;
        this.bOffset = bOffset;
        this.canSwimInWater = canSwimInWater;
        this.breathUnderwater = breathUnderwater;
        this.burnsInTheSun = burnsInTheSun;
        this.fireImmune = fireImmune;
        this.freezeImmune = freezeImmune;
    }

    public String name(){
        return name;
    }

    public float r(){
        return r;
    }

    public float g(){
        return g;
    }

    public float b(){
        return b;
    }

    public float rOffset(){
        return rOffset;
    }

    public float gOffset(){
        return gOffset;
    }

    public float bOffset(){
        return bOffset;
    }

    public boolean canSwimInWater(){
        return canSwimInWater;
    }

    public boolean breathUnderwater(){
        return breathUnderwater;
    }

    public boolean burnsInTheSun(){
        return burnsInTheSun;
    }

    public boolean fireImmune(){
        return fireImmune;
    }

    public boolean freezeImmune(){
        return freezeImmune;
    }
}