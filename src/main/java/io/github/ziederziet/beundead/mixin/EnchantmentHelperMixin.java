package io.github.ziederziet.beundead.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
//    @Inject(at = @At("TAIL"), method = "getDamageBonus", cancellable = true)
//    private void modifyDamage(int i, EntityType<?> entityType, CallbackInfoReturnable<Float> cir){
//        if (pEntity instanceof Player playerAttacked && BeUndeadApi.getZombieType(playerAttacked) > 0){
//            pTool.getEnchantments().keySet().forEach(enchantmentHolder -> {
//                if (enchantmentHolder.getRegisteredName().equals("minecraft:smite")){
//                    float damageOutcome = info.getReturnValueF();
//
//                    damageOutcome += 2.5F;
//                    int level = pTool.getEnchantments().getLevel(enchantmentHolder);
//                    damageOutcome += level * 2.5F;
//
//                    info.setReturnValue(damageOutcome);
//                }
//            });
//        }
//    }
}
