package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Player.class)
public class PlayerMixin {
    public int ambientSoundTime;

//    @Redirect(
//            method = "attack(Lnet/minecraft/world/entity/Entity;)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/MobType;)F"
//            )
//    )
//    public float getDamageBonus(ItemStack pStack, MobType pCreatureAttribute, Entity pTarget){
//        Player player = (Player) (Object) this;
//        float ogFloat = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), ((LivingEntity)pTarget).getMobType());
//
//        if (pTarget instanceof Player target){
//            if (BeUndeadApi.getZombieType(target) > 0){
//                ogFloat = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEAD);
//            }
//        }
//
//        return ogFloat;
//    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo info){
        Player player = (Player)(Object)this;
        if (BeUndeadApi.getZombieType(player) > 0){
            if (player.isAlive() && player.getRandom().nextInt(1500) < this.ambientSoundTime++) {
                this.ambientSoundTime = -160;
                BeUndeadApi.playAmbientSound(player);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "getSpeed()F", cancellable = true)
    public void getSpeed(CallbackInfoReturnable<Float> info){
        Player player = (Player)(Object)this;
        if (BeUndeadApi.getZombieType(player) > 0){
            info.setReturnValue((float) (info.getReturnValueF() * BeUndeadApi.getWalkingSpeed(player)));
        }
    }
}
