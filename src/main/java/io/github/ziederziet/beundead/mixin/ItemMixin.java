package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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
    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    public void use(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResult> info){
        if (!BeUndeadHelper.isHuman(player)){
            if (!BeUndeadHelper.hasZombieChest(player) && player.getItemInHand(usedHand).is(Items.CHEST)){
                if (level.isClientSide()){
                    if (!ClientInfo.canChestExtension){
                        return;
                    }
                }
                else if (!ServerConfigAccessor.getConfig().getZombieCanChestExtension()) {
                    return;
                }
                info.cancel();
                if (!level.isClientSide()){
                    BeUndeadHelper.setZombieChest(player, true);
                    player.getItemInHand(usedHand).consume(1, player);
                }
                else {
                    player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value());
                }
                info.setReturnValue(InteractionResult.CONSUME);
            } else {
                ItemStack itemstack = player.getItemInHand(usedHand);
                FoodProperties foodproperties = (FoodProperties)itemstack.get(DataComponents.FOOD);
                if (foodproperties != null){
                    if (!itemstack.is(BeUndead.UNDEAD_EATABLES)){
                        info.setReturnValue(InteractionResult.FAIL);
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "finishUsingItem")
    public void finish(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> info){
        if (livingEntity instanceof Player player && !BeUndeadHelper.isHuman(player) && itemStack.is(BeUndead.UNDEAD_CURES)){
            int cureRequirements = ServerConfigAccessor.getConfig().getCureRequirements();
            if (cureRequirements > 0 && cureRequirements != 3){
                if (cureRequirements < 2 || player.hasEffect(MobEffects.WEAKNESS)){
                    if ((cureRequirements < 4 && itemStack.is(BeUndead.UNDEAD_CURES)) || (cureRequirements > 3 && itemStack.is(Items.ENCHANTED_GOLDEN_APPLE))) {
                        BeUndeadHelper.startConverting(player, player);
                    }
                }
            }
        }
    }
}