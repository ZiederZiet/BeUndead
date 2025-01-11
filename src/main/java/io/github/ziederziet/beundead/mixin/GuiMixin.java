package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_background");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_progress");

    private static final ResourceLocation ZOMBIE_EXPERIENCE_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/zombie_experience_bar_progress");
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/experience_bar_background");


    @Shadow
    protected abstract void renderSlot(GuiGraphics pGuiGraphics, int pX, int pY, DeltaTracker pDeltaTracker, Player pPlayer, ItemStack pStack, int pSeed);

    @Inject(at = @At("HEAD"), method = "renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", cancellable = true)
    private void renderItemHotbar(GuiGraphics pGuiGraphics, DeltaTracker pDeltaTracker, CallbackInfo info) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player && BeUndead.getZombieType(player) > 0 && BeUndead.getInvStateOfPlayer(player) == 0) {
            info.cancel();
            ItemStack offhandItemstack = player.getOffhandItem();
            HumanoidArm humanoidarm = player.getMainArm().getOpposite();
            int i = pGuiGraphics.guiWidth() / 2;
            RenderSystem.enableBlend();
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, -90.0F);

            int additionXSlot = 80;

            pGuiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, i - 98 + additionXSlot, pGuiGraphics.guiHeight() - 22, 29, 22);
            pGuiGraphics.blitSprite(HOTBAR_SELECTION_SPRITE, i - 91 + additionXSlot - 1, pGuiGraphics.guiHeight() - 22 - 1, 24, 23);

            if (!offhandItemstack.isEmpty()) {
                if (humanoidarm == HumanoidArm.LEFT) {
                    pGuiGraphics.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, i - 91 + additionXSlot - 29, pGuiGraphics.guiHeight() - 23, 29, 24);
                } else {
                    pGuiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, i + 91 + additionXSlot, pGuiGraphics.guiHeight() - 23, 29, 24);
                }
            }

            pGuiGraphics.pose().popPose();
            RenderSystem.disableBlend();
            int l = 1;
            int i2;
            int j2;
            int k2;


            i2 = 4;
            j2 = i - 90 + 2 + additionXSlot;
            k2 = pGuiGraphics.guiHeight() - 16 - 3;
            this.renderSlot(pGuiGraphics, j2, k2, pDeltaTracker, player, (ItemStack)player.getInventory().items.get(i2), l++);


            if (!offhandItemstack.isEmpty()) {
                i2 = pGuiGraphics.guiHeight() - 16 - 3;
                if (humanoidarm == HumanoidArm.LEFT) {
                    this.renderSlot(pGuiGraphics, i - 91 + additionXSlot - 26, i2, pDeltaTracker, player, offhandItemstack, l++);
                } else {
                    this.renderSlot(pGuiGraphics, i + 91 + additionXSlot + 10, i2, pDeltaTracker, player, offhandItemstack, l++);
                }
            }

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                RenderSystem.enableBlend();
                float f = player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    j2 = pGuiGraphics.guiHeight() - 20;
                    k2 = i + 91 + 6;
                    if (humanoidarm == HumanoidArm.RIGHT) {
                        k2 = i - 91 - 22;
                    }

                    int l1 = (int)(f * 19.0F);
                    pGuiGraphics.blitSprite(HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                    pGuiGraphics.blitSprite(HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
                }

                RenderSystem.disableBlend();
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V", cancellable = true)
    private void renderExperienceBar(GuiGraphics pGuiGraphics, int pX, CallbackInfo info){
        if (BeUndead.getZombieType(Minecraft.getInstance().player) > 0){
            Minecraft.getInstance().getProfiler().push("expBar");
            int i = Minecraft.getInstance().player.getXpNeededForNextLevel();
            if (i > 0) {
                int k = (int)(Minecraft.getInstance().player.experienceProgress * 183.0F);
                int l = pGuiGraphics.guiHeight() - 32 + 3;
                RenderSystem.enableBlend();
                pGuiGraphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, pX, l, 182, 5);
                if (k > 0) {
                    pGuiGraphics.blitSprite(ZOMBIE_EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, pX, l, k, 5);
                }

                RenderSystem.disableBlend();
            }

            Minecraft.getInstance().getProfiler().pop();
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderExperienceLevel(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", cancellable = true)
    private void renderExperienceLevel(GuiGraphics p_335340_, DeltaTracker p_344840_, CallbackInfo info){
        if (BeUndead.getZombieType(Minecraft.getInstance().player) > 0){
            int i = Minecraft.getInstance().player.experienceLevel;
            if (this.isExperienceBarVisible() && i > 0) {
                Minecraft.getInstance().getProfiler().push("expLevel");
                String s = "" + i;
                int j = (p_335340_.guiWidth() - Minecraft.getInstance().font.width(s)) / 2;
                int k = p_335340_.guiHeight() - 31 - 4;
                p_335340_.drawString(Minecraft.getInstance().font, s, j + 1, k, 0, false);
                p_335340_.drawString(Minecraft.getInstance().font, s, j - 1, k, 0, false);
                p_335340_.drawString(Minecraft.getInstance().font, s, j, k + 1, 0, false);
                p_335340_.drawString(Minecraft.getInstance().font, s, j, k - 1, 0, false);
                p_335340_.drawString(Minecraft.getInstance().font, s, j, k, 16735264, false);
                Minecraft.getInstance().getProfiler().pop();
            }
            info.cancel();
        }
    }

    @Shadow
    protected abstract boolean isExperienceBarVisible();
}
