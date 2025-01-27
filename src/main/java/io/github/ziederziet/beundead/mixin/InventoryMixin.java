package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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
    public ItemStack getSelected(CallbackInfoReturnable<ItemStack> info) {
        if ( BeUndead.getInvStateOfPlayer(player) == 0){
            info.setReturnValue(items.get(4));
            info.cancel();
            return items.get(4);
        }
        return null;
    }


    @Inject(at = @At("HEAD"), method = "getFreeSlot()I", cancellable = true)
    public int getFreeSlotInject(CallbackInfoReturnable<Integer> info){
        int invState = BeUndead.getInvStateOfPlayer(player);
        if (invState == 0){
            int returnSlot = -1;
            if (getItem(4).isEmpty()){
                returnSlot = 4;
            }
            info.setReturnValue(returnSlot);
            return returnSlot;
        }
        else if (invState == 1){
            for(int i = 0; i < 9; ++i) {
                if (((ItemStack)this.items.get(i)).isEmpty()) {
                    info.setReturnValue(i);
                    return i;
                }
            }
            info.setReturnValue(-1);
            return -1;
        }
        return 0;
    }


    @Inject(at = @At("HEAD"), method = "add(ILnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    public boolean addInject(int pSlot, ItemStack pStack, CallbackInfoReturnable<Boolean> info){
        int invState = BeUndead.getInvStateOfPlayer(player);

        if (pSlot != -1){
            if (pSlot > 9){
                if (invState < 2){
                    info.setReturnValue(false);
                }
                return false;
            }
            else if (pSlot != 4){
                if (invState < 1){
                    info.setReturnValue(false);
                }
                return false;
            }
            else{
                return false;
            }
        }

        return false;
    }

    @Inject(at = @At("HEAD"), method = "pickSlot(I)V", cancellable = true)
    public void pickSlot(int pIndex, CallbackInfo info) {
        if (BeUndead.getInvStateOfPlayer(player) == 0) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "swapPaint(D)V", cancellable = true)
    public void swapPaint(double pDirection, CallbackInfo info) {
        if (BeUndead.getInvStateOfPlayer(player) == 0){
            this.selected = 4;
            info.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "dropAll()V")
    public void dropAll(CallbackInfo info){
        if (BeUndead.zombieHasChest(this.player)){
            this.player.drop(new ItemStack(Items.CHEST), true, false);
            BeUndead.setZombieChest(this.player, false);
        }
    }

    @Inject(at = @At("HEAD"), method = "setPickedItem(Lnet/minecraft/world/item/ItemStack;)V", cancellable = true)
    public void setPickedItem(ItemStack pStack, CallbackInfo info) {
        if (BeUndead.getInvStateOfPlayer(player) == 0 && player.getInventory().getItem(0).isEmpty()){
            if (this.items.get(selected).isEmpty()){
                this.items.set(this.selected, pStack);
            }
            info.cancel();
        }
    }
}
