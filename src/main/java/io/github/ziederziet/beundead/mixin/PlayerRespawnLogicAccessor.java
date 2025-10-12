package io.github.ziederziet.beundead.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerRespawnLogic.class)
public interface PlayerRespawnLogicAccessor {
    @Invoker("getOverworldRespawnPos")
    static BlockPos callGetOverworldRespawnPos(ServerLevel level, int i, int j) {
        throw new AssertionError();
    }
}
