package io.github.ziederziet.beundead.mixin;

import com.mojang.datafixers.util.Pair;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    public Container container;
//    @Shadow
//    public int slot;

    @Shadow
    public abstract int getSlotIndex();

    @Shadow
    private Pair<ResourceLocation, ResourceLocation> backgroundPair;

    @Inject(at = @At("HEAD"), method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    public boolean mayPlace(ItemStack pStack, CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndead.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getSlotIndex();
            int invState = BeUndead.getInvStateOfPlayer(inventory.player);
            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                info.setReturnValue(false);
                return false;
            }
        }
        return true;
    }

    @Inject(at = @At("HEAD"), method = "mayPickup(Lnet/minecraft/world/entity/player/Player;)Z", cancellable = true)
    public boolean mayPickup(Player pPlayer, CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndead.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getSlotIndex();
            int invState = BeUndead.getInvStateOfPlayer(inventory.player);
            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                info.setReturnValue(false);
                return false;
            }
        }
        return true;
    }

//    @Inject(at = @At("HEAD"), method = "isActive()Z", cancellable = true)
//    public boolean isActive(CallbackInfoReturnable<Boolean> info) {
//        if (container instanceof Inventory inventory && BeUndead.getInvStateOfPlayer(inventory.player) < 2){
//            int slot = getSlotIndex();
//            int invState = BeUndead.getInvStateOfPlayer(inventory.player);
//            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
//                info.setReturnValue(false);
//                return false;
//            }
//        }
//        return true;
//    }

//    @Inject(at = @At("HEAD"), method = "isFake()Z", cancellable = true)
//    public boolean isFake(CallbackInfoReturnable<Boolean> info) {
//        if (container instanceof Inventory inventory && BeUndead.getInvStateOfPlayer(inventory.player) < 2){
//            int slot = getSlotIndex();
//            int invState = BeUndead.getInvStateOfPlayer(inventory.player);
//            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
//                info.setReturnValue(true);
//                return true;
//            }
//        }
//        return false;
//    }

    @Inject(at = @At("HEAD"), method = "isHighlightable()Z", cancellable = true)
    public boolean isHighlightable(CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndead.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getSlotIndex();
            int invState = BeUndead.getInvStateOfPlayer(inventory.player);
            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                info.setReturnValue(false);
                return false;
            }
        }
        return true;
    }

//    @Inject(at = @At("HEAD"), method = "getNoItemIcon()Lcom/mojang/datafixers/util/Pair;", cancellable = true)
//    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon(CallbackInfoReturnable<Pair<ResourceLocation, ResourceLocation>> info) {
//        if (container instanceof Inventory inventory && BeUndead.getZombieType(inventory.player) > 0 && slot != 4){
//            //Pair<ResourceLocation, ResourceLocation> pairs = Pair.of(ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "textures/blocks/stone.png"), ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "textures/blocks/diamond_block.png"));
//            info.cancel();
//            //System.out.print(backgroundPair);
//            info.setReturnValue(backgroundPair);
//            return backgroundPair;
//        }
//        return null;
//    }
}
