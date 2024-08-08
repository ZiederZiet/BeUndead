package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Lists;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {
    @Shadow
    private Button exitToTitleButton;
    @Shadow
    public abstract void setButtonsActive(boolean pActive);
    @Inject(at = @At("HEAD"), method = "setButtonsActive(Z)V", cancellable = true)
    private void setButtonsActiveOverwrite(boolean pActive, CallbackInfo info) {
        if (BeUndead.getZombieType(Minecraft.getInstance().player) > 0 && BeUndead.getZombieRespawnTimer(Minecraft.getInstance().player) > 1){
            exitToTitleButton.active = pActive;
            info.cancel();
        }


    }
}
