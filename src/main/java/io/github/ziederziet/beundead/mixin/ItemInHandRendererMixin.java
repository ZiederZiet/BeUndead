package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(at = @At("HEAD"), method = "renderArmWithItem")
    private void onRenderArmWithItem(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci){
        if (((UndeadAccessor)abstractClientPlayer).getConverting()){
            float shakeIntensity = 0.01f;
            float offsetX = (float)(Math.cos(((float) Mth.floor(abstractClientPlayer.tickCount) * 3.25F)) * Math.PI * (double)0.4F) * shakeIntensity;

            poseStack.translate(offsetX, offsetX * 0.1F, 0.0F);
        }
    }
}