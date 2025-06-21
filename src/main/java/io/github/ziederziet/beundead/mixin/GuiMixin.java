package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
    private static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");

    @Shadow
    protected Minecraft minecraft;

    @Shadow protected int screenHeight;

    @Shadow protected int screenWidth;

    @Shadow public abstract Font getFont();

    @Inject(at = @At("HEAD"), method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V", cancellable = true)
    private void renderHotbar(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo info) {

    }

    @Inject(at = @At("HEAD"), method = "renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V", cancellable = true)
    private void renderExperienceBar(GuiGraphics pGuiGraphics, int pX, CallbackInfo info){
        this.minecraft.getProfiler().push("expBar");
        int i = this.minecraft.player.getXpNeededForNextLevel();
        if (i > 0) {
            int j = 182;
            int k = (int)(this.minecraft.player.experienceProgress * 183.0F);
            int l = this.screenHeight - 32 + 3;
            pGuiGraphics.blit(GUI_ICONS_LOCATION, pX, l, 0, 64, 182, 5);
            if (k > 0) {
                pGuiGraphics.blit(GUI_ICONS_LOCATION, pX, l, 0, 69, k, 5);
            }
        }

        this.minecraft.getProfiler().pop();
        if (this.minecraft.player.experienceLevel > 0) {
            this.minecraft.getProfiler().push("expLevel");
            String s = "" + this.minecraft.player.experienceLevel;
            int i1 = (this.screenWidth - this.getFont().width(s)) / 2;
            int j1 = this.screenHeight - 31 - 4;
            pGuiGraphics.drawString(this.getFont(), s, i1 + 1, j1, 0, false);
            pGuiGraphics.drawString(this.getFont(), s, i1 - 1, j1, 0, false);
            pGuiGraphics.drawString(this.getFont(), s, i1, j1 + 1, 0, false);
            pGuiGraphics.drawString(this.getFont(), s, i1, j1 - 1, 0, false);
            pGuiGraphics.drawString(this.getFont(), s, i1, j1, 8453920, false);
            this.minecraft.getProfiler().pop();
        }
    }
}
