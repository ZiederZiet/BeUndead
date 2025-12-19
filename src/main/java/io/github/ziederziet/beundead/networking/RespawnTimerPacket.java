package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class RespawnTimerPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "respawn_timer");
    public static final CustomPacketPayload.Type<RespawnTimerPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, RespawnTimerPacket> CODEC = StreamCodec.of((object, object2) -> object2.encode(object), RespawnTimerPacket::new);

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
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player instanceof UndeadAccessor accessor){
                accessor.setZombieRespawnTimer(timer);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}