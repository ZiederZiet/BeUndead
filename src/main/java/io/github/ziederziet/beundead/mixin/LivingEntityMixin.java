package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import io.github.ziederziet.beundead.common.UndeadType;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(at = @At("HEAD"), method = "canFreeze", cancellable = true)
    public void canFreeze(CallbackInfoReturnable<Boolean> info){
        if ((Object)this instanceof Player player && !BeUndeadHelper.isHuman(player) && BeUndeadHelper.getUndeadType(player).freezeImmune()){
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo info){
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (!livingEntity.level().isClientSide()){
            if (livingEntity instanceof Player player && !BeUndeadHelper.isHuman(player)){
                long conversionTime = BeUndeadHelper.getZombieConversionTime(player);
                if (conversionTime >= 0){
                    if (conversionTime == 1){
                        BeUndeadHelper.revive(player, true);
                    }
                }

                if (BeUndeadHelper.getInvStateOfPlayer(player) < 1){
                    BeUndeadHelper.checkSideItems(player);
                }

                UndeadType type = BeUndeadHelper.getUndeadType(player);

                if (type != null && type.burnsInTheSun() && BeUndeadHelper.isSunBurnTick(player)){
                    ItemStack itemstack = player.getItemBySlot(EquipmentSlot.HEAD);
                    if (!itemstack.isEmpty()) {
                        if (itemstack.isDamageableItem()) {
                            itemstack.hurtAndBreak(player.getRandom().nextInt(2), player, EquipmentSlot.HEAD);
                            Item item = itemstack.getItem();
                            itemstack.setDamageValue(itemstack.getDamageValue() + player.getRandom().nextInt(2));
                            if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                                player.onEquippedItemBroken(item, EquipmentSlot.HEAD);
                                player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                            }
                        }
                    }
                    else {
                        player.igniteForSeconds(8.0F);
                    }
                }
            }

            if (livingEntity instanceof InfectionAccessor){
                BeUndeadHelper.infectTick(livingEntity);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "isInvertedHealAndHarm()Z", cancellable = true)
    public void isInvertedHealAndHarm(CallbackInfoReturnable<Boolean> info) {
        if ((Object)this instanceof Player player && !BeUndeadHelper.isHuman(player)){
            info.cancel();
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", cancellable = true)
    public void canBeAffected(MobEffectInstance pEffectInstance, CallbackInfoReturnable<Boolean> info) {
        LivingEntity livingEntity = (LivingEntity) (Object)this;
        if ((livingEntity instanceof Player player && !BeUndeadHelper.isHuman(player)) && (pEffectInstance.is(MobEffects.REGENERATION) || pEffectInstance.is(MobEffects.POISON) || pEffectInstance.is(MobEffects.HUNGER))){
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "dropExperience", cancellable = true)
    protected void dropExperience(ServerLevel serverLevel, Entity entity, CallbackInfo info) {
        if (ServerConfigAccessor.getConfig().getZombieOnlyKillExperience() && entity instanceof Player player && !player.level().isClientSide() && !BeUndeadHelper.isHuman(player)){
            info.cancel();
            LivingEntity thisEntity = (LivingEntity) (Object) this;
            if (!(thisEntity instanceof Monster)){
                if (thisEntity instanceof Player killedPlayer){
                    if (BeUndeadHelper.isHuman(killedPlayer)){
                        player.giveExperiencePoints(killedPlayer.totalExperience);
                    }

                    return;
                }
                if (thisEntity instanceof AbstractVillager){
                    int reward = 20;
                    player.giveExperiencePoints(reward);
                }
                else if (thisEntity.shouldDropExperience() && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
                    int reward = thisEntity.getExperienceReward((ServerLevel) player.level(), entity);
                    player.giveExperiencePoints(reward);
                }
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "canBreatheUnderwater()Z", cancellable = true)
    public void canBreatheUnderwater(CallbackInfoReturnable<Boolean> info) {
        if ((Object)this instanceof Player player && !BeUndeadHelper.isHuman(player)){
            UndeadType type = BeUndeadHelper.getUndeadType(player);
            if (type != null && type.breathUnderwater()){
                info.setReturnValue(true);
                info.cancel();
            }
        }
    }

//    @Inject(at = @At("HEAD"), method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;") TODO
//    public void eat(Level level, ItemStack itemStack, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> info){
//        if ((LivingEntity)(Object)this instanceof Player player && !BeUndeadHelper.isHuman(player) && itemStack.is(BeUndead.UNDEAD_CURES)){
//            int cureRequirements = ServerConfigAccessor.getConfig().getCureRequirements();
//            if (cureRequirements > 0 && cureRequirements != 3){
//                if (cureRequirements < 2 || player.hasEffect(MobEffects.WEAKNESS)){
//                    if ((cureRequirements < 4 && itemStack.is(BeUndead.UNDEAD_CURES)) || (cureRequirements > 3 && itemStack.is(Items.ENCHANTED_GOLDEN_APPLE))) {
//                        BeUndeadHelper.startConverting(player, player);
//                    }
//                }
//            }
//        }
//    }

    @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection")
    private void checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> info){
        if ((LivingEntity)(Object)this instanceof Player player && !BeUndeadHelper.isHuman(player)){
            if (ServerConfigAccessor.getConfig().getCureRequirements() == 3 && player.hasEffect(MobEffects.WEAKNESS)){
                if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    boolean totem = false;

                    for (InteractionHand interactionHand : InteractionHand.values()) {
                        ItemStack itemStack2 = player.getItemInHand(interactionHand);
                        if (itemStack2.is(Items.TOTEM_OF_UNDYING)) {
                            totem = true;
                            break;
                        }
                    }

                    if (totem){
                        BeUndeadHelper.setUndeadType(player, "");
                    }
                }
            }
        }
    }
}