package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;


@Mixin(AvoidEntityGoal.class)
public class AvoidEntityGoalMixin {


    @Shadow @Final protected PathfinderMob mob;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"), method = "canUse")
    public List redirectGetEntitiesToNotAvoidUndead(Level instance, Class aClass, AABB aabb, Predicate<Player> predicate){
        if (mob instanceof Evoker && aClass == Player.class){

            return instance.getEntitiesOfClass(aClass, aabb, predicate.and(player -> BeUndeadHelper.isHuman(player)));
        }

        return instance.getEntitiesOfClass(aClass, aabb, predicate);
    }
}
