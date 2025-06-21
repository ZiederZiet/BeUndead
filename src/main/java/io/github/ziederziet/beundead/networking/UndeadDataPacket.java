package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT && Minecraft.getInstance().level != null){
                if (Minecraft.getInstance().level.getEntity(playerId) instanceof Player player){
                    System.out.println("LOLL: " + type);

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

        System.out.println("LFISABHDGIKDSJBSDBGS: " + undeadAccessor.getType());

        return new UndeadDataPacket(player.getId(), undeadAccessor.getType(), undeadAccessor.getZombieConversionTime() > 0, undeadAccessor.hasZombieChest());
    }
}