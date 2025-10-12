package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.config.ServerConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$MoreTab")
public class CreateWorldScreenMoreTabMixin {
//    @Inject(at = @At("RETURN"), method = "<init>")
    @Inject(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
        shift = At.Shift.AFTER, ordinal = 2),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void init(CreateWorldScreen outer, CallbackInfo info, GridLayout.RowHelper rowHelper){
        rowHelper.addChild(Button.builder(Component.translatable("selectWorld.beUndeadConfig"), (button) -> {
            Minecraft.getInstance().setScreen(ServerConfigScreen.create(outer));
        }).width(210).build());
    }
}
