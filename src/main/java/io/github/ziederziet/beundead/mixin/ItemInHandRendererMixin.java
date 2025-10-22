package io.github.ziederziet.beundead.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackAnim(F)F"), method = "renderHandsWithItems")
    public float renderHandsWithItems(LocalPlayer instance, float f){
        f = instance.getAttackAnim(f);

        

        return f;
    }
}
