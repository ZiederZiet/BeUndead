package io.github.ziederziet.beundead.goal;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.mixin.NearestAttackableTargetGoalAccessor;
import io.github.ziederziet.beundead.mixin.TargetGoalAccessor;
import io.github.ziederziet.beundead.mixin.TargetingConditionsAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CustomNearestAttackablePlayerGoal extends NearestAttackableTargetGoal {
    public CustomNearestAttackablePlayerGoal(Mob pMob, Class pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, Predicate<LivingEntity> pTargetPredicate) {
        super(pMob, pTargetType, pRandomInterval, pMustSee, pMustReach, pTargetPredicate);
    }

    public static CustomNearestAttackablePlayerGoal copyFrom(NearestAttackableTargetGoal ogGoal, int zombieType, boolean overrideOldPredictate){
        NearestAttackableTargetGoalAccessor nearestAttackableTargetGoalAccessor = (NearestAttackableTargetGoalAccessor) ogGoal;
        TargetGoalAccessor targetGoalAccessor = (TargetGoalAccessor) ogGoal;
        Predicate<LivingEntity> targetingConditions;
        Predicate<LivingEntity> additionalTargetingConditions = playerEntity -> BeUndead.getZombieType((Player) playerEntity) != 0;
        if (zombieType >= 0){
            additionalTargetingConditions = playerEntity -> BeUndead.getZombieType((Player) playerEntity) == zombieType;
        }
        if (!overrideOldPredictate){
            targetingConditions = ((TargetingConditionsAccessor)nearestAttackableTargetGoalAccessor.getTargetingConditions()).getSelector();
            if (targetingConditions != null){
                targetingConditions = targetingConditions.and(additionalTargetingConditions);
            }
            else {
                targetingConditions = additionalTargetingConditions.and(entity -> true);
            }
        }
        else {
            targetingConditions = additionalTargetingConditions.and(entity -> true);
        }

        return new CustomNearestAttackablePlayerGoal(targetGoalAccessor.getMob(), Player.class, nearestAttackableTargetGoalAccessor.getRandomInterval(), targetGoalAccessor.getMustSee(), targetGoalAccessor.getMustReach(), targetingConditions);
    }
}
