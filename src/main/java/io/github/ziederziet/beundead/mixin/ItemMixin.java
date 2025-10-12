package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.config.ServerModConfig;
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
    public void use(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info){
        if (!BeUndeadHelper.isHuman(player)){
            if (!BeUndeadHelper.hasZombieChest(player) && player.getItemInHand(usedHand).is(Items.CHEST)){
                if (level.isClientSide()){
                    if (!ClientInfo.canChestExtension){
                        return;
                    }
                }
                else if (!ServerModConfig.get().getZombieCanChestExtension()) {
                    return;
                }
                info.cancel();
                if (!level.isClientSide()){
                    BeUndeadHelper.setZombieChest(player, true);
                    if (!player.getAbilities().instabuild){
                        player.getItemInHand(usedHand).shrink(1);
                    }
                }
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0F, 1.0F);
                info.setReturnValue(InteractionResultHolder.consume(player.getItemInHand(usedHand)));
            } else {
                ItemStack itemstack = player.getItemInHand(usedHand);
                FoodProperties foodproperties = itemstack.getFoodComponent();
                if (foodproperties != null){
                    if (!itemstack.is(BeUndead.UNDEAD_EATABLES)){
                        info.setReturnValue(InteractionResultHolder.fail(itemstack));
                    }
                }
            }
        }
    }
}
