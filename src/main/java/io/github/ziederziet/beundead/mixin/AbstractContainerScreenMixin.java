package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    private static final ResourceLocation SLOT_DISABLED_TEXTURE = ResourceLocation.withDefaultNamespace("container/slot_disabled");
    @Inject(at = @At("TAIL"), method = "renderSlot")
    protected void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot, CallbackInfo info){
        if (pSlot.container instanceof Inventory inventory && BeUndeadApi.getZombieType(inventory.player) > 0) {
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if (invState < 2)
            {
                if (pSlot.getSlotIndex() < 36 && (!(invState > 0 && pSlot.getSlotIndex() < 9) && pSlot.getSlotIndex() != 4)) {
                    pGuiGraphics.blitSprite(SLOT_DISABLED_TEXTURE, pSlot.x - 1, pSlot.y - 1, 18, 18);
                }
            }
        }
    }

}
