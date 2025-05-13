package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin {
    @Shadow protected TargetingConditions targetConditions;

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V")
    public void init(Mob pMob, Class pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, Predicate pTargetPredicate, CallbackInfo info){
        if (this.targetConditions != null){
            Predicate<LivingEntity> selector = ((TargetingConditionsAccessor)this.targetConditions).getSelector();
            Predicate<LivingEntity> addedSelector;
            if (pMob instanceof IronGolem){
                addedSelector = livingEntity -> livingEntity instanceof Player player && BeUndeadApi.getZombieType(player) > 0;
            } else {
                addedSelector = livingEntity -> !(livingEntity instanceof Player player) || BeUndeadApi.getZombieType(player) == 0;
            }
            if (selector == null){
                selector = addedSelector;
            } else {
                if (pMob instanceof IronGolem){
                    selector = selector.or(addedSelector);
                } else {
                    selector = selector.and(addedSelector);
                }
            }
            this.targetConditions = this.targetConditions.selector(selector);
        }
    }
}
