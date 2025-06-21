package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.common.ClientInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ZombieSettingsPacket {
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

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT){
                ClientInfo.zombieInvState = invState;
                ClientInfo.canChestExtension = canChestExtension;
                ClientInfo.zombieNightVision = nightVision;
                ClientInfo.zombieJumpOnTheirOwn = jumpOnTheirOwn;
                ClientInfo.zombieMaxViewDistance = zombieMaxViewDistance;
                ClientInfo.zombieWalkingSpeed = zombieWalkingSpeed;
            }
            context.setPacketHandled(true);
        });
    }
}
