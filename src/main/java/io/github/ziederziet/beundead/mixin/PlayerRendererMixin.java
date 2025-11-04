package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.client.UndeadRenderState;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.client.ZombieChestLayer;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer {
    public PlayerRendererMixin(EntityRendererProvider.Context context, EntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(EntityRendererProvider.Context pContext, boolean pUseSlimModel, CallbackInfo info){
        addLayer(new ZombieChestLayer((PlayerRenderer)(Object)this, pContext.getModelSet()));
    }

    @Inject(at = @At("TAIL"), method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V")
    public void extractRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo info){
        UndeadRenderState state = (UndeadRenderState) playerRenderState;
        state.setType(BeUndeadHelper.getUndeadTypeName(abstractClientPlayer));
        state.setShaking(BeUndeadHelper.isZombieConverting(abstractClientPlayer));
        state.setChestExtension(BeUndeadHelper.hasZombieChest(abstractClientPlayer));
    }

    @Inject(at = @At("TAIL"), method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true, order = 100)
    public void getTextureLocation(PlayerRenderState playerRenderState, CallbackInfoReturnable<ResourceLocation> info) {
        if (!((UndeadRenderState)playerRenderState).isHuman()){
            String type = ((UndeadRenderState)playerRenderState).getType();
            info.setReturnValue(UndeadSkinManager.getOrCreateSkin(info.getReturnValue(), type, playerRenderState.skin.textureUrl()));
        }
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/model/geom/ModelPart;Z)V"), method = "renderRightHand")
    private ResourceLocation renderRightHandInjection(ResourceLocation resourceLocation){
        LocalPlayer player = Minecraft.getInstance().player;
        if (!BeUndeadHelper.isHuman(player)){
            return UndeadSkinManager.getOrCreateSkin(resourceLocation, BeUndeadHelper.getUndeadTypeName(player), player.getSkin().textureUrl());
        }
        return resourceLocation;
    }
}