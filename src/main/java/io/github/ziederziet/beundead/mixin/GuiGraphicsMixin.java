package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    private static ResourceLocation DEFAULT_GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");
    private static ResourceLocation ZOMBIE_ICONS = ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "textures/gui/undead_icons.png");

    @Inject(at = @At("HEAD"), method = "blit(Lnet/minecraft/resources/ResourceLocation;IIIFFIIII)V", cancellable = true)
    private void blit(ResourceLocation pAtlasLocation, int pX, int pY, int pBlitOffset, float pUOffset, float pVOffset, int pUWidth, int pVHeight, int pTextureWidth, int pTextureHeight, CallbackInfo info){
        if (pAtlasLocation == DEFAULT_GUI_ICONS_LOCATION){
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null && BeUndeadApi.getZombieType(localPlayer) > 0){
                ((GuiGraphics)(Object)this).blit(ZOMBIE_ICONS, pX, pY, pBlitOffset, pUOffset, pVOffset, pUWidth, pVHeight, pTextureWidth, pTextureHeight);
                info.cancel();
            }
        }
    }
}