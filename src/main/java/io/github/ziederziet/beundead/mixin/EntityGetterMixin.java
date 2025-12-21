package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(EntityGetter.class)
public interface  EntityGetterMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"), method = "hasNearbyAlivePlayer")
    default  <T> boolean hasNearbyAlivePlayer(Predicate instance, T t){
        if (t instanceof Player player && !BeUndeadHelper.isHuman(player)){
            return false;
        }
        return instance.test(t);
    }
}