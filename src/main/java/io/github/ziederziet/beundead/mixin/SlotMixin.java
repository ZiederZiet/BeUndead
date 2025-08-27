package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    public Container container;

    @Shadow public abstract int getContainerSlot();

    @Inject(at = @At("HEAD"), method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    public void mayPlace(ItemStack pStack, CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndeadApi.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getContainerSlot();
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen){
                if ((!(invState > 0 && slot > 35) && slot != 40) && (slot < 45 && slot > 8)){
                    info.setReturnValue(false);
                }
            }
            else {
                if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                    info.setReturnValue(false);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "mayPickup(Lnet/minecraft/world/entity/player/Player;)Z", cancellable = true)
    public void mayPickup(Player pPlayer, CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndeadApi.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getContainerSlot();
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen creativeModeInventoryScreen && creativeModeInventoryScreen.isInventoryOpen()){
                if ((!(invState > 0 && slot > 35) && slot != 40) && (slot < 45 && slot > 8)){
                    info.setReturnValue(false);
                }
            }
            else {
                if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                    info.setReturnValue(false);
                }
            }

        }
    }

    @Inject(at = @At("HEAD"), method = "isHighlightable()Z", cancellable = true)
    public void isHighlightable(CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndeadApi.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getContainerSlot();
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen){
                if ((!(invState > 0 && slot > 35) && slot != 40) && (slot < 45 && slot > 8)){
                    info.setReturnValue(false);
                }
            }
            else {
                if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                    info.setReturnValue(false);
                }
            }
        }
    }
}
