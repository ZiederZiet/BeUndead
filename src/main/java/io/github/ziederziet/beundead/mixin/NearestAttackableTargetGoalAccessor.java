package io.github.ziederziet.beundead.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NearestAttackableTargetGoal.class)
public interface NearestAttackableTargetGoalAccessor {
    @Accessor("targetConditions")
    public TargetingConditions getTargetingConditions();
    @Accessor("randomInterval")
    public int getRandomInterval();

    @Accessor("targetType")
    public Class getTargetType();
}
