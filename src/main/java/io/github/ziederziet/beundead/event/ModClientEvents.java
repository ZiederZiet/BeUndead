package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.mixin.DeathScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class ModClientEvents {
    public static void ClientEntityRemoval(Entity entity){
        if (entity instanceof AbstractClientPlayer abstractClientPlayer){
            UndeadSkinManager.removeSkin(abstractClientPlayer.getSkinTextureLocation());
        }
    }
    public static boolean isCreativeScreen(){
        return Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen creativeModeInventoryScreen && creativeModeInventoryScreen.isInventoryOpen();
    }
    public static void StartClientTick(Minecraft minecraft){
        LocalPlayer player = minecraft.player;
        if (player != null && player.level().isClientSide()){
            if (minecraft.screen instanceof DeathScreen deathScreen){
                long respawnTimer = BeUndeadHelper.getZombieRespawnTimer(player);
                long timeTo = respawnTimer - player.level().getGameTime();
                if (timeTo < 2){
                    Button button = ((DeathScreenAccessor)deathScreen).getExitButtons().get(0);
                    button.active = true;
                    button.setMessage(Component.translatable("deathScreen.respawn"));
                } else if (timeTo % 20 == 0){
                    int minutes = (int)Math.floor(timeTo / 20D / 60D);
                    int seconds = (int)Math.floor(timeTo / 20D % 60D);
                    ((DeathScreenAccessor)deathScreen).getExitButtons().get(0).setMessage(Component.translatable("deathScreen.respawn").append(" " + minutes + ":" + (String.valueOf(seconds).length() == 1 ? "0" : "") + seconds));
                }
            }
        }
    }

    public static void ClientDisconnectEvent(ClientPacketListener clientPacketListener, Minecraft minecraft){
        UndeadSkinManager.removeAll();
    }
}