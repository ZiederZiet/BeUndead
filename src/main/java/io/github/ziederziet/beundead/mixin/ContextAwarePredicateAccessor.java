package io.github.ziederziet.beundead.mixin;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ContextAwarePredicate.class)
public interface ContextAwarePredicateAccessor {
    @Accessor("conditions")
    List<LootItemCondition> getConditions();
}
