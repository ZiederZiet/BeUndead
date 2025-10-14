package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends GuiComponent {
    private static final ResourceLocation SLOT_DISABLED_TEXTURE = new ResourceLocation("textures/gui/sprites/container/slot_disabled.png");
    @Inject(at = @At("TAIL"), method = "renderSlot")
    protected void renderSlot(PoseStack poseStack, Slot slot, CallbackInfo info){
        if (slot.container instanceof Inventory inventory && !BeUndeadHelper.isHuman(inventory.player)) {
            int invState = BeUndeadHelper.getInvStateOfPlayer(inventory.player);
            if (invState < 2) {
                int slotId = slot.getContainerSlot();
                if ((Object)this instanceof CreativeModeInventoryScreen creativeModeInventoryScreen && creativeModeInventoryScreen.isInventoryOpen()){
                    if ((!(invState > 0 && slotId > 35) && slotId != 40) && (slotId < 45 && slotId > 8)){
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderTexture(0, SLOT_DISABLED_TEXTURE);
                        blit(poseStack, slot.x - 1, slot.y - 1, 0,0, 18, 18, 18, 18);
                        RenderSystem.disableBlend();
                    }
                }
                else {
                    if (slotId < 36 && (!(invState > 0 && slotId < 9) && slotId != 4)) {
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderTexture(0, SLOT_DISABLED_TEXTURE);
                        blit(poseStack, slot.x - 1, slot.y - 1, 0, 0, 18, 18, 18, 18);
                        RenderSystem.disableBlend();
                    }
                }
            }
        }
    }

}