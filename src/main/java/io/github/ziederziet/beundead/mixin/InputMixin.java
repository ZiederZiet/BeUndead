package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.InputAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Input.class)
public class InputMixin implements InputAccessor {
    private boolean autoJumped;

    @Inject(at = @At("HEAD"), method = "jump", cancellable = true)
    public void jumpInject(CallbackInfoReturnable<Boolean> info){
        if (Minecraft.getInstance().player != null){
            LocalPlayer player = Minecraft.getInstance().player;
            if (!autoJumped && !BeUndeadHelper.canJump(player)){
                if (!player.getAbilities().flying){
                    info.setReturnValue(false);
                }
            }
        }
    }

    @Override
    public void setAutoJumped(boolean on) {
        autoJumped = on;
    }
}