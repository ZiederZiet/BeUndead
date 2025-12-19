package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.client.ZombieChestLayer;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer {

    public PlayerRendererMixin(EntityRendererProvider.Context context, EntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

//    @Inject(at = @At("TAIL"), method = "<init>")
//    public void init(EntityRendererProvider.Context pContext, boolean pUseSlimModel, CallbackInfo info){
//        addLayer(new ZombieChestLayer((PlayerRenderer)(Object)this, pContext.getModelSet()));
//    }

    @Inject(at = @At("HEAD"), method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true, order = 100)
    public void getTextureLocation(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> info) {
        if (!BeUndeadHelper.isHuman(player)){
            info.setReturnValue(UndeadSkinManager.getOrCreateSkin(player.getSkin().texture(), BeUndeadHelper.getUndeadTypeName(player), player));
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/PlayerSkin;texture()Lnet/minecraft/resources/ResourceLocation;"), method = "renderHand", order = 100)
    private ResourceLocation texture(PlayerSkin instance, PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight, AbstractClientPlayer player, ModelPart pRendererArm, ModelPart pRendererArmwear){
        ResourceLocation location = player.getSkin().texture();
        if (!BeUndeadHelper.isHuman(player)){
            return UndeadSkinManager.getOrCreateSkin(location, BeUndeadHelper.getUndeadTypeName(player), player);
        }
        return location;
    }
}