package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class UndeadDataPacket implements FabricPacket {
    public static final ResourceLocation ID = new ResourceLocation(BeUndead.MODID, "undead_data");
    public static final PacketType<UndeadDataPacket> TYPE = PacketType.create(ID, UndeadDataPacket::new);

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

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeInt(playerId);
        friendlyByteBuf.writeUtf(type);
        friendlyByteBuf.writeBoolean(converting);
        friendlyByteBuf.writeBoolean(chest);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(LocalPlayer localPlayer, PacketSender packetSender){
        if (localPlayer.getLevel().getEntity(playerId) instanceof Player player){
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
}