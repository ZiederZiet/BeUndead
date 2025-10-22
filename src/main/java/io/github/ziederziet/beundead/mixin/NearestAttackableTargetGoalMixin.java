package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.server.level.ServerLevel;
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

import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin {
    @Shadow protected TargetingConditions targetConditions;

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;IZZLnet/minecraft/world/entity/ai/targeting/TargetingConditions$Selector;)V")
    public void init(Mob mob, Class class_, int i, boolean bl, boolean bl2, TargetingConditions.Selector selector, CallbackInfo info){
        if (this.targetConditions != null){
            // TODO FIX ATTACKING
            TargetingConditions.Selector s = ((TargetingConditionsAccessor)this.targetConditions).getSelector();
            if (s != null){
                Predicate<LivingEntity> selector2 = entity -> s.test(entity, (ServerLevel) entity.level());
                Predicate<LivingEntity> addedSelector;
                if (mob instanceof IronGolem){
                    addedSelector = livingEntity -> livingEntity instanceof Player player && !BeUndeadHelper.isHuman(player);
                } else {
                    addedSelector = livingEntity -> !(livingEntity instanceof Player player) || BeUndeadHelper.isHuman(player);
                }
                if (selector2 == null){
                    selector2 = addedSelector;
                } else {
                    if (mob instanceof IronGolem){
                        selector2 = selector2.or(addedSelector);
                    } else {
                        selector2 = selector2.and(addedSelector);
                    }
                }
                Predicate<LivingEntity> finalSelector2 = selector2;
                this.targetConditions = this.targetConditions.selector((livingEntity, level) -> finalSelector2.test(livingEntity));
            }
        }
    }
}
