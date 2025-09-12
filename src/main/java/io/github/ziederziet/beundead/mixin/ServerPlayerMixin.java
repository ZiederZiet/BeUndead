package io.github.ziederziet.beundead.mixin;

import com.mojang.authlib.GameProfile;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation, CallbackInfo info){
        BeUndeadApi.setZombieType((ServerPlayer)(Object)this, ConfigAccessor.getConfig().hasUndeadMode() ? 1 : 0, false);
//        serverLevel.getServer().execute(() -> {
//            BeUndeadApi.sendUndeadPacket((ServerPlayer)(Object)this);
//        });
    }
}
