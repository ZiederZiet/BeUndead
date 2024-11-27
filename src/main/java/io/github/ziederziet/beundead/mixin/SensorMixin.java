package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Sensor.class)
public class SensorMixin {
    @Inject(at = @At("TAIL"), method = "isEntityTargetable(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z", cancellable = true)
    private static boolean isEntityTargetable(LivingEntity pLivingEntity, LivingEntity pTarget, CallbackInfoReturnable<Boolean> info){
        if (pTarget instanceof Player player && BeUndead.getZombieType(player) > 0){
            if (pLivingEntity.getBrain().hasMemoryValue(MemoryModuleType.ANGRY_AT)){
                UUID uuid = pLivingEntity.getBrain().getMemory(MemoryModuleType.ANGRY_AT).get();
                if (uuid.getMostSignificantBits() == pTarget.getUUID().getMostSignificantBits() &&
                        uuid.getLeastSignificantBits() == pTarget.getUUID().getLeastSignificantBits()){
                    info.setReturnValue(true);
                    return true;
                }
            }
            info.setReturnValue(false);
            return false;
        }
        return info.getReturnValue();
    }
}
