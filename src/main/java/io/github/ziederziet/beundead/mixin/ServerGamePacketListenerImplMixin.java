package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(at = @At("HEAD"), method = "handleClientCommand", cancellable = true)
    public void handleClientCommand(ServerboundClientCommandPacket pPacket, CallbackInfo info){
        ServerboundClientCommandPacket.Action serverboundclientcommandpacket$action = pPacket.getAction();
        if (serverboundclientcommandpacket$action == ServerboundClientCommandPacket.Action.PERFORM_RESPAWN){
            if (BeUndeadHelper.getZombieRespawnTimer(this.player) > this.player.getLevel().getGameTime() && this.player.getServer().isDedicatedServer() && !this.player.hasPermissions(2)){
                info.cancel();
            }
        }
    }
}