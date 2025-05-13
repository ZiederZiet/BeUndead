package io.github.ziederziet.beundead.mixin;

import com.mojang.datafixers.util.Pair;
import io.github.ziederziet.beundead.api.BeUndeadApi;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    public Container container;

    @Shadow
    public abstract int getSlotIndex();

    @Inject(at = @At("HEAD"), method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    public void mayPlace(ItemStack pStack, CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndeadApi.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getSlotIndex();
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                info.setReturnValue(false);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "mayPickup(Lnet/minecraft/world/entity/player/Player;)Z", cancellable = true)
    public void mayPickup(Player pPlayer, CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndeadApi.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getSlotIndex();
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                info.setReturnValue(false);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "isHighlightable()Z", cancellable = true)
    public void isHighlightable(CallbackInfoReturnable<Boolean> info) {
        if (container instanceof Inventory inventory && BeUndeadApi.getInvStateOfPlayer(inventory.player) < 2){
            int slot = getSlotIndex();
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if ((!(invState > 0 && slot < 9) && slot != 4) && slot < 36){
                info.setReturnValue(false);
            }
        }
    }
}
