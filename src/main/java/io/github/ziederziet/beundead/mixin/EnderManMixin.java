package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EnderManMixin {
    @Inject(at = @At("HEAD"), method = "isLookingAtMe", cancellable = true)
    void isLookingAtMe(Player player, CallbackInfoReturnable<Boolean> info){
        if (!BeUndeadHelper.isHuman(player)){
            info.setReturnValue(false);
        }
    }
}
