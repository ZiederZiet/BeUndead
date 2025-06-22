package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkDirection;

public class RespawnTimerPacket {
    private long timer;

    public RespawnTimerPacket(long timer){
        this.timer = timer;
    }

    public RespawnTimerPacket(FriendlyByteBuf buffer){
        timer = buffer.readLong();
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeLong(timer);
    }

    public void handle(CustomPayloadEvent.Context context){
        if (context.isClientSide()){
            context.enqueueWork(() -> {
                if (context.isClientSide()){
                    if (Minecraft.getInstance().player instanceof UndeadAccessor accessor){
                        accessor.setZombieRespawnTimer(timer);
                    }
                }
                context.setPacketHandled(true);
            });
        }
    }
}