package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slime.class)
public class SlimeMixin {
    @Inject(at = @At("HEAD"), method = "dealDamage(Lnet/minecraft/world/entity/LivingEntity;)V", cancellable = true)
    protected void dealDamage(LivingEntity pLivingEntity, CallbackInfo info){
        if (pLivingEntity instanceof Player player && BeUndeadApi.getZombieType(player) > 0){
            info.cancel();
        }
    }
}