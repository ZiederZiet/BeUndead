package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.common.ClientUndeadType;
import io.github.ziederziet.beundead.common.AbstractServerUndeadType;
import io.github.ziederziet.beundead.common.UndeadType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

            SoundEvent stepSound = null;
            SoundEvent hurtSound = null;
            SoundEvent deathSound = null;
            SoundEvent ambientSound = null;

            Optional<Holder.Reference<SoundEvent>> stepSoundOptional = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(buffer.readUtf()));
            Optional<Holder.Reference<SoundEvent>> hurtSoundOptional = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(buffer.readUtf()));
            Optional<Holder.Reference<SoundEvent>> deathSoundOptional = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(buffer.readUtf()));
            Optional<Holder.Reference<SoundEvent>> ambientSoundOptional = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(buffer.readUtf()));
            if (stepSoundOptional.isPresent()){
                stepSound = stepSoundOptional.get().value();
            }
            if (hurtSoundOptional.isPresent()){
                hurtSound = hurtSoundOptional.get().value();
            }
            if (deathSoundOptional.isPresent()){
                deathSound = deathSoundOptional.get().value();
            }
            if (ambientSoundOptional.isPresent()){
                ambientSound = ambientSoundOptional.get().value();
            }

            String overlayTextureString = buffer.readUtf();
            ResourceLocation overlayTexture = overlayTextureString.isBlank() ? null : ResourceLocation.parse(overlayTextureString);

            undeadTypes.put(typeName, new ClientUndeadType(typeNameName, r, g, b, rOffset, gOffset, bOffset, canSwimInWater, breathUnderwater, burnsInTheSun, fireImmune, freezeImmune, stepSound, hurtSound, deathSound, ambientSound, overlayTexture));
        }
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(invState);
        buffer.writeBoolean(canChestExtension);
        buffer.writeBoolean(nightVision);
        buffer.writeBoolean(jumpOnTheirOwn);
        buffer.writeInt(zombieMaxViewDistance);
        buffer.writeDouble(zombieWalkingSpeed);
        buffer.writeFloat(zombieBreakingSpeed);
        buffer.writeBoolean(zombieSprintEnabled);

        buffer.writeInt(undeadTypes.size());

        undeadTypes.forEach((typeName, undeadType) -> {
            buffer.writeUtf(typeName);
            buffer.writeUtf(undeadType.name());
            buffer.writeFloat(undeadType.r());
            buffer.writeFloat(undeadType.g());
            buffer.writeFloat(undeadType.b());
            buffer.writeFloat(undeadType.rOffset());
            buffer.writeFloat(undeadType.gOffset());
            buffer.writeFloat(undeadType.bOffset());
            buffer.writeBoolean(undeadType.canSwimInWater());
            buffer.writeBoolean(undeadType.breathUnderwater());
            buffer.writeBoolean(undeadType.burnsInTheSun());
            buffer.writeBoolean(undeadType.fireImmune());
            buffer.writeBoolean(undeadType.freezeImmune());

            if (undeadType instanceof AbstractServerUndeadType serverUndeadType){
                if (serverUndeadType.stepSound() != null){
                    buffer.writeUtf(serverUndeadType.stepSound());
                }
                else{
                    buffer.writeUtf("");
                }
                if (serverUndeadType.hurtSound() != null){
                    buffer.writeUtf(serverUndeadType.hurtSound());
                }
                else{
                    buffer.writeUtf("");
                }
                if (serverUndeadType.deathSound() != null){
                    buffer.writeUtf(serverUndeadType.deathSound());
                }
                else{
                    buffer.writeUtf("");
                }
                if (serverUndeadType.ambientSound() != null){
                    buffer.writeUtf(serverUndeadType.ambientSound());
                }
                else{
                    buffer.writeUtf("");
                }
                if (serverUndeadType.overlayTexture() != null){
                    buffer.writeUtf(serverUndeadType.overlayTexture());
                }
                else{
                    buffer.writeUtf("");
                }
            }
        });
    }

    public void handle(ClientPlayNetworking.Context context){
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
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}