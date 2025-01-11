package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public void handle(CustomPayloadEvent.Context context){
        if (context.isClientSide()){
            BeUndead.Mod.clientZombieInvState = invState;
            BeUndead.Mod.clientCanChestExtension = canChestExtension;
            BeUndead.Mod.clientZombieNightVision = nightVision;
            BeUndead.Mod.clientZombieJumpOnTheirOwn = jumpOnTheirOwn;
            BeUndead.Mod.clientZombieMaxViewDistance = zombieMaxViewDistance;
            BeUndead.Mod.clientZombieWalkingSpeed = zombieWalkingSpeed;
        }
    }
}
