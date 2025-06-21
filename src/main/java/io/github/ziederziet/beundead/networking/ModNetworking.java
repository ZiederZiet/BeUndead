package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register(){
        int id = 0;

        INSTANCE.registerMessage(id, ZombieSettingsPacket.class,
                ZombieSettingsPacket::encode,
                ZombieSettingsPacket::new,
                (packet, context) -> packet.handle(context));
//                .encoder(ZombieSettingsPacket::encode)
//                .decoder(ZombieSettingsPacket::new)
//                .consumerMainThread(ZombieSettingsPacket::handle)
        id++;

        INSTANCE.registerMessage(id, UndeadDataPacket.class,
                UndeadDataPacket::encode,
                UndeadDataPacket::new,
                (packet, context) -> packet.handle(context));
        id++;

        INSTANCE.registerMessage(id, RespawnTimerPacket.class,
                RespawnTimerPacket::encode,
                RespawnTimerPacket::new,
                (packet, context) -> packet.handle(context));
        id++;
    }

    public static void sendToServer(Object msg){
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToClient(Object msg, ServerPlayer serverPlayer){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }

    public static void sendToAllClients(Object msg){
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static void sendToAllTrackingAndSelfClients(Object msg, Entity toTrack){
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> toTrack), msg);
    }

    public static void sendToAllTrackingClients(Object msg, Entity toTrack){
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> toTrack), msg);
    }
}
