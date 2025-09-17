package io.github.ziederziet.beundead.mixin;

import com.mojang.authlib.GameProfile;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo info){
        BeUndeadApi.setZombieType((ServerPlayer)(Object)this, ServerConfigAccessor.getConfig().hasUndeadMode() ? 1 : 0, false);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/PlayerRespawnLogic;getOverworldRespawnPos(Lnet/minecraft/server/level/ServerLevel;II)Lnet/minecraft/core/BlockPos;"), method = "adjustSpawnLocation")
    private BlockPos redirectGetOverworldRespawnPos(ServerLevel level, int i, int j) {
        ServerPlayer serverPlayer = (ServerPlayer)(Object)this;

        if (BeUndeadApi.getZombieType(serverPlayer) > 0){
            return BeUndeadApi.getOverworldRespawnPosForUndead(level, i, j);
        }
        else {
            return PlayerRespawnLogicAccessor.callGetOverworldRespawnPos(level, i, j);
        }
    }
}
