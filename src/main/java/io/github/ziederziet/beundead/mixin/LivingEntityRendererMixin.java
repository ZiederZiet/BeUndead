package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(at = @At("HEAD"), method = "isShaking(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    protected void isShaking(LivingEntity pEntity, CallbackInfoReturnable<Boolean> info) {
        if (pEntity instanceof Player player && BeUndeadApi.getZombieType(player) > 0 && BeUndeadApi.isZombieConverting(player)){
            info.setReturnValue(true);
        }
    }
}