package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(at = @At("HEAD"), method = "canStartSprinting()Z", cancellable = true)
    private boolean canStartSprinting(CallbackInfoReturnable<Boolean> info) {
        if (BeUndead.getZombieType((Player) (Object)this) > 0){
            info.setReturnValue(false);
            info.cancel();
            return false;
        }
        return false;
    }
}
