package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.ClientInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ZombieSettingsPacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "zombie_settings");
    public static final Type<ZombieSettingsPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ZombieSettingsPacket> CODEC = StreamCodec.of((object, object2) -> object2.encode(object), ZombieSettingsPacket::new);

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

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(invState);
        buffer.writeBoolean(canChestExtension);
        buffer.writeBoolean(nightVision);
        buffer.writeBoolean(jumpOnTheirOwn);
        buffer.writeInt(zombieMaxViewDistance);
        buffer.writeDouble(zombieWalkingSpeed);
    }

    public void handle(ClientPlayNetworking.Context context){
        ClientInfo.zombieInvState = invState;
        ClientInfo.canChestExtension = canChestExtension;
        ClientInfo.zombieNightVision = nightVision;
        ClientInfo.zombieJumpOnTheirOwn = jumpOnTheirOwn;
        ClientInfo.zombieMaxViewDistance = zombieMaxViewDistance;
        ClientInfo.zombieWalkingSpeed = zombieWalkingSpeed;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
