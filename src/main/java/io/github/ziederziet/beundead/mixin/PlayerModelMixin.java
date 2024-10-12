package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.client.model.AnimationUtils.bobArms;

@Mixin(PlayerModel.class)
@OnlyIn(Dist.CLIENT)
public class PlayerModelMixin {

//    @Overwrite
//    public RenderType renderType(ResourceLocation pLocation) {
//        return ((ModelAccessor)this).getUsualRendertype().apply(pLocation);
//    }

    @Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V")
    public void setupAnim(LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo info) {
        //super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        if (pEntity instanceof Player player && BeUndead.getZombieType(player) > 0){

            if (player != Minecraft.getInstance().player || Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON){
                HumanoidModelAccessor humanoidModelAccessor = (HumanoidModelAccessor) this;

                boolean pIsAggressive = false;

                float pAttackTime = ((EntityModelAccessor)this).getAttackTime();
                ModelPart pRightArm = humanoidModelAccessor.getRightArm();
                ModelPart pLeftArm = humanoidModelAccessor.getLeftArm();

                PlayerModelAccessor playerModelAccessor = (PlayerModelAccessor) this;

                ModelPart pRightSleeve = playerModelAccessor.getRightSleeve();
                ModelPart pLeftSleeve = playerModelAccessor.getLeftSleeve();

                float $$5 = Mth.sin(pAttackTime * 3.1415927F);
                float $$6 = Mth.sin((1.0F - (1.0F - pAttackTime) * (1.0F - pAttackTime)) * 3.1415927F);
                pRightArm.zRot = 0.0F;
                pLeftArm.zRot = 0.0F;
                pRightArm.yRot = -(0.1F - $$5 * 0.6F);
                pLeftArm.yRot = 0.1F - $$5 * 0.6F;
                pRightSleeve.zRot = 0.0F;
                pLeftSleeve.zRot = 0.0F;
                pRightSleeve.yRot = -(0.1F - $$5 * 0.6F);
                pLeftSleeve.yRot = 0.1F - $$5 * 0.6F;
                float $$7 = -3.1415927F / (pIsAggressive ? 1.5F : 2.25F);
                pRightArm.xRot = $$7;
                pLeftArm.xRot = $$7;
                pRightArm.xRot += $$5 * 1.2F - $$6 * 0.4F;
                pLeftArm.xRot += $$5 * 1.2F - $$6 * 0.4F;
                pRightSleeve.xRot = $$7;
                pLeftSleeve.xRot = $$7;
                pRightSleeve.xRot += $$5 * 1.2F - $$6 * 0.4F;
                pLeftSleeve.xRot += $$5 * 1.2F - $$6 * 0.4F;
                bobArms(pRightArm, pLeftArm, pAgeInTicks);
                bobArms(pRightSleeve, pLeftSleeve, pAgeInTicks);
            }
        }

        //AnimationUtils.animateZombieArms(humanoidModelAccessor.getLeftArm(), humanoidModelAccessor.getRightArm(), false, ((EntityModelAccessor)this).getAttackTime(), pAgeInTicks);
        //AnimationUtils.animateZombieArms(playerModelAccessor.getLeftArm(), playerModelAccessor.getRightArm(), false, , pAgeInTicks);
    }
}
