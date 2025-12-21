package io.github.ziederziet.beundead.mixin;

import com.mojang.authlib.GameProfile;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import io.github.ziederziet.beundead.config.PerWorldConfig;
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
        PerWorldConfig config = PerWorldConfig.get();
        BeUndeadHelper.setUndeadType((ServerPlayer)(Object)this, config.hasUndeadMode() ? "zombie" : "", false);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/PlayerRespawnLogic;getOverworldRespawnPos(Lnet/minecraft/server/level/ServerLevel;II)Lnet/minecraft/core/BlockPos;"), method = "adjustSpawnLocation")
    private BlockPos redirectGetOverworldRespawnPos(ServerLevel level, int i, int j) {
        ServerPlayer serverPlayer = (ServerPlayer)(Object)this;

        boolean undeadBurning;

        boolean loaded = ((UndeadAccessor)serverPlayer).wasLoaded();

        if (!loaded){
            PerWorldConfig config = PerWorldConfig.get();
            undeadBurning = config.hasUndeadMode();
        }
        else {
            undeadBurning = !BeUndeadHelper.isHuman(serverPlayer) && BeUndeadHelper.getUndeadType(serverPlayer).burnsInTheSun();
        }

        if (undeadBurning){
            return BeUndeadHelper.getOverworldRespawnPosForUndead(level, i, j);
        }
        else {
            return PlayerRespawnLogicAccessor.callGetOverworldRespawnPos(level, i, j);
        }
    }
}
