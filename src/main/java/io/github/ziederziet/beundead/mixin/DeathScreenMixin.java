package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Lists;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {
    @Shadow private final List<Button> exitButtons = Lists.newArrayList();

    @Inject(at = @At("HEAD"), method = "setButtonsActive(Z)V", cancellable = true)
    private void setButtonsActiveOverwrite(boolean pActive, CallbackInfo info) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && BeUndeadHelper.getZombieRespawnTimer(player) > 0){
            long timeDif = BeUndeadHelper.getZombieRespawnTimer(player) - player.level().getGameTime();
            if (timeDif > 0 && !player.hasPermissions(2)) {
                Button $$1;
                for(Iterator var2 = this.exitButtons.iterator(); var2.hasNext();) {
                    $$1 = (Button)var2.next();
                    $$1.active = pActive;
                    if ($$1.getMessage() instanceof MutableComponent mutableComponent && mutableComponent.getContents() instanceof TranslatableContents translatableContents && translatableContents.getKey().equals("deathScreen.respawn")){
                        $$1.active = false;
                    }
                }
                info.cancel();
            }
        }
    }

    @Shadow
    protected abstract void exitToTitleScreen();

    @Inject(at = @At("HEAD"), method = "handleExitToTitleScreen()V", cancellable = true)
    private void handleExitToTitleScreen(CallbackInfo info) {
        if (!BeUndeadHelper.isHuman(Minecraft.getInstance().player)){
            this.exitToTitleScreen();
            info.cancel();
        }
    }
}