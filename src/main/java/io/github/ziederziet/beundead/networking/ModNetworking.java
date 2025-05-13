package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ModNetworking {
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "main"))
            .serverAcceptedVersions((server, version) -> true)
            .clientAcceptedVersions((server, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register(){
        INSTANCE.messageBuilder(ZombieSettingsPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ZombieSettingsPacket::encode)
                .decoder(ZombieSettingsPacket::new)
                .consumerMainThread(ZombieSettingsPacket::handle)
                .add();

        INSTANCE.messageBuilder(UndeadDataPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UndeadDataPacket::encode)
                .decoder(UndeadDataPacket::new)
                .consumerMainThread(UndeadDataPacket::handle)
                .add();

        INSTANCE.messageBuilder(RespawnTimerPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RespawnTimerPacket::encode)
                .decoder(RespawnTimerPacket::new)
                .consumerMainThread(RespawnTimerPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg){
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static void sendToClient(Object msg, ServerPlayer serverPlayer){
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(serverPlayer));
    }

    public static void sendToAllClients(Object msg){
        INSTANCE.send(msg, PacketDistributor.ALL.noArg());
    }

    public static void sendToAllTrackingAndSelfClients(Object msg, Entity toTrack){
        INSTANCE.send(msg, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(toTrack));
    }

    public static void sendToAllTrackingClients(Object msg, Entity toTrack){
        INSTANCE.send(msg, PacketDistributor.TRACKING_ENTITY.with(toTrack));
    }
}
