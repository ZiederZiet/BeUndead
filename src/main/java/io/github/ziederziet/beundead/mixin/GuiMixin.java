package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
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
public abstract class GuiMixin extends GuiComponent {
    @Shadow public abstract Font getFont();

    @Shadow private int screenWidth;
    @Shadow private int screenHeight;
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderSlot(PoseStack poseStack, int i, int j, float f, Player player, ItemStack itemStack, int k);

    private static ResourceLocation ZOMBIE_ICONS = new ResourceLocation(BeUndead.MODID, "textures/gui/undead_icons.png");
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");


    @Inject(at = @At("HEAD"), method = "renderHotbar", cancellable = true)
    private void renderItemHotbar(float partialTick, PoseStack poseStack, CallbackInfo info) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player && !BeUndeadHelper.isHuman(player) && BeUndeadHelper.getInvStateOfPlayer(player) == 0) {
            info.cancel();
            ItemStack itemstack = player.getOffhandItem();
            HumanoidArm humanoidarm = player.getMainArm().getOpposite();
            int i = screenWidth / 2;
            RenderSystem.enableBlend();
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, -90.0F);

            int additionXSlot = 80;

            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);

            blit(poseStack, i - 98 + additionXSlot, this.screenHeight - 22, 53, 22, 29, 24);
            blit(poseStack, i - 91 + additionXSlot - 1, this.screenHeight - 22 - 1, 0, 22, 24, 22);
            if (!itemstack.isEmpty()) {
                if (humanoidarm == HumanoidArm.LEFT) {
                    blit(poseStack, i - 91 + additionXSlot - 29, this.screenHeight - 23, 24, 22, 29, 24);
                } else {
                    blit(poseStack, i + 91 + additionXSlot, this.screenHeight - 23, 53, 22, 29, 24);
                }
            }

            poseStack.popPose();
            RenderSystem.disableBlend();
            int l = 1;
            int i2;

            this.renderSlot(poseStack, i - 90 + 2 + additionXSlot, this.screenHeight - 16 - 3, partialTick, player, (ItemStack)player.getInventory().items.get(4), l++);

            if (!itemstack.isEmpty()) {
                i2 = screenHeight - 16 - 3;
                if (humanoidarm == HumanoidArm.LEFT) {
                    this.renderSlot(poseStack, i - 91 + additionXSlot - 26, i2, partialTick, player, itemstack, l);
                } else {
                    this.renderSlot(poseStack, i + 91 + additionXSlot + 10, i2, partialTick, player, itemstack, l);
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
                    RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
                    blit(poseStack, k2, j2, 0, 94, 18, 18);
                    blit(poseStack, k2, j2 + 18 - l1, 18, 112 - l1, 18, l1);
                }
            }

            RenderSystem.disableBlend();
        }

    }

    @Inject(at = @At("HEAD"), method = "renderExperienceBar", cancellable = true)
    private void renderExperienceLevel(PoseStack poseStack, int i, CallbackInfo info){
        if (!BeUndeadHelper.isHuman(Minecraft.getInstance().player)){
            this.minecraft.getProfiler().push("expBar");
            RenderSystem.setShaderTexture(0, ZOMBIE_ICONS);

            RenderSystem.enableBlend();
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, -90.0F);

            int j = this.minecraft.player.getXpNeededForNextLevel();
            if (j > 0) {
                int k = (int)(this.minecraft.player.experienceProgress * 183.0F);
                int l = this.screenHeight - 32 + 3;
                blit(poseStack, i, l, 0, 64, 182, 5);
                if (k > 0) {
                    blit(poseStack, i, l, 0, 69, k, 5);
                }
            }

            minecraft.getProfiler().pop();
            if (minecraft.player.experienceLevel > 0) {
                minecraft.getProfiler().push("expLevel");
                String string = "" + minecraft.player.experienceLevel;
                int l = (this.screenWidth - this.getFont().width(string)) / 2;
                int m = this.screenHeight - 31 - 4;
                this.getFont().draw(poseStack, string, l + 1, m, 0);
                this.getFont().draw(poseStack, string, l + 1, m, 0);
                this.getFont().draw(poseStack, string, l - 1, m, 0);
                this.getFont().draw(poseStack, string, l, m + 1, 0);
                this.getFont().draw(poseStack, string, l, m - 1, 0);
                this.getFont().draw(poseStack, string, l, m, -41952);
                this.minecraft.getProfiler().pop();
            }

            info.cancel();
        }
    }
}
