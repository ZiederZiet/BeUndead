package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", cancellable = true)
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info){
        if (BeUndead.getZombieType(pPlayer) > 0 && !BeUndead.zombieHasChest(pPlayer) && pPlayer.getItemInHand(pUsedHand).is(Items.CHEST)){
            info.cancel();
            if (!pLevel.isClientSide()){
                BeUndead.setZombieChest(pPlayer, true);
                pPlayer.getItemInHand(pUsedHand).consume(1, pPlayer);
            }
            pLevel.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0F, 1.0F);
            InteractionResultHolder<ItemStack> interactionResultHolder = InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
            info.setReturnValue(interactionResultHolder);
            return interactionResultHolder;
        }
        return null;
    }
}
