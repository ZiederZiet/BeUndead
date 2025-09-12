package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.WorldCreationUiStateUndeadStateAccessor;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import io.github.ziederziet.beundead.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow public abstract WorldCreationUiState getUiState();

    @Inject(at = @At("HEAD"), method = "onCreate")
    private void onCreate(CallbackInfo info){
        ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);

        ModConfig config = holder.getConfig();

        config.undeadMode = ((WorldCreationUiStateUndeadStateAccessor)getUiState()).hasUndeadMode();

        holder.save();
    }
}
