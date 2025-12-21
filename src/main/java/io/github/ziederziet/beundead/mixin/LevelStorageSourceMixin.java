package io.github.ziederziet.beundead.mixin;

import com.mojang.serialization.Dynamic;
import io.github.ziederziet.beundead.client.LevelSummaryAccessor;
import io.github.ziederziet.beundead.config.PerWorldConfig;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {
    @Inject(at = @At("RETURN"), method = "makeLevelSummary")
    void makeLevelSummary(Dynamic<?> pDynamic, LevelStorageSource.LevelDirectory pLevelDirectory, boolean pLocked, CallbackInfoReturnable<LevelSummary> info){
        PerWorldConfig config = PerWorldConfig.load(pLevelDirectory);
        if (config != null){
            ((LevelSummaryAccessor)info.getReturnValue()).setUndead(config.undeadMode);
        }
    }
}