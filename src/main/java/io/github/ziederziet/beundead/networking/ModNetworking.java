package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {
    public static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation CHANNEL_ID =
            ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "main");

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(
                UndeadDataPacket.TYPE,
                UndeadDataPacket.CODEC,
                UndeadDataPacket::handle
        );

        registrar.playToClient(
                RespawnTimerPacket.TYPE,
                RespawnTimerPacket.CODEC,
                RespawnTimerPacket::handle
        );

        registrar.playToClient(
                ZombieSettingsPacket.TYPE,
                ZombieSettingsPacket.CODEC,
                ZombieSettingsPacket::handle
        );
    }


    public static void sendToServer(CustomPacketPayload msg){
        PacketDistributor.sendToServer(msg);
    }

    public static void sendToClient(CustomPacketPayload msg, ServerPlayer serverPlayer){
        PacketDistributor.sendToPlayer(serverPlayer, msg);
    }

    public static void sendToAllClients(CustomPacketPayload msg){
        PacketDistributor.sendToAllPlayers(msg);
    }

    public static void sendToAllTrackingAndSelfClients(CustomPacketPayload msg, ServerPlayer toTrack){
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(toTrack, msg);
    }

    public static void sendToAllTrackingClients(CustomPacketPayload msg, Entity toTrack){
        PacketDistributor.sendToPlayersTrackingEntity(toTrack, msg);
    }
}
