package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.client.UndeadRenderState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<S extends LivingEntityRenderState> {
    @Inject(at = @At("HEAD"), method = "isShaking", cancellable = true)
    protected void isShaking(S livingEntityRenderState, CallbackInfoReturnable<Boolean> info) {
        if (livingEntityRenderState instanceof PlayerRenderState playerRenderState && ((UndeadRenderState)playerRenderState).isShaking()){
            info.setReturnValue(true);
        }
    }
}