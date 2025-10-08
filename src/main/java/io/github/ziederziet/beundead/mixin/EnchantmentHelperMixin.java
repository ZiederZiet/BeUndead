package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(at = @At("TAIL"), method = "modifyDamage(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)F", cancellable = true)
    private static void modifyDamage(ServerLevel pLevel, ItemStack pTool, Entity pEntity, DamageSource pDamageSource, float pDamage, CallbackInfoReturnable<Float> info){
        if (pEntity instanceof Player playerAttacked && !BeUndeadHelper.isHuman(playerAttacked)){
            pTool.getEnchantments().keySet().forEach(enchantmentHolder -> {
                if (enchantmentHolder.getRegisteredName().equals("minecraft:smite")){
                    float damageOutcome = info.getReturnValueF();

                    damageOutcome += 2.5F;
                    int level = pTool.getEnchantments().getLevel(enchantmentHolder);
                    damageOutcome += level * 2.5F;

                    info.setReturnValue(damageOutcome);
                }
            });
        }
    }
}
