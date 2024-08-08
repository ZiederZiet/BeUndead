package io.github.ziederziet.beundead.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(TargetingConditions.class)
public interface TargetingConditionsAccessor {
    @Accessor("selector")
    public Predicate<LivingEntity> getSelector();
}
