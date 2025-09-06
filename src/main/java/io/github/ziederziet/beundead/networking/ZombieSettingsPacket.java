package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.ClientInfo;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ZombieSettingsPacket implements FabricPacket {
    public static final ResourceLocation ID = new ResourceLocation(BeUndead.MODID, "zombie_settings");
    public static final PacketType<ZombieSettingsPacket> TYPE = PacketType.create(ID, ZombieSettingsPacket::new);

    int invState;
    boolean canChestExtension;
    boolean nightVision;
    boolean jumpOnTheirOwn;
    int zombieMaxViewDistance;
    double zombieWalkingSpeed;
    public ZombieSettingsPacket(int invState, boolean canChestExtension, boolean nightVision, boolean jumpOnTheirOwn, int zombieMaxViewDistance, double zombieWalkingSpeed){
        this.invState = invState;
        this.canChestExtension = canChestExtension;
        this.nightVision = nightVision;
        this.jumpOnTheirOwn = jumpOnTheirOwn;
        this.zombieMaxViewDistance = zombieMaxViewDistance;
        this.zombieWalkingSpeed = zombieWalkingSpeed;
    }

    public ZombieSettingsPacket(FriendlyByteBuf buffer){
        invState = buffer.readInt();
        canChestExtension = buffer.readBoolean();
        nightVision = buffer.readBoolean();
        jumpOnTheirOwn = buffer.readBoolean();
        zombieMaxViewDistance = buffer.readInt();
        zombieWalkingSpeed = buffer.readDouble();
    }

    public void handle(LocalPlayer localPlayer, PacketSender packetSender){
        ClientInfo.zombieInvState = invState;
        ClientInfo.canChestExtension = canChestExtension;
        ClientInfo.zombieNightVision = nightVision;
        ClientInfo.zombieJumpOnTheirOwn = jumpOnTheirOwn;
        ClientInfo.zombieMaxViewDistance = zombieMaxViewDistance;
        ClientInfo.zombieWalkingSpeed = zombieWalkingSpeed;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(invState);
        friendlyByteBuf.writeBoolean(canChestExtension);
        friendlyByteBuf.writeBoolean(nightVision);
        friendlyByteBuf.writeBoolean(jumpOnTheirOwn);
        friendlyByteBuf.writeInt(zombieMaxViewDistance);
        friendlyByteBuf.writeDouble(zombieWalkingSpeed);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
