package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
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
    public Input input;

    @Shadow
    private int autoJumpTime;

    @Inject(at = @At("HEAD"), method = "canStartSprinting()Z", cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> info) {
        if (BeUndead.getZombieType((Player) (Object)this) > 0){
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isAutoJumpEnabled()Z", cancellable = true)
    public void isAutoJumpEnabled(CallbackInfoReturnable<Boolean> info){
        if (!BeUndead.canJump((LocalPlayer)(Object)this)){
            info.setReturnValue(true);
        }
    }

    private int waterAutoJumpTicks = 0;

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void tick(CallbackInfo info){
        if (waterAutoJumpTicks > 0){
            waterAutoJumpTicks--;
            autoJumpTime = 1;
        }
    }

    @Inject(at = @At("TAIL"), method = "updateAutoJump(FF)V")
    protected void updateAutoJump(float pMovementX, float pMovementZ, CallbackInfo info){
        if (this.autoJumpTime > 0 && waterAutoJumpTicks == 0 && ((Player)(Object)this).isInWater()){
            this.waterAutoJumpTicks = 7;
        }
    }
}
