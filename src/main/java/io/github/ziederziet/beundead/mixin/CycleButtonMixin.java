package io.github.ziederziet.beundead.mixin;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CycleButton.class)
public abstract class CycleButtonMixin {
    @Shadow protected abstract void cycleValue(int pDelta);

    @Inject(at = @At("TAIL"), method = "cycleValue")
    private void cycleValue(int pDelta, CallbackInfo info){
        if ((Object)this instanceof CycleButton<?> cycleButton) {
            Object value = cycleButton.getValue();
            if (value instanceof WorldCreationUiState.SelectedGameMode selectedGameMode) {
                if (selectedGameMode == WorldCreationUiState.SelectedGameMode.HARDCORE){
                    cycleValue(pDelta);
                }
            }
        }
    }
}
