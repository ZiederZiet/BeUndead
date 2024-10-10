package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerSensor.class)
public class PlayerSensorMixin {
    @Inject(at = @At("HEAD"), method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V", cancellable = true)
    protected void doTick(ServerLevel pLevel, LivingEntity pEntity, CallbackInfo info){
        if (pEntity instanceof Player player && BeUndead.getZombieType(player) > 0){
            info.cancel();
        }
    }
}
