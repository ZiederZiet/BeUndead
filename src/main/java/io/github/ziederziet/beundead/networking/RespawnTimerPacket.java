package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT){
                if (Minecraft.getInstance().player instanceof UndeadAccessor accessor){
                    accessor.setZombieRespawnTimer(timer);
                }
            }
            context.setPacketHandled(true);
        });
    }
}