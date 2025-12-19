package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.client.ZombieChestModel;
import io.github.ziederziet.beundead.event.ModClientEvents;
import io.github.ziederziet.beundead.event.ModEvents;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.networking.RespawnTimerPacket;
import io.github.ziederziet.beundead.networking.UndeadDataPacket;
import io.github.ziederziet.beundead.networking.ZombieSettingsPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

@Environment(EnvType.CLIENT)
public class BeUndeadClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ZombieChestModel.ZOMBIE_CHEST_LAYER_LOCATION, ZombieChestModel::createBodyLayer);

        ModNetworking.registerC2S();

        ClientTickEvents.START_CLIENT_TICK.register(ModClientEvents::StartClientTick);

        ClientPlayConnectionEvents.DISCONNECT.register(ModClientEvents::ClientDisconnectEvent);

        ClientPlayNetworking.registerGlobalReceiver(ZombieSettingsPacket.TYPE, ZombieSettingsPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(UndeadDataPacket.TYPE, UndeadDataPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(RespawnTimerPacket.TYPE, RespawnTimerPacket::handle);
    }
}
