package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(NearestVisibleLivingEntities.class)
public class NearestVisibleLivingEntitiesMixin {
    @Shadow
    @Final
    @Mutable
    private Predicate<LivingEntity> lineOfSightTest;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/List;)V", at = @At("TAIL"))
    private void onInit(LivingEntity livingEntity, List<LivingEntity> list, CallbackInfo ci) {
        Object2BooleanOpenHashMap<LivingEntity> object2BooleanOpenHashMap = new Object2BooleanOpenHashMap(list.size());
        Predicate<LivingEntity> predicate = (livingEntity2) -> Sensor.isEntityTargetable(livingEntity, livingEntity2) || (livingEntity2 instanceof Player player && BeUndeadApi.getZombieType(player) > 0);
        this.lineOfSightTest = (livingEntityx) -> object2BooleanOpenHashMap.computeIfAbsent(livingEntityx, predicate);
    }
}
