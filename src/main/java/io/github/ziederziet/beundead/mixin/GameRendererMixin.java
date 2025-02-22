package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At("HEAD"), method = "getNightVisionScale", cancellable = true)
    private static void getNightVisionScale(LivingEntity pLivingEntity, float pNanoTime, CallbackInfoReturnable<Float> info){
        if (pLivingEntity instanceof Player player && BeUndead.getZombieType(player) > 0){
            info.setReturnValue(1.0F);
        }
    }
}
