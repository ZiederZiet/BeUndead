package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "isSprinting()Z", cancellable = true)
    public boolean isSprinting(CallbackInfoReturnable<Boolean> info) {
        if ((Object)this instanceof Player player && BeUndead.getZombieType(player) > 0){
            info.setReturnValue(false);
            info.cancel();
            return false;
        }
        return false;
    }
}
