package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.client.ZombieChestModel;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

@Environment(EnvType.CLIENT)
public class BeUndeadClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ZombieChestModel.ZOMBIE_CHEST_LAYER_LOCATION, ZombieChestModel::createBodyLayer);

        ModNetworking.registerC2S();
    }
}
