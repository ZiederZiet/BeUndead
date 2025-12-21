package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.config.PerWorldConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRespawnLogic.class)
public class PlayerRespawnLogicMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/PlayerRespawnLogic;getOverworldRespawnPos(Lnet/minecraft/server/level/ServerLevel;II)Lnet/minecraft/core/BlockPos;"), method = "getSpawnPosInChunk")
    private static BlockPos redirectGetOverworldRespawnPos(ServerLevel level, int i, int j) {
        if (PerWorldConfig.get().hasUndeadMode()){
            return BeUndeadHelper.getOverworldRespawnPosForUndead(level, i, j);
        }
        else {
            return PlayerRespawnLogicAccessor.callGetOverworldRespawnPos(level, i, j);
        }
    }
}
