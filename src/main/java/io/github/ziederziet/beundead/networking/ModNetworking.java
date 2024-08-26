package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        INSTANCE.messageBuilder(ClientGetFogAndRedMoonPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientGetFogAndRedMoonPacket::encode)
                .decoder(ClientGetFogAndRedMoonPacket::new)
                .consumerMainThread(ClientGetFogAndRedMoonPacket::handle)
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
}
