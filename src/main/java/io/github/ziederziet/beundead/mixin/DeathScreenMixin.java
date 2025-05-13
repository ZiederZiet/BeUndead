package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Lists;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {
    @Shadow private final List<Button> exitButtons = Lists.newArrayList();

    @Inject(at = @At("HEAD"), method = "setButtonsActive(Z)V", cancellable = true)
    private void setButtonsActiveOverwrite(boolean pActive, CallbackInfo info) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && BeUndeadApi.getZombieRespawnTimer(player) > 0){
            long timeDif = BeUndeadApi.getZombieRespawnTimer(player) - player.level().getGameTime();
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
        if (BeUndeadApi.getZombieType(Minecraft.getInstance().player) > 0){
            this.exitToTitleScreen();
            info.cancel();
        }
    }
}