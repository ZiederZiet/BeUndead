package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.client.ZombieChestLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
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

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(EntityRendererProvider.Context pContext, boolean pUseSlimModel, CallbackInfo info){
        addLayer(new ZombieChestLayer((PlayerRenderer)(Object)this, pContext.getModelSet()));
    }

    @Inject(at = @At("HEAD"), method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true, order = 100)
    public void getTextureLocation(AbstractClientPlayer entity, CallbackInfoReturnable<ResourceLocation> info) {
        int type = BeUndeadApi.getZombieType(entity);
        if (type > 0){
            info.setReturnValue(UndeadSkinManager.getOrCreateSkin(entity.getSkinTextureLocation(), type, entity));
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;getSkinTextureLocation()Lnet/minecraft/resources/ResourceLocation;"), method = "renderHand", order = 100)
    private ResourceLocation texture(AbstractClientPlayer instance, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer, ModelPart modelPart, ModelPart modelPart2){
        int type = BeUndeadApi.getZombieType(abstractClientPlayer);
        ResourceLocation location = abstractClientPlayer.getSkinTextureLocation();
        if (type > 0){
            return UndeadSkinManager.getOrCreateSkin(location, type, abstractClientPlayer);
        }
        return location;
    }
}