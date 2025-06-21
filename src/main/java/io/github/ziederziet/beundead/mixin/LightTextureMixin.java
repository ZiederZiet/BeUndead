package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.ClientInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @Inject(at = @At("HEAD"), method = "getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F", cancellable = true)
    private static void getBrightness(DimensionType pDimensionType, int pLightLevel, CallbackInfoReturnable<Float> info) {
        if (Minecraft.getInstance().player != null){
            if (BeUndeadApi.getZombieType(Minecraft.getInstance().player) > 0 && ClientInfo.zombieNightVision) {
                info.setReturnValue(1F);
            }
        }
    }
}
