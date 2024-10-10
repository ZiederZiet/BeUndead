package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.client.CustomRenderTypes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ForgeEventFactoryClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class PlayerRendererMixin {

    @Shadow
    protected abstract void setModelProperties(AbstractClientPlayer pClientPlayer);

    @OnlyIn(Dist.CLIENT)
    @Inject(at = @At("HEAD"), method = "renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;)V", cancellable = true)
    private void renderHand(PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer pPlayer, ModelPart pRendererArm, ModelPart pRendererArmwear, CallbackInfo info) {
        if (BeUndead.getZombieType(pPlayer) > 0){
            info.cancel();
            PlayerModel<AbstractClientPlayer> playermodel = (PlayerModel)((PlayerRenderer)(Object)this).getModel();
            this.setModelProperties(pPlayer);
            playermodel.attackTime = 0.0F;
            playermodel.crouching = false;
            playermodel.swimAmount = 0.0F;
            playermodel.setupAnim(pPlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            pRendererArm.xRot = 0.0F;
            ResourceLocation resourcelocation = pPlayer.getSkin().texture();
            BeUndead.setZombieColors(BeUndead.getZombieType(pPlayer));
            pRendererArm.render(pPoseStack, pBuffer.getBuffer(CustomRenderTypes.zombieEntitySolid(resourcelocation)), pCombinedLight, OverlayTexture.NO_OVERLAY);
            pRendererArmwear.xRot = 0.0F;
            pRendererArmwear.render(pPoseStack, pBuffer.getBuffer(CustomRenderTypes.zombieEntityTranslucent(resourcelocation)), pCombinedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
