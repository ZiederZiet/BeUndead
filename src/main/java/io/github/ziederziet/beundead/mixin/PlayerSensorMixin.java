package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(PlayerSensor.class)
public abstract class PlayerSensorMixin extends Sensor<LivingEntity> {
    @Overwrite
    protected void doTick(ServerLevel pLevel, LivingEntity pEntity) {
        Stream var10000 = pLevel.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((p_26744_) -> {
            return pEntity.closerThan(p_26744_, 16.0) && p_26744_ instanceof Player player && BeUndead.getZombieType(player) > 0;
        });
        Objects.requireNonNull(pEntity);
        List<Player> $$2 = (List)var10000.sorted(Comparator.comparingDouble(entity -> pEntity.distanceToSqr((Entity) entity))).collect(Collectors.toList());
        Brain<?> $$3 = pEntity.getBrain();
        $$3.setMemory(MemoryModuleType.NEAREST_PLAYERS, $$2);
        List<Player> $$4 = (List)$$2.stream().filter((p_26747_) -> {
            return isEntityTargetable(pEntity, p_26747_);
        }).collect(Collectors.toList());
        $$3.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, $$4.isEmpty() ? null : (Player)$$4.get(0));
        Optional<Player> $$5 = $$4.stream().filter((p_148304_) -> {
            return isEntityAttackable(pEntity, p_148304_);
        }).findFirst();
        $$3.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, $$5);
    }
}
