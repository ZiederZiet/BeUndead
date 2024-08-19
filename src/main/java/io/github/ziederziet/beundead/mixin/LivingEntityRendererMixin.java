package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.ziederziet.beundead.BeUndead;
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
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ForgeEventFactoryClient;
import net.minecraftforge.client.event.RenderNameTagEvent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();

    @Shadow
    protected M model;

    private void renderNameTag(T pEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, float pPartialTick) {
        EntityRenderDispatcher entityRenderDispatcher = ((EntityRendererAccessor) (Object) this).getEntityRenderDispatcher();
        double d0 = entityRenderDispatcher.distanceToSqr(pEntity);
        if (ForgeHooksClient.isNameplateInRenderDistance(pEntity, d0)) {
            Vec3 vec3 = pEntity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, pEntity.getViewYRot(pPartialTick));
            if (vec3 != null) {
                boolean flag = !pEntity.isDiscrete();
                int i = "deadmau5".equals(pDisplayName.getString()) ? -10 : 0;
                pPoseStack.pushPose();
                pPoseStack.translate(vec3.x, vec3.y + 0.5, vec3.z);
                pPoseStack.mulPose(entityRenderDispatcher.cameraOrientation());
                pPoseStack.scale(0.025F, -0.025F, 0.025F);
                Matrix4f matrix4f = pPoseStack.last().pose();
                float f = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                int j = (int)(f * 255.0F) << 24;
                Font font = ((LivingEntityRenderer)(Object)this).getFont();
                float f1 = (float)(-font.width(pDisplayName) / 2);
                font.drawInBatch(pDisplayName, f1, (float)i, 553648127, false, matrix4f, pBufferSource, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, pPackedLight);
                if (flag) {
                    font.drawInBatch(pDisplayName, f1, (float)i, -1, false, matrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, pPackedLight);
                }

                pPoseStack.popPose();
            }
        }

    }

    @Shadow
    protected abstract float getAttackAnim(T pLivingBase, float pPartialTickTime);

    @Shadow
    protected abstract float getBob(T pLivingBase, float pPartialTick);

    @Shadow
    protected abstract void setupRotations(T pEntity, PoseStack pPoseStack, float pBob, float pYBodyRot, float pPartialTick, float pScale);

    @Shadow
    protected abstract void scale(T pLivingEntity, PoseStack pPoseStack, float pPartialTickTime);

    @Shadow
    protected abstract boolean isBodyVisible(T pLivingEntity);

    @Shadow
    protected abstract float getWhiteOverlayProgress(T pLivingEntity, float pPartialTicks);

    private <E extends Entity> void renderLeash(T pEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, E pLeashHolder) {
        pPoseStack.pushPose();
        Vec3 vec3 = pLeashHolder.getRopeHoldPosition(pPartialTick);
        double d0 = (double)(pEntity.getPreciseBodyRotation(pPartialTick) * 0.017453292F) + 1.5707963267948966;
        Vec3 vec31 = pEntity.getLeashOffset(pPartialTick);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp((double)pPartialTick, pEntity.xo, pEntity.getX()) + d1;
        double d4 = Mth.lerp((double)pPartialTick, pEntity.yo, pEntity.getY()) + vec31.y;
        double d5 = Mth.lerp((double)pPartialTick, pEntity.zo, pEntity.getZ()) + d2;
        pPoseStack.translate(d1, vec31.y, d2);
        float f = (float)(vec3.x - d3);
        float f1 = (float)(vec3.y - d4);
        float f2 = (float)(vec3.z - d5);
        float f3 = 0.025F;
        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix4f = pPoseStack.last().pose();
        float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
        float f5 = f2 * f4;
        float f6 = f * f4;
        BlockPos blockpos = BlockPos.containing(pEntity.getEyePosition(pPartialTick));
        BlockPos blockpos1 = BlockPos.containing(pLeashHolder.getEyePosition(pPartialTick));
        int i = pEntity.isOnFire() ? 15 : pEntity.level().getBrightness(LightLayer.BLOCK, blockpos);
        int j = pLeashHolder.isOnFire() ? 15 : pEntity.level().getBrightness(LightLayer.BLOCK, blockpos);
        int k = pEntity.level().getBrightness(LightLayer.SKY, blockpos);
        int l = pEntity.level().getBrightness(LightLayer.SKY, blockpos1);

        int j1;
        for(j1 = 0; j1 <= 24; ++j1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.025F, f5, f6, j1, false);
        }

        for(j1 = 24; j1 >= 0; --j1) {
            addVertexPair(vertexconsumer, matrix4f, f, f1, f2, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
        }

        pPoseStack.popPose();
    }

    private static void addVertexPair(VertexConsumer pBuffer, Matrix4f pPose, float pStartX, float pStartY, float pStartZ, int pEntityBlockLight, int pHolderBlockLight, int pEntitySkyLight, int pHolderSkyLight, float pYOffset, float pDy, float pDx, float pDz, int pIndex, boolean pReverse) {
        float f = (float)pIndex / 24.0F;
        int i = (int)Mth.lerp(f, (float)pEntityBlockLight, (float)pHolderBlockLight);
        int j = (int)Mth.lerp(f, (float)pEntitySkyLight, (float)pHolderSkyLight);
        int k = LightTexture.pack(i, j);
        float f1 = pIndex % 2 == (pReverse ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = 0.5F * f1;
        float f3 = 0.4F * f1;
        float f4 = 0.3F * f1;
        float f5 = pStartX * f;
        float f6 = pStartY > 0.0F ? pStartY * f * f : pStartY - pStartY * (1.0F - f) * (1.0F - f);
        float f7 = pStartZ * f;
        pBuffer.addVertex(pPose, f5 - pDx, f6 + pDy, f7 + pDz).setColor(f2, f3, f4, 1.0F).setLight(k);
        pBuffer.addVertex(pPose, f5 + pDx, f6 + pYOffset - pDy, f7 - pDz).setColor(f2, f3, f4, 1.0F).setLight(k);
    }

    @Shadow
    protected abstract boolean shouldShowName(T pEntity);

    public void renderS(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (!ForgeEventFactoryClient.onRenderLivingPre(pEntity, ((LivingEntityRenderer<T, M>) (Object)this), pPartialTicks, pPoseStack, pBuffer, pPackedLight)) {
            pPoseStack.pushPose();
            this.model.attackTime = this.getAttackAnim(pEntity, pPartialTicks);
            boolean shouldSit = pEntity.isPassenger() && pEntity.getVehicle() != null && pEntity.getVehicle().shouldRiderSit();
            this.model.riding = shouldSit;
            this.model.young = pEntity.isBaby();
            float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
            float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
            float f2 = f1 - f;
            float f7;
            if (shouldSit) {
                Entity var12 = pEntity.getVehicle();
                if (var12 instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity)var12;
                    f = Mth.rotLerp(pPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
                    f2 = f1 - f;
                    f7 = Mth.wrapDegrees(f2);
                    if (f7 < -85.0F) {
                        f7 = -85.0F;
                    }

                    if (f7 >= 85.0F) {
                        f7 = 85.0F;
                    }

                    f = f1 - f7;
                    if (f7 * f7 > 2500.0F) {
                        f += f7 * 0.2F;
                    }

                    f2 = f1 - f;
                }
            }

            float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
            if (LivingEntityRenderer.isEntityUpsideDown(pEntity)) {
                f6 *= -1.0F;
                f2 *= -1.0F;
            }

            f2 = Mth.wrapDegrees(f2);
            float f9;
            if (pEntity.hasPose(Pose.SLEEPING)) {
                Direction direction = pEntity.getBedOrientation();
                if (direction != null) {
                    f9 = pEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                    pPoseStack.translate((float)(-direction.getStepX()) * f9, 0.0F, (float)(-direction.getStepZ()) * f9);
                }
            }

            f7 = pEntity.getScale();
            pPoseStack.scale(f7, f7, f7);
            f9 = this.getBob(pEntity, pPartialTicks);
            this.setupRotations(pEntity, pPoseStack, f9, f, pPartialTicks, f7);
            pPoseStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(pEntity, pPoseStack, pPartialTicks);
            pPoseStack.translate(0.0F, -1.501F, 0.0F);
            float f4 = 0.0F;
            float f5 = 0.0F;
            if (!shouldSit && pEntity.isAlive()) {
                f4 = pEntity.walkAnimation.speed(pPartialTicks);
                f5 = pEntity.walkAnimation.position(pPartialTicks);
                if (pEntity.isBaby()) {
                    f5 *= 3.0F;
                }

                if (f4 > 1.0F) {
                    f4 = 1.0F;
                }
            }

            this.model.prepareMobModel(pEntity, f5, f4, pPartialTicks);
            this.model.setupAnim(pEntity, f5, f4, f9, f2, f6);
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag = this.isBodyVisible(pEntity);
            boolean flag1 = !flag && !pEntity.isInvisibleTo(minecraft.player);
            boolean flag2 = minecraft.shouldEntityAppearGlowing(pEntity);
            RenderType rendertype = null;
            {
                ResourceLocation resourcelocation = ((LivingEntityRenderer)(Object)this).getTextureLocation(pEntity);
                if (flag1) {
                    rendertype = RenderType.itemEntityTranslucentCull(resourcelocation);
                } else if (flag) {
                    rendertype = this.model.renderType(resourcelocation);
                } else {
                    rendertype = flag2 ? RenderType.outline(resourcelocation) : null;
                }
            }
            if (rendertype != null) {
                VertexConsumer vertexconsumer = pBuffer.getBuffer(rendertype);
                //vertexconsumer.setColor(4424760);
                vertexconsumer.setColor(0);
                int i = LivingEntityRenderer.getOverlayCoords(pEntity, this.getWhiteOverlayProgress(pEntity, pPartialTicks));
                this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, i, flag1 ? 654311423 : -1);
            }

            if (!pEntity.isSpectator()) {
                Iterator var26 = this.layers.iterator();

                while(var26.hasNext()) {
                    RenderLayer<T, M> renderlayer = (RenderLayer)var26.next();
                    renderlayer.render(pPoseStack, pBuffer, pPackedLight, pEntity, f5, f4, pPartialTicks, f9, f2, f6);
                }
            }

            pPoseStack.popPose();

            if (pEntity instanceof Leashable leashable) {
                Entity entity = leashable.getLeashHolder();
                if (entity != null) {
                    this.renderLeash(pEntity, pPartialTicks, pPoseStack, pBuffer, entity);
                }
            }

            RenderNameTagEvent event = ForgeEventFactoryClient.fireRenderNameTagEvent(pEntity, pEntity.getDisplayName(), (EntityRenderer<T>) (Object)this, pPoseStack, pBuffer, pPackedLight, pPartialTicks);
            if (!event.getResult().isDenied() && (event.getResult().isAllowed() || this.shouldShowName(pEntity))) {
                this.renderNameTag(pEntity, event.getContent(), pPoseStack, pBuffer, pPackedLight, pPartialTicks);
            }

            //((EntityRenderer)(Object)this).render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
            ForgeEventFactoryClient.onRenderLivingPost(pEntity, ((LivingEntityRenderer<T, M>)(Object)this), pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        }
    }
}
