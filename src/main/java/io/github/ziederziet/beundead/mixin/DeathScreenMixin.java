package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Lists;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
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
    @Shadow
    private Button exitToTitleButton;
    @Shadow
    private final List<Button> exitButtons = Lists.newArrayList();
    @Shadow
    public abstract void setButtonsActive(boolean pActive);
    @Inject(at = @At("HEAD"), method = "setButtonsActive(Z)V", cancellable = true)
    private void setButtonsActiveOverwrite(boolean pActive, CallbackInfo info) {
        if (BeUndead.getZombieType(Minecraft.getInstance().player) > 0 && BeUndead.getZombieRespawnTimer(Minecraft.getInstance().player) > 1){
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
