package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.WorldCreationUiStateUndeadStateAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CycleButton.class)
public abstract class CycleButtonMixin<T> {
    @Shadow protected abstract void cycleValue(int pDelta);

    @Inject(at = @At("TAIL"), method = "cycleValue")
    private void cycleValue(int pDelta, CallbackInfo info){
        if ((Object)this instanceof CycleButton<?> cycleButton) {
            Object value = cycleButton.getValue();
            if (value instanceof WorldCreationUiState.SelectedGameMode selectedGameMode) {
                if (selectedGameMode == WorldCreationUiState.SelectedGameMode.HARDCORE){
                    if (Minecraft.getInstance().screen instanceof CreateWorldScreen screen){
                        WorldCreationUiStateUndeadStateAccessor accessor = (WorldCreationUiStateUndeadStateAccessor)screen.getUiState();
                        if (accessor.hasUndeadMode()){
                            accessor.setUndeadMode(false);
                            cycleValue(pDelta);
                        }
                        else {
                            screen.getUiState().setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
                            accessor.setUndeadMode(true);
                            cycleButton.setMessage(Component.translatable("selectWorld.gameMode.undead"));
                            cycleButton.setTooltip(Tooltip.create(Component.translatable("selectWorld.gameMode.undead.info")));
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "createFullName", cancellable = true)
    private void createFullName(T object, CallbackInfoReturnable<MutableComponent> info){
        if (object instanceof WorldCreationUiState.SelectedGameMode && Minecraft.getInstance().screen instanceof CreateWorldScreen screen) {
            WorldCreationUiStateUndeadStateAccessor accessor = (WorldCreationUiStateUndeadStateAccessor) screen.getUiState();
            if (accessor.hasUndeadMode()) {
                info.setReturnValue(Component.translatable("selectWorld.gameMode.undead"));
            }
        }
    }
}
