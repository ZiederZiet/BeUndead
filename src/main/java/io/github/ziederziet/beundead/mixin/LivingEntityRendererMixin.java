package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.client.CustomRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class LivingEntityRendererMixin {
    @Inject(at = @At("HEAD"), method = "getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;", cancellable = true)
    protected void getRenderType(LivingEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing, CallbackInfoReturnable<RenderType> info){
        if (pLivingEntity instanceof Player player && BeUndead.getZombieType(player) > 0){
            info.cancel();
            ResourceLocation resourcelocation = ((EntityRenderer<LivingEntity>)(Object)this).getTextureLocation(pLivingEntity);
            BeUndead.setZombieColors(BeUndead.getZombieType(player));
            info.setReturnValue(CustomRenderTypes.zombieEntityTranslucent(resourcelocation));
        }
    }

    @Inject(at = @At("HEAD"), method = "isShaking(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    protected void isShaking(LivingEntity pEntity, CallbackInfoReturnable<Boolean> info) {
        if (pEntity instanceof Player player && BeUndead.getZombieType(player) > 0 && BeUndead.getZombieConversionTime(player) >= 0){
            info.setReturnValue(true);
        }
    }
}
