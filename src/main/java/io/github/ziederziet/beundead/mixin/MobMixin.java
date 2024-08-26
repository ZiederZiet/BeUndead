package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(at = @At("HEAD"), method = "isSunBurnTick()Z", cancellable = true)
    protected boolean isSunBurnTick(CallbackInfoReturnable<Boolean> info) {
        if (BeUndead.Mod.getFoggyDay()){
            info.setReturnValue(false);
            info.cancel();
            return false;
        }
        return false;
    }
}
