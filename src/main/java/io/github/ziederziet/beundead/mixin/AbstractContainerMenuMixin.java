package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(at = @At("HEAD"), method = "doClick", cancellable = true)
    private void doClick(int pSlotId, int pButton, ClickType pClickType, Player pPlayer, CallbackInfo info){
        if (pClickType == ClickType.SWAP && BeUndead.getInvStateOfPlayer(pPlayer) < 1){
            info.cancel();
        }
    }
}
