package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {
    @Inject(at = @At("HEAD"), method = "isHostile(Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    private void isHostile(LivingEntity pEntity, CallbackInfoReturnable<Boolean> info) {
        if (pEntity instanceof Player player && BeUndeadApi.getZombieType(player) > 0){
            info.cancel();
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "isClose(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    private void isClose(LivingEntity pAttacker, LivingEntity pTarget, CallbackInfoReturnable<Boolean> info) {
        if (pTarget instanceof Player){
            float $$2 = 8.0F;
            info.setReturnValue(pTarget.distanceToSqr(pAttacker) <= (double)($$2 * $$2));
            info.cancel();
        }
    }
}
