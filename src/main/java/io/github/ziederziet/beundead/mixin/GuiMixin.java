package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
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
    private static ResourceLocation ZOMBIE_ICONS = ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "textures/gui/undead_icons.png");
    private static final ResourceLocation WIDGETS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/widgets.png");
    private static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");

    @Shadow
    protected Minecraft minecraft;

    @Shadow protected int screenHeight;

    @Shadow protected int screenWidth;

    @Shadow public abstract Font getFont();

    @Shadow protected abstract Player getCameraPlayer();

    @Shadow protected abstract void renderSlot(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed);

    @Inject(at = @At("HEAD"), method = "renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V", cancellable = true)
    private void renderHotbar(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo info) {
        Player player = this.getCameraPlayer();
        if (player != null) {
            if(BeUndeadApi.getInvStateOfPlayer(player) == 0){
                info.cancel();

                ItemStack itemstack = player.getOffhandItem();
                HumanoidArm humanoidarm = player.getMainArm().getOpposite();
                int i = this.screenWidth / 2;
                pGuiGraphics.pose().pushPose();
                pGuiGraphics.pose().translate(0.0F, 0.0F, -90.0F);

                int additionXSlot = 80;

                pGuiGraphics.blit(WIDGETS_LOCATION, i - 98 + additionXSlot, this.screenHeight - 22, 53, 22, 29, 24);
                pGuiGraphics.blit(WIDGETS_LOCATION, i - 91 + additionXSlot - 1, this.screenHeight - 22 - 1, 0, 22, 24, 22);
                if (!itemstack.isEmpty()) {
                    if (humanoidarm == HumanoidArm.LEFT) {
                        pGuiGraphics.blit(WIDGETS_LOCATION, i - 91 + additionXSlot - 29, this.screenHeight - 23, 24, 22, 29, 24);
                    } else {
                        pGuiGraphics.blit(WIDGETS_LOCATION, i + 91 + additionXSlot, this.screenHeight - 23, 53, 22, 29, 24);
                    }
                }

                pGuiGraphics.pose().popPose();
                int l = 1;

                for(int i1 = 0; i1 < 9; ++i1) {
                    int j1 = i - 90 + i1 * 20 + 2 + additionXSlot;
                    int k1 = this.screenHeight - 16 - 3;
                    this.renderSlot(pGuiGraphics, j1, k1, pPartialTick, player, (ItemStack)player.getInventory().items.get(i1), l++);
                }

                if (!itemstack.isEmpty()) {
                    int i2 = this.screenHeight - 16 - 3;
                    if (humanoidarm == HumanoidArm.LEFT) {
                        this.renderSlot(pGuiGraphics, i - 91 + additionXSlot - 26, i2, pPartialTick, player, itemstack, l++);
                    } else {
                        this.renderSlot(pGuiGraphics, i + 91 + additionXSlot + 10, i2, pPartialTick, player, itemstack, l++);
                    }
                }

                RenderSystem.enableBlend();
                if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                    float f = this.minecraft.player.getAttackStrengthScale(0.0F);
                    if (f < 1.0F) {
                        int j2 = this.screenHeight - 20;
                        int k2 = i + 91 + 6;
                        if (humanoidarm == HumanoidArm.RIGHT) {
                            k2 = i - 91 - 22;
                        }

                        int l1 = (int)(f * 19.0F);
                        pGuiGraphics.blit(GUI_ICONS_LOCATION, k2, j2, 0, 94, 18, 18);
                        pGuiGraphics.blit(GUI_ICONS_LOCATION, k2, j2 + 18 - l1, 18, 112 - l1, 18, l1);
                    }
                }

                RenderSystem.disableBlend();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V", cancellable = true)
    private void renderExperienceBar(GuiGraphics pGuiGraphics, int pX, CallbackInfo info){
        Player player = this.getCameraPlayer();
        if (player != null) {
            if (BeUndeadApi.getZombieType(player) > 0){
                info.cancel();
                this.minecraft.getProfiler().push("expBar");
                int i = this.minecraft.player.getXpNeededForNextLevel();
                if (i > 0) {
                    int k = (int)(this.minecraft.player.experienceProgress * 183.0F);
                    int l = this.screenHeight - 32 + 3;
                    pGuiGraphics.blit(ZOMBIE_ICONS, pX, l, 0, 64, 182, 5);
                    if (k > 0) {
                        pGuiGraphics.blit(ZOMBIE_ICONS, pX, l, 0, 69, k, 5);
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
                    pGuiGraphics.drawString(this.getFont(), s, i1, j1, 16735264, false);
                    this.minecraft.getProfiler().pop();
                }
            }
        }
    }
}
