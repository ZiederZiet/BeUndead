package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.mixin.DeathScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BeUndead.MODID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onLevelTickEvent(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide() && Minecraft.getInstance().player != null){
            LocalPlayer player = Minecraft.getInstance().player;
            if (player.level().isClientSide()){
                if (Minecraft.getInstance().screen instanceof DeathScreen deathScreen){
                    long respawnTimer = BeUndeadApi.getZombieRespawnTimer(player);
                    long timeTo = respawnTimer - player.level().getGameTime();
                    if (timeTo == 1){
                        ((DeathScreenAccessor)deathScreen).getExitButtons().getFirst().active = true;
                        ((DeathScreenAccessor)deathScreen).getExitButtons().getFirst().setMessage(Component.translatable("deathScreen.respawn"));
                    } else if (timeTo > 0 && timeTo % 20 == 0){
                        int minutes = (int)Math.floor(timeTo / 20D / 60D);
                        int seconds = (int)Math.floor(timeTo / 20D % 60D);
                        ((DeathScreenAccessor)deathScreen).getExitButtons().getFirst().setMessage(Component.translatable("deathScreen.respawn").append(" " + minutes + ":" + (String.valueOf(seconds).length() == 1 ? "0" : "") + seconds));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        UndeadSkinManager.removeAll();
    }
}