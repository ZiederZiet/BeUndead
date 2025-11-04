package io.github.ziederziet.beundead.mixin;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TargetingConditions.class)
public interface TargetingConditionsAccessor {
    @Accessor("selector")
    TargetingConditions.Selector getSelector();
}
