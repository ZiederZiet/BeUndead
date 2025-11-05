package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.common.InputAccessor;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow
    private int autoJumpTime;

    @Shadow public ClientInput input;

    @Inject(at = @At("HEAD"), method = "canStartSprinting()Z", cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> info) {
        if (!BeUndeadHelper.isHuman((Player) (Object)this) && !ClientInfo.zombieSprintEnabled){
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isAutoJumpEnabled()Z", cancellable = true)
    public void isAutoJumpEnabled(CallbackInfoReturnable<Boolean> info){
        if (!BeUndeadHelper.canJump((LocalPlayer)(Object)this)){
            info.setReturnValue(true);
        }
    }

    private int waterAutoJumpTicks = 0;

    @Inject(at = @At("HEAD"), method = "aiStep")
    public void aiStepHead(CallbackInfo info){
        ((InputAccessor)(Object)this.input.keyPresses).setAutoJumped(false);
        if (waterAutoJumpTicks > 0){
            waterAutoJumpTicks--;
            autoJumpTime = 1;
        }
    }

    @Inject(at = @At("TAIL"), method = "aiStep")
    public void aiStepTail(CallbackInfo info){

    }

    @Inject(at = @At("TAIL"), method = "updateAutoJump(FF)V")
    protected void updateAutoJump(float pMovementX, float pMovementZ, CallbackInfo info){
        if (this.autoJumpTime > 0 && waterAutoJumpTicks == 0 && ((Player)(Object)this).isInWater()){
            this.waterAutoJumpTicks = 7;
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;makeJump()V", shift = At.Shift.AFTER), method = "aiStep")
    private void makeJump(CallbackInfo info){
        ((InputAccessor)(Object)this.input.keyPresses).setAutoJumped(true);
    }
}
