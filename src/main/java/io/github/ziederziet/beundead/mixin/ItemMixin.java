package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.zombie_settings.ZombieSettingsSavedData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;", cancellable = true)
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info){
        if (BeUndead.getZombieType(pPlayer) > 0){
            if (!BeUndead.zombieHasChest(pPlayer) && pPlayer.getItemInHand(pUsedHand).is(Items.CHEST)){
                if (pLevel.isClientSide()){
                    if (!BeUndead.Mod.clientCanChestExtension){
                        return null;
                    }
                }
                else if (!ZombieSettingsSavedData.getZombieSettingsSavedData(pLevel.getServer()).canChestExtension()) {
                    return null;
                }
                info.cancel();
                if (!pLevel.isClientSide()){
                    BeUndead.setZombieChest(pPlayer, true);
                    pPlayer.getItemInHand(pUsedHand).consume(1, pPlayer);
                }
                pLevel.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0F, 1.0F);
                InteractionResultHolder<ItemStack> interactionResultHolder = InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
                info.setReturnValue(interactionResultHolder);
                return interactionResultHolder;
            } else {
                ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
                FoodProperties foodproperties = (FoodProperties)itemstack.get(DataComponents.FOOD);
                if (foodproperties != null){
                    if (!itemstack.is(BeUndead.UNDEAD_EATABLES)){
                        InteractionResultHolder<ItemStack> interactionResultHolder = InteractionResultHolder.fail(itemstack);
                        info.setReturnValue(interactionResultHolder);
                        return interactionResultHolder;
                    }
                }
            }

        }
        return null;
    }
}
