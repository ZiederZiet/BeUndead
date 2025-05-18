package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @Shadow
    public Player player;

    @Shadow
    public int selected;

    @Shadow
    public NonNullList<ItemStack> items;

    @Shadow
    public abstract ItemStack getItem(int pIndex);

    @Inject(at = @At("HEAD"), method = "getSelected()Lnet/minecraft/world/item/ItemStack;", cancellable = true)
    public void getSelected(CallbackInfoReturnable<ItemStack> info) {
        if (BeUndeadApi.getInvStateOfPlayer(player) == 0){
            info.setReturnValue(items.get(4));
            info.cancel();
        }
    }


    @Inject(at = @At("HEAD"), method = "getFreeSlot()I", cancellable = true)
    public void getFreeSlotInject(CallbackInfoReturnable<Integer> info){
        int invState = BeUndeadApi.getInvStateOfPlayer(player);
        if (invState == 0){
            int returnSlot = -1;
            if (getItem(4).isEmpty()){
                returnSlot = 4;
            }
            info.setReturnValue(returnSlot);
            return;
        }
        else if (invState == 1){
            for(int i = 0; i < 9; ++i) {
                if (((ItemStack)this.items.get(i)).isEmpty()) {
                    info.setReturnValue(i);
                    return;
                }
            }
            info.setReturnValue(-1);
            return;
        }

        //info.setReturnValue(0);
    }


    @Inject(at = @At("HEAD"), method = "add(ILnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    public void addInject(int slot, ItemStack pStack, CallbackInfoReturnable<Boolean> info){
        int invState = BeUndeadApi.getInvStateOfPlayer(player);

        if (slot != -1){
            if (slot > 9){
                if (invState < 2){
                    info.setReturnValue(false);
                }
            }
            else if (slot != 4){
                if (invState < 1){
                    info.setReturnValue(false);
                }
            }
            else {
                info.setReturnValue(false);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "pickSlot(I)V", cancellable = true)
    public void pickSlot(int index, CallbackInfo info) {
        if (BeUndeadApi.getInvStateOfPlayer(player) == 0) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "swapPaint(D)V", cancellable = true)
    public void swapPaint(double direction, CallbackInfo info) {
        if (BeUndeadApi.getInvStateOfPlayer(player) == 0){
            this.selected = 4;
            info.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "dropAll()V")
    public void dropAll(CallbackInfo info){
        if (BeUndeadApi.hasZombieChest(this.player)){
            this.player.drop(new ItemStack(Items.CHEST), true, false);
            BeUndeadApi.setZombieChest(this.player, false);
        }
    }

    @Inject(at = @At("HEAD"), method = "setPickedItem(Lnet/minecraft/world/item/ItemStack;)V", cancellable = true)
    public void setPickedItem(ItemStack stack, CallbackInfo info) {
        if (BeUndeadApi.getInvStateOfPlayer(player) == 0 && player.getInventory().getItem(0).isEmpty()){
            if (this.items.get(selected).isEmpty()){
                this.items.set(this.selected, stack);
            }
            info.cancel();
        }
    }
}
