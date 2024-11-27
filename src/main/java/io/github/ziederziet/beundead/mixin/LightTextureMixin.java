package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @Inject(at = @At("HEAD"), method = "getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F", cancellable = true)
    private static float getBrightness(DimensionType pDimensionType, int pLightLevel, CallbackInfoReturnable<Float> info) {
        if (Minecraft.getInstance().player != null){
            if (BeUndead.getZombieType(Minecraft.getInstance().player) > 0){
                info.setReturnValue(1F);
                return 1F;
            }
        }
        return 0F;
    }
}
