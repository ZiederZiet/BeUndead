package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
public class AbstractContainerScreenMixin {
    private static final ResourceLocation SLOT_DISABLED_TEXTURE = new ResourceLocation("textures/gui/sprites/container/slot_disabled.png");
    @Inject(at = @At("TAIL"), method = "renderSlot")
    protected void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo info){
        if (pSlot.container instanceof Inventory inventory && BeUndeadApi.getZombieType(inventory.player) > 0) {
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if (invState < 2)
            {
                int slot = pSlot.getContainerSlot();
                if ((Object)this instanceof CreativeModeInventoryScreen creativeModeInventoryScreen && creativeModeInventoryScreen.isInventoryOpen()){
                    if ((!(invState > 0 && slot > 35) && slot != 40) && (slot < 45 && slot > 8)){
                        pGuiGraphics.blit(SLOT_DISABLED_TEXTURE, pSlot.x - 1, pSlot.y - 1, 0,0, 18, 18, 18, 18);
                    }
                }
                else {
                    if (slot < 36 && (!(invState > 0 && slot < 9) && slot != 4)) {
                        pGuiGraphics.blit(SLOT_DISABLED_TEXTURE, pSlot.x - 1, pSlot.y - 1, 0, 0, 18, 18, 18, 18);
                    }
                }

                //pGuiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(slot), pSlot.x - 1, pSlot.y - 1, 16711935, false);
            }
        }
    }

}
