package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.WorldCreationUiStateUndeadStateAccessor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldCreationUiState.class)
public class WorldCreationUiStateMixin implements WorldCreationUiStateUndeadStateAccessor {
    private boolean undeadMode;
    @Override
    public void setUndeadMode(boolean on) {
        undeadMode = on;
    }

    @Override
    public boolean hasUndeadMode() {
        return undeadMode;
    }
}
