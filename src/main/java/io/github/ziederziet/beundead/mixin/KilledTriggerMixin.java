package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(KilledTrigger.class)
public abstract class KilledTriggerMixin extends SimpleCriterionTrigger {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/KilledTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Predicate;)V"), method = "trigger")
    public void trigger(KilledTrigger instance, ServerPlayer player, Predicate predicate, ServerPlayer serverPlayer, Entity entity, DamageSource damageSource){
//        LootContext lootContext = EntityPredicate.createContext(serverPlayer, entity);
//        this.trigger(serverPlayer, (triggerObject) -> {
//            KilledTrigger.TriggerInstance triggerInstance = (((KilledTrigger.TriggerInstance)triggerObject));
//            if (triggerInstance.matches(serverPlayer, lootContext, damageSource)){
//                return true;
//            }
//            if (entity instanceof Player killedPlayer && !BeUndeadHelper.isHuman(killedPlayer)){
//                Optional<ContextAwarePredicate> oContextAwarePredicate = triggerInstance.entityPredicate();
//                if (oContextAwarePredicate.isPresent()){
//                    ContextAwarePredicate contextAwarePredicate = oContextAwarePredicate.get();
//                    LootItemCondition[] lootItemConditions = ((ContextAwarePredicateAccessor)contextAwarePredicate).getConditions();
//                    if (lootItemConditions.length == 1){
//                        if (lootItemConditions[0] instanceof LootItemEntityPropertyCondition entityPropertyCondition){
//                            if (entityPropertyCondition.entityTarget() == LootContext.EntityTarget.THIS){
//                                return entityPropertyCondition.predicate().map(entityPredicate -> {
//                                    return entityPredicate.entityType().isPresent() && entityPredicate.entityType().get().matches(EntityType.ZOMBIE);
//                                }).orElseGet(() -> false);
//                            }
//                        }
//                        return true;
//                    }
//                }
//            }
//            return false;
//        });
    }
}
