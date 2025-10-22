package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.server.level.ServerLevel;
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
    @Inject(at = @At("TAIL"), method = "isEntityTargetable", cancellable = true)
    private static void isEntityTargetable(ServerLevel serverLevel, LivingEntity livingEntity, LivingEntity target, CallbackInfoReturnable<Boolean> info){
        if (target instanceof Player player && !BeUndeadHelper.isHuman(player)){
            if (livingEntity.getBrain().hasMemoryValue(MemoryModuleType.ANGRY_AT)){
                UUID uuid = livingEntity.getBrain().getMemory(MemoryModuleType.ANGRY_AT).get();
                if (uuid.getMostSignificantBits() == target.getUUID().getMostSignificantBits() &&
                        uuid.getLeastSignificantBits() == target.getUUID().getLeastSignificantBits()){
                    info.setReturnValue(true);
                }
            }
            info.setReturnValue(false);
        }
    }
}