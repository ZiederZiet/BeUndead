package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

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
        }
    }
}
