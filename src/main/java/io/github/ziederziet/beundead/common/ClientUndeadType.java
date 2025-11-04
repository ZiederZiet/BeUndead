package io.github.ziederziet.beundead.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ClientUndeadType extends UndeadType {
    final SoundEvent stepSound;
    final SoundEvent hurtSound;
    final SoundEvent deathSound;
    final SoundEvent ambientSound;
    final ResourceLocation overlayTexture;

    public ClientUndeadType(String name, float r, float g, float b, float rOffset, float gOffset, float bOffset, boolean canSwimInWater, boolean breathUnderwater, boolean burnsInTheSun, boolean fireImmune, boolean freezeImmune, SoundEvent stepSound, SoundEvent hurtSound, SoundEvent deathSound, SoundEvent ambientSound, ResourceLocation overlayTexture) {
        super(name, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune);
        this.stepSound = stepSound;
        this.hurtSound = hurtSound;
        this.deathSound = deathSound;
        this.ambientSound = ambientSound;
        this.overlayTexture = overlayTexture;
    }

    public SoundEvent stepSound(){
        return stepSound;
    }

    public SoundEvent hurtSound(){
        return hurtSound;
    }

    public SoundEvent deathSound(){
        return deathSound;
    }

    public SoundEvent ambientSound(){
        return ambientSound;
    }

    public ResourceLocation overlayTexture(){
        return overlayTexture;
    }
}