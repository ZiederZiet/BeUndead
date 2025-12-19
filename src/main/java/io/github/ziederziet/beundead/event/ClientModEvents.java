package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.client.ZombieChestLayer;
import io.github.ziederziet.beundead.client.ZombieChestModel;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.mixin.DeathScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = BeUndead.MODID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                ZombieChestModel.ZOMBIE_CHEST_LAYER_LOCATION,
                ZombieChestModel::createBodyLayer
        );
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {

        @Nullable EntityRenderer defaultRenderer = event.getRenderer(EntityType.PLAYER);

        if (defaultRenderer instanceof PlayerRenderer playerRenderer) {
            playerRenderer.addLayer(
                    new ZombieChestLayer(
                            playerRenderer,
                            event.getEntityModels()
                    )
            );
        }
    }

    @SubscribeEvent
    public static void StartClientTick(ClientTickEvent.Pre event){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player != null && player.level().isClientSide()){
            if (minecraft.screen instanceof DeathScreen deathScreen){
                long respawnTimer = BeUndeadHelper.getZombieRespawnTimer(player);
                long timeTo = respawnTimer - player.level().getGameTime();
                if (timeTo < 2){
                    Button button = ((DeathScreenAccessor)deathScreen).getExitButtons().getFirst();
                    button.active = true;
                    button.setMessage(Component.translatable("deathScreen.respawn"));
                } else if (timeTo % 20 == 0){
                    int minutes = (int)Math.floor(timeTo / 20D / 60D);
                    int seconds = (int)Math.floor(timeTo / 20D % 60D);
                    ((DeathScreenAccessor)deathScreen).getExitButtons().getFirst().setMessage(Component.translatable("deathScreen.respawn").append(" " + minutes + ":" + (String.valueOf(seconds).length() == 1 ? "0" : "") + seconds));
                }
            }
        }
    }

    @SubscribeEvent
    public static void ClientDisconnectEvent(ClientPlayerNetworkEvent.LoggingOut event){
        UndeadSkinManager.removeAll();
    }

    @SubscribeEvent
    public static void ClientLevelRemoval(EntityLeaveLevelEvent event){
        if (event.getEntity() instanceof AbstractClientPlayer player){
            UndeadSkinManager.removeSkin(player.getSkin().texture());
        }
    }
}
