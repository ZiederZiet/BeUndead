package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow protected abstract void renderSlot(GuiGraphics guiGraphics, int i, int j, float f, Player player, ItemStack itemStack, int k);

    @Shadow public abstract Font getFont();

    @Shadow private int screenWidth;
    @Shadow private int screenHeight;
    @Shadow @Final private Minecraft minecraft;
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = new ResourceLocation("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = new ResourceLocation("hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = new ResourceLocation("hud/hotbar_offhand_right");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = new ResourceLocation("hud/hotbar_attack_indicator_background");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = new ResourceLocation("hud/hotbar_attack_indicator_progress");

    private static final ResourceLocation ZOMBIE_EXPERIENCE_BAR_PROGRESS_SPRITE = new ResourceLocation("hud/zombie_experience_bar_progress");
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = new ResourceLocation("hud/experience_bar_background");




    @Inject(at = @At("HEAD"), method = "renderHotbar", cancellable = true)
    private void renderItemHotbar(float partialTick, GuiGraphics guiGraphics, CallbackInfo info) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player && BeUndeadApi.getZombieType(player) > 0 && BeUndeadApi.getInvStateOfPlayer(player) == 0) {
            info.cancel();
            ItemStack offhandItemstack = player.getOffhandItem();
            HumanoidArm humanoidarm = player.getMainArm().getOpposite();
            int i = guiGraphics.guiWidth() / 2;
            RenderSystem.enableBlend();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);

            int additionXSlot = 80;

            guiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, i - 98 + additionXSlot, guiGraphics.guiHeight() - 22, 29, 22);
            guiGraphics.blitSprite(HOTBAR_SELECTION_SPRITE, i - 91 + additionXSlot - 1, guiGraphics.guiHeight() - 22 - 1, 24, 23);

            if (!offhandItemstack.isEmpty()) {
                if (humanoidarm == HumanoidArm.LEFT) {
                    guiGraphics.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, i - 91 + additionXSlot - 29, guiGraphics.guiHeight() - 23, 29, 24);
                } else {
                    guiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, i + 91 + additionXSlot, guiGraphics.guiHeight() - 23, 29, 24);
                }
            }

            guiGraphics.pose().popPose();
            RenderSystem.disableBlend();
            int l = 1;
            int i2;
            int j2;
            int k2;


            i2 = 4;
            j2 = i - 90 + 2 + additionXSlot;
            k2 = guiGraphics.guiHeight() - 16 - 3;
            this.renderSlot(guiGraphics, j2, k2, partialTick, player, (ItemStack)player.getInventory().items.get(i2), l++);

            if (!offhandItemstack.isEmpty()) {
                i2 = guiGraphics.guiHeight() - 16 - 3;
                if (humanoidarm == HumanoidArm.LEFT) {
                    this.renderSlot(guiGraphics, i - 91 + additionXSlot - 26, i2, partialTick, player, offhandItemstack, l);
                } else {
                    this.renderSlot(guiGraphics, i + 91 + additionXSlot + 10, i2, partialTick, player, offhandItemstack, l);
                }
            }

            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                RenderSystem.enableBlend();
                float f = player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    j2 = guiGraphics.guiHeight() - 20;
                    k2 = i + 91 + 6;
                    if (humanoidarm == HumanoidArm.RIGHT) {
                        k2 = i - 91 - 22;
                    }

                    int l1 = (int)(f * 19.0F);
                    guiGraphics.blitSprite(HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                    guiGraphics.blitSprite(HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
                }

                RenderSystem.disableBlend();
            }
        }

    }

//    @Inject(at = @At("HEAD"), method = "renderExperienceBar", cancellable = true)
//    private void renderExperienceBar(GuiGraphics guiGraphics, int pX, CallbackInfo info){
//        if (BeUndeadApi.getZombieType(Minecraft.getInstance().player) > 0){
//            Minecraft.getInstance().getProfiler().push("expBar");
//            int i = Minecraft.getInstance().player.getXpNeededForNextLevel();
//            if (i > 0) {
//                int k = (int)(Minecraft.getInstance().player.experienceProgress * 183.0F);
//                int l = guiGraphics.guiHeight() - 32 + 3;
//                RenderSystem.enableBlend();
//                guiGraphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, pX, l, 182, 5);
//                if (k > 0) {
//                    guiGraphics.blitSprite(ZOMBIE_EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, pX, l, k, 5);
//                }
//
//                RenderSystem.disableBlend();
//            }
//
//            Minecraft.getInstance().getProfiler().pop();
//            info.cancel();
//        }
//    }

    @Inject(at = @At("HEAD"), method = "renderExperienceBar", cancellable = true)
    private void renderExperienceLevel(GuiGraphics guiGraphics, int i, CallbackInfo info){
        if (BeUndeadApi.getZombieType(Minecraft.getInstance().player) > 0){
            this.minecraft.getProfiler().push("expBar");
            int j = this.minecraft.player.getXpNeededForNextLevel();
            if (j > 0) {
                int l = (int)(this.minecraft.player.experienceProgress * 183.0F);
                int m = this.screenHeight - 32 + 3;
                guiGraphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, i, m, 182, 5);
                if (l > 0) {
                    guiGraphics.blitSprite(ZOMBIE_EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, i, m, l, 5);
                }
            }

            minecraft.getProfiler().pop();
            if (minecraft.player.experienceLevel > 0) {
                minecraft.getProfiler().push("expLevel");
                String string = "" + minecraft.player.experienceLevel;
                int l = (this.screenWidth - this.getFont().width(string)) / 2;
                int m = this.screenHeight - 31 - 4;
                guiGraphics.drawString(this.getFont(), string, l + 1, m, 0, false);
                guiGraphics.drawString(this.getFont(), string, l - 1, m, 0, false);
                guiGraphics.drawString(this.getFont(), string, l, m + 1, 0, false);
                guiGraphics.drawString(this.getFont(), string, l, m - 1, 0, false);
                guiGraphics.drawString(this.getFont(), string, l, m, -41952, false);
                this.minecraft.getProfiler().pop();
            }

            info.cancel();
        }
    }
}
