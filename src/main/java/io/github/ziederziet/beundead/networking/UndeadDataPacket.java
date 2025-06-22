package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class UndeadDataPacket {
    int playerId;
    int type;
    boolean converting;
    boolean chest;

    public UndeadDataPacket(int playerId, int type, boolean converting, boolean chest){
        this.playerId = playerId;
        this.type = type;
        this.converting = converting;
        this.chest = chest;
    }

    public UndeadDataPacket(FriendlyByteBuf buffer){
        playerId = buffer.readInt();
        type = buffer.readInt();
        converting = buffer.readBoolean();
        chest = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(playerId);
        buffer.writeInt(type);
        buffer.writeBoolean(converting);
        buffer.writeBoolean(chest);
    }

    public void handle(CustomPayloadEvent.Context context){
        context.enqueueWork(() -> {
            if (context.isClientSide()){
                if (Minecraft.getInstance().level.getEntity(playerId) instanceof Player player){
                    UndeadAccessor undeadAccessor = (UndeadAccessor) player;
                    undeadAccessor.setType(type);
                    undeadAccessor.setConverting(converting);
                    undeadAccessor.setZombieChest(chest);
                }
            }
            context.setPacketHandled(true);
        });
    }

    public static UndeadDataPacket getPacket(Player player){
        UndeadAccessor undeadAccessor = (UndeadAccessor) player;
        return new UndeadDataPacket(player.getId(), undeadAccessor.getType(), undeadAccessor.getZombieConversionTime() > 0, undeadAccessor.hasZombieChest());
    }
}