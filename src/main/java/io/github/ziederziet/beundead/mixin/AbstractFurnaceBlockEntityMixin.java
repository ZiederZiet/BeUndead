package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
    @Inject(at = @At("HEAD"), method = "awardUsedRecipesAndPopExperience", cancellable = true)
    public void awardUsedRecipesAndPopExperience(ServerPlayer serverPlayer, CallbackInfo info){
        if (ServerConfigAccessor.getConfig().getZombieOnlyKillExperience() && BeUndeadApi.getZombieType(serverPlayer) > 0){
            info.cancel();
        }
    }
}