package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.client.CustomRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ForgeEventFactoryClient;
import net.minecraftforge.client.event.RenderNameTagEvent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class LivingEntityRendererMixin {
    @Inject(at = @At("HEAD"), method = "getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;", cancellable = true)
    protected RenderType getRenderType(LivingEntity pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing, CallbackInfoReturnable<RenderType> info){
        if (pLivingEntity instanceof Player player && BeUndead.getZombieType(player) > 0){
            info.cancel();
            ResourceLocation resourcelocation = ((EntityRenderer<LivingEntity>)(Object)this).getTextureLocation(pLivingEntity);
            BeUndead.setZombieColors(BeUndead.getZombieType(player));
            info.setReturnValue(CustomRenderTypes.zombieEntityTranslucent(resourcelocation));
            return CustomRenderTypes.zombieEntityTranslucent(resourcelocation);
        }
        return null;
    }

    @Inject(at = @At("HEAD"), method = "isShaking(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    protected boolean isShaking(LivingEntity pEntity, CallbackInfoReturnable<Boolean> info) {
        if (pEntity instanceof Player player && BeUndead.getZombieType(player) > 0 && BeUndead.getZombieConversionTime(player) >= 0){
            info.setReturnValue(true);
            return true;
        }
        return false;
    }
}
