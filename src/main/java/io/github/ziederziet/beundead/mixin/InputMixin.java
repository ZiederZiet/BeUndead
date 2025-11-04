package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Input.class)
public class InputMixin  {
    @Inject(at = @At("HEAD"), method = "jump", cancellable = true)
    public void jumpInject(CallbackInfoReturnable<Boolean> info){
        if (Minecraft.getInstance().player != null){
            LocalPlayer player = Minecraft.getInstance().player;
            if (!BeUndeadHelper.canJump(player) && !player.getAbilities().flying && !(player.getAbilities().mayfly && !player.onGround() && !player.isInWater())){
                info.setReturnValue(false);
            }
        }
    }
}
