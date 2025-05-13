package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.client.ZombieChestLayer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class PlayerRendererMixin {

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(EntityRendererProvider.Context pContext, boolean pUseSlimModel, CallbackInfo info){
        ((PlayerRenderer)(Object)this).addLayer(new ZombieChestLayer((PlayerRenderer)(Object)this, pContext.getModelSet()));
    }

    @Inject(at = @At("HEAD"), method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true)
    public void getTextureLocation(AbstractClientPlayer entity, CallbackInfoReturnable<ResourceLocation> info) {
        if (BeUndeadApi.getZombieType(entity) > 0){
            info.setReturnValue(UndeadSkinManager.getOrCreateSkin(entity.getSkin().texture(), entity));
        }
    }

//    @OnlyIn(Dist.CLIENT)
//    @Inject(at = @At("HEAD"), method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V", cancellable = true)
//    private void renderHand(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer pPlayer, ModelPart pRendererArm, ModelPart pRendererArmwear, CallbackInfo info) {
//        if (BeUndead.getZombieType(pPlayer) > 0){
//            info.cancel();
//            PlayerModel<AbstractClientPlayer> playermodel = (PlayerModel)((PlayerRenderer)(Object)this).getModel();
//            this.setModelProperties(pPlayer);
//            playermodel.attackTime = 0.0F;
//            playermodel.crouching = false;
//            playermodel.swimAmount = 0.0F;
//            playermodel.setupAnim(pPlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
//            pRendererArm.xRot = 0.0F;
//            ResourceLocation resourcelocation = pPlayer.getSkin().texture();
//            BeUndead.setZombieColors(BeUndead.getZombieType(pPlayer));
//            pRendererArm.render(pPoseStack, pBuffer.getBuffer(CustomRenderTypes.zombieEntitySolid(resourcelocation)), pCombinedLight, OverlayTexture.NO_OVERLAY);
//            pRendererArmwear.xRot = 0.0F;
//            pRendererArmwear.render(pPoseStack, pBuffer.getBuffer(CustomRenderTypes.zombieEntityTranslucent(resourcelocation)), pCombinedLight, OverlayTexture.NO_OVERLAY);
//        }
//    }
}
