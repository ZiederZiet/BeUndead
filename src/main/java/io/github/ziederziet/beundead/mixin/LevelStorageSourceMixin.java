package io.github.ziederziet.beundead.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import io.github.ziederziet.beundead.client.LevelSummaryAccessor;
import io.github.ziederziet.beundead.config.PerWorldConfig;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.function.BiFunction;

@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {
    @Inject(at = @At("RETURN"), method = "readLevelData")
    <T> void readLevelData(LevelStorageSource.LevelDirectory levelDirectory, BiFunction<Path, DataFixer, T> biFunction, CallbackInfoReturnable<T> info){
        if (info.getReturnValue() instanceof LevelSummary levelSummary){
            PerWorldConfig config = PerWorldConfig.load(levelDirectory);
            if (config != null){
                ((LevelSummaryAccessor)levelSummary).setUndead(config.undeadMode);
            }
        }

    }
}