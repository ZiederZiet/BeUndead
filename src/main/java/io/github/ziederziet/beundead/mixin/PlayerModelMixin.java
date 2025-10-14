package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.model.AnimationUtils.bobModelPart;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {

    @Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V")
    public void setupAnim(LivingEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo info) {
        if (pEntity instanceof Player player && !BeUndeadHelper.isHuman(player)){

            if (player != Minecraft.getInstance().player || Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON){
                HumanoidModelAccessor humanoidModelAccessor = (HumanoidModelAccessor) this;

                boolean pIsAggressive = false;

                float pAttackTime = ((EntityModelAccessor)this).getAttackTime();
                ModelPart pRightArm = humanoidModelAccessor.getRightArm();
                ModelPart pLeftArm = humanoidModelAccessor.getLeftArm();

                PlayerModelAccessor playerModelAccessor = (PlayerModelAccessor) this;

                ModelPart pRightSleeve = playerModelAccessor.getRightSleeve();
                ModelPart pLeftSleeve = playerModelAccessor.getLeftSleeve();
                
                boolean doesRight = false; // MAIN HAND
                boolean doesLeft = false;

                if (player.isUsingItem()){
                    switch (player.getUseItem().getUseAnimation()){
                        case SPYGLASS, TOOT_HORN, SPEAR, BRUSH: {
                            doesLeft = true;
                            break;
                        }
                        case NONE, DRINK, BLOCK, EAT: {
                            doesRight = true;
                            doesLeft = true;
                            break;
                        }
                        default: {
                            break;
                        }
                    }

                    if (player.getUsedItemHand() == InteractionHand.OFF_HAND){
                        boolean doesRightB = doesRight;
                        doesRight = doesLeft;
                        doesLeft = doesRightB;
                    }
                }
                else {
                    doesRight = true;
                    doesLeft = true;
                }

                float $$5 = Mth.sin(pAttackTime * 3.1415927F);
                float $$6 = Mth.sin((1.0F - (1.0F - pAttackTime) * (1.0F - pAttackTime)) * 3.1415927F);
                float $$7 = -3.1415927F / (pIsAggressive ? 1.5F : 2.25F);
                if (doesRight){
                    pRightArm.zRot = 0.0F;
                    pRightArm.yRot = -(0.1F - $$5 * 0.6F);
                    pRightSleeve.zRot = 0.0F;
                    pRightSleeve.yRot = -(0.1F - $$5 * 0.6F);
                    pRightArm.xRot = $$7;
                    pRightArm.xRot += $$5 * 1.2F - $$6 * 0.4F;
                    pRightSleeve.xRot = $$7;
                    pRightSleeve.xRot += $$5 * 1.2F - $$6 * 0.4F;
                    bobModelPart(pRightArm, pAgeInTicks, 1.0F);
                    bobModelPart(pRightSleeve, pAgeInTicks, 1.0F);
                }
                if (doesLeft){
                    pLeftArm.zRot = 0.0F;
                    pLeftArm.yRot = 0.1F - $$5 * 0.6F;
                    pLeftSleeve.zRot = 0.0F;
                    pLeftSleeve.yRot = 0.1F - $$5 * 0.6F;
                    pLeftArm.xRot = $$7;
                    pLeftArm.xRot += $$5 * 1.2F - $$6 * 0.4F;
                    pLeftSleeve.xRot = $$7;
                    pLeftSleeve.xRot += $$5 * 1.2F - $$6 * 0.4F;
                    bobModelPart(pLeftArm, pAgeInTicks, 1.0F);
                    bobModelPart(pLeftSleeve, pAgeInTicks, 1.0F);
                }
            }
        }
    }
}
