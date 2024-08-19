package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.ImmutableList;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.openjdk.nashorn.internal.objects.annotations.Constructor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
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
        if (BeUndead.getZombieType(player) > 0){
            info.setReturnValue(items.get(4));
            info.cancel();
            return items.get(4);
        }
        return null;
    }

    @Shadow
    protected abstract int addResource(ItemStack pStack);

    @Shadow
    protected abstract int addResource(int pSlot, ItemStack pStack);

    @Shadow
    public abstract int getFreeSlot();

    @Inject(at = @At("HEAD"), method = "add(ILnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    public boolean add(int pSlot, ItemStack pStack, CallbackInfoReturnable<Boolean> info){

        if (BeUndead.getZombieType(player) == 0){
            return false;
        }
        else {
            info.cancel();
            pSlot = 4;
        }


        if (pStack.isEmpty()) {
            info.setReturnValue(false);
            return false;
        } else {

            if (!getItem(pSlot).isEmpty()){
                ItemStack alreadyInHand = getItem(pSlot);
                if (alreadyInHand.isStackable()){
                    //System.out.print(!ItemStack.isSameItemSameComponents(alreadyInHand, pStack));
                    if (!ItemStack.isSameItemSameComponents(alreadyInHand, pStack)){
                        info.setReturnValue(false);
                        return false;
                    }
                }
                else {
                    info.setReturnValue(false);
                    return false;
                }
            }

            Throwable throwable;
            CrashReport crashreport;
            CrashReportCategory crashreportcategory;
            label80: {
                try {
                    if (!pStack.isDamaged()) {
                        break label80;
                    }

                    if (pSlot == -1) {
                        pSlot = this.getFreeSlot();
                    }

                    if (pSlot >= 0) {
                        this.items.set(pSlot, pStack.copyAndClear());
                        ((ItemStack)this.items.get(pSlot)).setPopTime(5);
                        info.setReturnValue(true);
                        return true;
                    }
                } catch (Throwable var10) {
                    throwable = var10;
                    crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                    crashreportcategory = crashreport.addCategory("Item being added");
                    crashreportcategory.setDetail("Registry Name", () -> {
                        return String.valueOf(ForgeRegistries.ITEMS.getKey(pStack.getItem()));
                    });
                    crashreportcategory.setDetail("Item Class", () -> {
                        return pStack.getItem().getClass().getName();
                    });
                    crashreportcategory.setDetail("Item ID", Item.getId(pStack.getItem()));
                    crashreportcategory.setDetail("Item data", pStack.getDamageValue());
                    crashreportcategory.setDetail("Item name", () -> {
                        return pStack.getHoverName().getString();
                    });
                    throw new ReportedException(crashreport);
                }

                try {
                    if (this.player.hasInfiniteMaterials()) {
                        pStack.setCount(0);
                        info.setReturnValue(true);
                        return true;
                    }
                } catch (Throwable var8) {
                    throwable = var8;
                    crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                    crashreportcategory = crashreport.addCategory("Item being added");
                    crashreportcategory.setDetail("Registry Name", () -> {
                        return String.valueOf(ForgeRegistries.ITEMS.getKey(pStack.getItem()));
                    });
                    crashreportcategory.setDetail("Item Class", () -> {
                        return pStack.getItem().getClass().getName();
                    });
                    crashreportcategory.setDetail("Item ID", Item.getId(pStack.getItem()));
                    crashreportcategory.setDetail("Item data", pStack.getDamageValue());
                    crashreportcategory.setDetail("Item name", () -> {
                        return pStack.getHoverName().getString();
                    });
                    throw new ReportedException(crashreport);
                }

                try {
                    info.setReturnValue(false);
                    return false;
                } catch (Throwable var6) {
                    throwable = var6;
                    crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                    crashreportcategory = crashreport.addCategory("Item being added");
                    crashreportcategory.setDetail("Registry Name", () -> {
                        return String.valueOf(ForgeRegistries.ITEMS.getKey(pStack.getItem()));
                    });
                    crashreportcategory.setDetail("Item Class", () -> {
                        return pStack.getItem().getClass().getName();
                    });
                    crashreportcategory.setDetail("Item ID", Item.getId(pStack.getItem()));
                    crashreportcategory.setDetail("Item data", pStack.getDamageValue());
                    crashreportcategory.setDetail("Item name", () -> {
                        return pStack.getHoverName().getString();
                    });
                    throw new ReportedException(crashreport);
                }
            }

            int i;
            try {
                do {
                    i = pStack.getCount();
                    if (pSlot == -1) {
                        pStack.setCount(this.addResource(pStack));
                    } else {
                        pStack.setCount(this.addResource(pSlot, pStack));
                    }
                } while(!pStack.isEmpty() && pStack.getCount() < i);

                if (pStack.getCount() == i && this.player.hasInfiniteMaterials()) {
                    pStack.setCount(0);
                    info.setReturnValue(true);
                    return true;
                }
            } catch (Throwable var9) {
                throwable = var9;
                crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                crashreportcategory = crashreport.addCategory("Item being added");
                crashreportcategory.setDetail("Registry Name", () -> {
                    return String.valueOf(ForgeRegistries.ITEMS.getKey(pStack.getItem()));
                });
                crashreportcategory.setDetail("Item Class", () -> {
                    return pStack.getItem().getClass().getName();
                });
                crashreportcategory.setDetail("Item ID", Item.getId(pStack.getItem()));
                crashreportcategory.setDetail("Item data", pStack.getDamageValue());
                crashreportcategory.setDetail("Item name", () -> {
                    return pStack.getHoverName().getString();
                });
                throw new ReportedException(crashreport);
            }

            try {
                info.setReturnValue(pStack.getCount() < i);
                return pStack.getCount() < i;
            } catch (Throwable var7) {
                throwable = var7;
                crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                crashreportcategory = crashreport.addCategory("Item being added");
                crashreportcategory.setDetail("Registry Name", () -> {
                    return String.valueOf(ForgeRegistries.ITEMS.getKey(pStack.getItem()));
                });
                crashreportcategory.setDetail("Item Class", () -> {
                    return pStack.getItem().getClass().getName();
                });
                crashreportcategory.setDetail("Item ID", Item.getId(pStack.getItem()));
                crashreportcategory.setDetail("Item data", pStack.getDamageValue());
                crashreportcategory.setDetail("Item name", () -> {
                    return pStack.getHoverName().getString();
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "pickSlot(I)V", cancellable = true)
    public void pickSlot(int pIndex, CallbackInfo info) {
        if (BeUndead.getZombieType(player) > 0) {
//            if (this.items.get(selected).isEmpty()){
//                this.items.set(this.selected, (ItemStack)this.items.get(pIndex));
//            }
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "swapPaint(D)V", cancellable = true)
    public void swapPaint(double pDirection, CallbackInfo info) {
        if (BeUndead.getZombieType(player) > 0){
            this.selected = 4;
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "setPickedItem(Lnet/minecraft/world/item/ItemStack;)V", cancellable = true)
    public void setPickedItem(ItemStack pStack, CallbackInfo info) {
        if (BeUndead.getZombieType(player) > 0 && player.getInventory().getItem(0).isEmpty()){
            if (this.items.get(selected).isEmpty()){
                this.items.set(this.selected, pStack);
            }
            info.cancel();
        }
    }
}
