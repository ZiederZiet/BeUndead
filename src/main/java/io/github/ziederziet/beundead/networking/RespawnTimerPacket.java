package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class RespawnTimerPacket implements FabricPacket {
    public static final ResourceLocation ID = new ResourceLocation(BeUndead.MODID, "respawn_timer");
    public static final PacketType<RespawnTimerPacket> TYPE = PacketType.create(ID, RespawnTimerPacket::new);

    private long timer;

    public RespawnTimerPacket(long timer){
        this.timer = timer;
    }

    public RespawnTimerPacket(FriendlyByteBuf buffer){
        timer = buffer.readLong();
    }

    public void write(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeLong(timer);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(LocalPlayer localPlayer, PacketSender packetSender){
        if (localPlayer instanceof UndeadAccessor accessor){
            accessor.setZombieRespawnTimer(timer);
        }
    }
}