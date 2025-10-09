package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UndeadDataPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(BeUndead.MODID, "undead_data");
    public static final CustomPacketPayload.Type<UndeadDataPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, UndeadDataPacket> CODEC = StreamCodec.of((object, object2) -> object2.encode(object), UndeadDataPacket::new);

    int playerId;
    String type;
    boolean converting;
    boolean chest;

    public UndeadDataPacket(int playerId, String type, boolean converting, boolean chest){
        this.playerId = playerId;
        this.type = type;
        this.converting = converting;
        this.chest = chest;
    }

    public UndeadDataPacket(FriendlyByteBuf buffer){
        playerId = buffer.readInt();
        type = buffer.readUtf();
        converting = buffer.readBoolean();
        chest = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(playerId);
        buffer.writeUtf(type);
        buffer.writeBoolean(converting);
        buffer.writeBoolean(chest);
    }

    public void handle(ClientPlayNetworking.Context context){
        if (context.client().level.getEntity(playerId) instanceof Player player){
            UndeadAccessor undeadAccessor = (UndeadAccessor) player;
            undeadAccessor.setType(type);
            undeadAccessor.setConverting(converting);
            undeadAccessor.setZombieChest(chest);
        }
    }

    public static UndeadDataPacket getPacket(Player player){
        UndeadAccessor undeadAccessor = (UndeadAccessor) player;
        return new UndeadDataPacket(player.getId(), undeadAccessor.getType(), undeadAccessor.getZombieConversionTime() > 0, undeadAccessor.hasZombieChest());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}