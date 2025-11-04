package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.AbstractServerUndeadType;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.common.ClientUndeadType;
import io.github.ziederziet.beundead.common.UndeadType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;

public class ZombieSettingsPacket implements FabricPacket {
    public static final ResourceLocation ID = new ResourceLocation(BeUndead.MODID, "zombie_settings");
    public static final PacketType<ZombieSettingsPacket> TYPE = PacketType.create(ID, ZombieSettingsPacket::new);

    int invState;
    boolean canChestExtension;
    boolean nightVision;
    boolean jumpOnTheirOwn;
    int zombieMaxViewDistance;
    double zombieWalkingSpeed;
    float zombieBreakingSpeed;
    boolean zombieSprintEnabled;
    Map<String, UndeadType> undeadTypes = new HashMap<>();

    public ZombieSettingsPacket(int invState, boolean canChestExtension, boolean nightVision, boolean jumpOnTheirOwn, int zombieMaxViewDistance, double zombieWalkingSpeed, float zombieBreakingSpeed, boolean zombieSprintEnabled, Map<String, UndeadType> undeadTypes){
        this.invState = invState;
        this.canChestExtension = canChestExtension;
        this.nightVision = nightVision;
        this.jumpOnTheirOwn = jumpOnTheirOwn;
        this.zombieMaxViewDistance = zombieMaxViewDistance;
        this.zombieWalkingSpeed = zombieWalkingSpeed;
        this.zombieBreakingSpeed = zombieBreakingSpeed;
        this.zombieSprintEnabled = zombieSprintEnabled;
        this.undeadTypes = undeadTypes;
    }

    public ZombieSettingsPacket(FriendlyByteBuf buffer){
        invState = buffer.readInt();
        canChestExtension = buffer.readBoolean();
        nightVision = buffer.readBoolean();
        jumpOnTheirOwn = buffer.readBoolean();
        zombieMaxViewDistance = buffer.readInt();
        zombieWalkingSpeed = buffer.readDouble();
        zombieBreakingSpeed = buffer.readFloat();
        zombieSprintEnabled = buffer.readBoolean();

        int mapSize = buffer.readInt();
        for (int i = 0; i < mapSize; i++) {
            String typeName = buffer.readUtf();
            String typeNameName = buffer.readUtf();
            float r = buffer.readFloat();
            float g = buffer.readFloat();
            float b = buffer.readFloat();
            float rOffset = buffer.readFloat();
            float gOffset = buffer.readFloat();
            float bOffset = buffer.readFloat();
            boolean canSwimInWater = buffer.readBoolean();
            boolean breathUnderwater = buffer.readBoolean();
            boolean burnsInTheSun = buffer.readBoolean();
            boolean fireImmune = buffer.readBoolean();
            boolean freezeImmune = buffer.readBoolean();

            SoundEvent stepSound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(buffer.readUtf()));
            SoundEvent hurtSound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(buffer.readUtf()));
            SoundEvent deathSound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(buffer.readUtf()));
            SoundEvent ambientSound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(buffer.readUtf()));

            String overlayTextureString = buffer.readUtf();
            ResourceLocation overlayTexture = overlayTextureString.isBlank() ? null : new ResourceLocation(overlayTextureString);

            undeadTypes.put(typeName, new ClientUndeadType(typeNameName, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune, stepSound, hurtSound, deathSound, ambientSound, overlayTexture));
        }
    }

    public void handle(LocalPlayer localPlayer, PacketSender packetSender){
        ClientInfo.zombieInvState = invState;
        ClientInfo.canChestExtension = canChestExtension;
        ClientInfo.zombieNightVision = nightVision;
        ClientInfo.zombieJumpOnTheirOwn = jumpOnTheirOwn;
        ClientInfo.zombieMaxViewDistance = zombieMaxViewDistance;
        ClientInfo.zombieWalkingSpeed = zombieWalkingSpeed;
        ClientInfo.zombieBreakingSpeed = zombieBreakingSpeed;
        ClientInfo.zombieSprintEnabled = zombieSprintEnabled;
        ClientInfo.undeadTypes = undeadTypes;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(invState);
        friendlyByteBuf.writeBoolean(canChestExtension);
        friendlyByteBuf.writeBoolean(nightVision);
        friendlyByteBuf.writeBoolean(jumpOnTheirOwn);
        friendlyByteBuf.writeInt(zombieMaxViewDistance);
        friendlyByteBuf.writeDouble(zombieWalkingSpeed);
        friendlyByteBuf.writeFloat(zombieBreakingSpeed);
        friendlyByteBuf.writeBoolean(zombieSprintEnabled);

        friendlyByteBuf.writeInt(undeadTypes.size());

        undeadTypes.forEach((typeName, undeadType) -> {
            friendlyByteBuf.writeUtf(typeName);
            friendlyByteBuf.writeUtf(undeadType.name());
            friendlyByteBuf.writeFloat(undeadType.r());
            friendlyByteBuf.writeFloat(undeadType.g());
            friendlyByteBuf.writeFloat(undeadType.b());
            friendlyByteBuf.writeFloat(undeadType.rOffset());
            friendlyByteBuf.writeFloat(undeadType.gOffset());
            friendlyByteBuf.writeFloat(undeadType.bOffset());
            friendlyByteBuf.writeBoolean(undeadType.canSwimInWater());
            friendlyByteBuf.writeBoolean(undeadType.breathUnderwater());
            friendlyByteBuf.writeBoolean(undeadType.burnsInTheSun());
            friendlyByteBuf.writeBoolean(undeadType.fireImmune());
            friendlyByteBuf.writeBoolean(undeadType.freezeImmune());

            if (undeadType instanceof AbstractServerUndeadType serverUndeadType){
                if (serverUndeadType.stepSound() != null){
                    friendlyByteBuf.writeUtf(serverUndeadType.stepSound());
                }
                else{
                    friendlyByteBuf.writeUtf("");
                }
                if (serverUndeadType.hurtSound() != null){
                    friendlyByteBuf.writeUtf(serverUndeadType.hurtSound());
                }
                else{
                    friendlyByteBuf.writeUtf("");
                }
                if (serverUndeadType.deathSound() != null){
                    friendlyByteBuf.writeUtf(serverUndeadType.deathSound());
                }
                else{
                    friendlyByteBuf.writeUtf("");
                }
                if (serverUndeadType.ambientSound() != null){
                    friendlyByteBuf.writeUtf(serverUndeadType.ambientSound());
                }
                else{
                    friendlyByteBuf.writeUtf("");
                }
                if (serverUndeadType.overlayTexture() != null){
                    friendlyByteBuf.writeUtf(serverUndeadType.overlayTexture());
                }
                else{
                    friendlyByteBuf.writeUtf("");
                }
            }
        });
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
