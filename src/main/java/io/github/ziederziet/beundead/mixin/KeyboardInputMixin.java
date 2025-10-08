package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(at = @At("TAIL"), method = "tick(ZF)V")
    public void tick(boolean pIsSneaking, float pSneakingSpeedMultiplier, CallbackInfo info){
        if (Minecraft.getInstance().player != null){
            LocalPlayer player = Minecraft.getInstance().player;
            if (!BeUndeadHelper.canJump(player) && !player.getAbilities().flying && !(player.getAbilities().mayfly && !player.onGround())){
                ((KeyboardInput)(Object)this).jumping = false;
            }
        }
    }
}
