package io.github.ziederziet.beundead.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class ModNetworking {
    public static void sendToServer(FabricPacket msg){
        ClientPlayNetworking.send(msg);
    }

    public static void sendToClient(FabricPacket msg, ServerPlayer serverPlayer){
        ServerPlayNetworking.send(serverPlayer, msg);
    }

    public static void sendToAllClients(MinecraftServer server, FabricPacket msg){
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, msg);
        }
    }

    public static void sendToAllTrackingAndSelfClients(FabricPacket msg, ServerPlayer toTrack){
        for (ServerPlayer player : PlayerLookup.tracking(toTrack)){
            ServerPlayNetworking.send(player, msg);
        }
        ServerPlayNetworking.send(toTrack, msg);
    }

    public static void sendToAllTrackingClients(FabricPacket msg, Entity toTrack){
        for (ServerPlayer player : PlayerLookup.tracking(toTrack)){
            ServerPlayNetworking.send(player, msg);
        }
    }

    public static void registerC2S() {
    }

    public static void registerS2C() {
        //PayloadTypeRegistry.playS2C().register(UndeadDataPacket.TYPE, UndeadDataPacket.)
//        PayloadTypeRegistry.playS2C().register(ZombieSettingsPacket.TYPE, ZombieSettingsPacket.CODEC);
//        PayloadTypeRegistry.playS2C().register(UndeadDataPacket.TYPE, UndeadDataPacket.CODEC);
//        PayloadTypeRegistry.playS2C().register(RespawnTimerPacket.TYPE, RespawnTimerPacket.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ZombieSettingsPacket.TYPE, ZombieSettingsPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(UndeadDataPacket.TYPE, UndeadDataPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(RespawnTimerPacket.TYPE, RespawnTimerPacket::handle);
    }
}
