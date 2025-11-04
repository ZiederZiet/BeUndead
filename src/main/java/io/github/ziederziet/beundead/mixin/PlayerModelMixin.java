package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.client.UndeadRenderState;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin extends HumanoidModel {

    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V")
    public void setupAnim(PlayerRenderState playerRenderState, CallbackInfo info) {
        if (!((UndeadRenderState)playerRenderState).isHuman()){
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, playerRenderState.attackTime, playerRenderState.ageInTicks);
            //AnimationUtils.animateZombieArms(this.leftSleeve, this.rightSleeve, false, playerRenderState.attackTime, playerRenderState.ageInTicks);
        }
    }
}
