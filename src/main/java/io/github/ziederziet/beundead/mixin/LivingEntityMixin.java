package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo info){
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (livingEntity instanceof Player player){
            int type = BeUndeadApi.getZombieType(player);

            if (type > 0){
                long conversionTime = BeUndeadApi.getZombieConversionTime(player);
                if (conversionTime >= 0){
                    if (conversionTime == 1){
                        if (BeUndeadApi.getZombieConversionType(player) > 0){
                            BeUndeadApi.setZombieType(player, BeUndeadApi.getZombieConversionType(player));
                        } else {
                            BeUndeadApi.revive(player, true);
                        }
                    }
                }

                if (BeUndeadApi.getInvStateOfPlayer(player) == 0){
                    BeUndeadApi.checkSideItems(player);
                    player.getInventory().selected = 4;
                }

                if (type != 2 && BeUndeadApi.isSunBurnTick(player)){
                    ItemStack itemstack = player.getItemBySlot(EquipmentSlot.HEAD);
                    if (!itemstack.isEmpty()) {
                        if (itemstack.isDamageableItem()) {
                            itemstack.hurtAndBreak(player.getRandom().nextInt(2), player, player1 -> {
                                player1.broadcastBreakEvent(EquipmentSlot.HEAD);
                            });
                            itemstack.setDamageValue(itemstack.getDamageValue() + player.getRandom().nextInt(2));
                            if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                                player.broadcastBreakEvent(EquipmentSlot.HEAD);
                                player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                            }
                        }
                    }
                    else {
                        player.setSecondsOnFire(8);
                    }
                }
            }
        }

        if (livingEntity instanceof InfectionAccessor){
            BeUndeadApi.infectTick(livingEntity);
        }
    }

    @Inject(at = @At("HEAD"), method = "isInvertedHealAndHarm()Z", cancellable = true)
    public void isInvertedHealAndHarm(CallbackInfoReturnable<Boolean> info) {
        if ((Object)this instanceof Player player && BeUndeadApi.getZombieType(player) > 0){
            info.cancel();
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z", cancellable = true)
    public void canBeAffected(MobEffectInstance pEffectInstance, CallbackInfoReturnable<Boolean> info) {
        LivingEntity livingEntity = (LivingEntity) (Object)this;
        if ((livingEntity instanceof Player player && BeUndeadApi.getZombieType(player) > 0) && (pEffectInstance.getEffect() == MobEffects.REGENERATION || pEffectInstance.getEffect() == MobEffects.POISON || pEffectInstance.getEffect() == MobEffects.HUNGER)){
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onEffectUpdated")
    protected void onEffectUpdated(MobEffectInstance mobEffectInstance, boolean bl, Entity entity, CallbackInfo info){
        if (mobEffectInstance.getEffect() == BeUndead.INFECTED_EFFECT){
            LivingEntity livingEntity = (LivingEntity)(Object)this;
            if (!livingEntity.level().isClientSide()){
                BeUndeadApi.addedInfectionEffect(livingEntity);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "hurt")
    private void hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> info){
        if (info.getReturnValue()){
            LivingEntity livingEntity = (LivingEntity)(Object)this;
            if (damageSource.getEntity() instanceof Player player && BeUndeadApi.getZombieType(player) == 2){
                livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
            }
            else if (ConfigAccessor.getConfig().isInfectionEnabled() && livingEntity instanceof Villager || (livingEntity instanceof Player player && BeUndeadApi.getZombieType(player) <= 0)){
                if (damageSource.getEntity() instanceof Zombie || (damageSource.getEntity() instanceof Player playerAttacker && BeUndeadApi.getZombieType(playerAttacker) > 0)){
                    if (livingEntity.getRandom().nextBoolean()){
                        Player infecter = damageSource.getEntity() instanceof Player playerAttacker ? playerAttacker : null;
                        BeUndeadApi.infectBy(livingEntity, infecter, damageSource.getEntity() instanceof ZombifiedPiglin ? 15 : 28);
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience()V"), method = "dropAllDeathLoot", cancellable = true)
    protected void dropExperience(DamageSource damageSource, CallbackInfo info) {
        if (damageSource.getEntity() instanceof Player player && !player.level().isClientSide() && BeUndeadApi.getZombieType(player) > 0){
            info.cancel();
            LivingEntity thisEntity = (LivingEntity) (Object) this;
            if (!(thisEntity instanceof Monster)){
                if (thisEntity instanceof AbstractVillager){
                    int reward = 20;
                    player.giveExperiencePoints(reward);
                }
                else if (thisEntity.shouldDropExperience() && thisEntity.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
                    int reward = thisEntity.getExperienceReward();
                    player.giveExperiencePoints(reward);
                }
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "canBreatheUnderwater()Z", cancellable = true)
    public void canBreatheUnderwater(CallbackInfoReturnable<Boolean> info) {
        if ((Object)this instanceof Player player && BeUndeadApi.getZombieType(player) == 3){
            info.setReturnValue(true);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;", cancellable = true)
    protected void getHurtSound(DamageSource pDamageSource, CallbackInfoReturnable<SoundEvent> info) {
        if ((Object)this instanceof Player player && BeUndeadApi.getZombieType(player) > 0){
            SoundEvent hurtSound = BeUndeadApi.getHurtSound(player);
            info.setReturnValue(hurtSound);
        }
    }

    @Inject(at = @At("HEAD"), method = "getDeathSound()Lnet/minecraft/sounds/SoundEvent;", cancellable = true)
    protected void getDeathSound(CallbackInfoReturnable<SoundEvent> info) {
        if ((Object)this instanceof Player player && BeUndeadApi.getZombieType(player) > 0){
            SoundEvent deathSound = BeUndeadApi.getDeathSound(player);
            info.setReturnValue(deathSound);
        }
    }

    @Inject(at = @At("HEAD"), method = "eat")
    public void eat(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir){
        if ((LivingEntity)(Object)this instanceof Player player && BeUndeadApi.getZombieType(player) > 0 && itemStack.is(BeUndead.UNDEAD_CURES)){
            int cureRequirements = ConfigAccessor.getConfig().getCureRequirements();
            if (cureRequirements > 0 && cureRequirements != 3){
                if (cureRequirements < 2 || player.hasEffect(MobEffects.WEAKNESS)){
                    if ((cureRequirements < 4 && itemStack.is(BeUndead.UNDEAD_CURES)) || (cureRequirements > 3 && itemStack.is(Items.ENCHANTED_GOLDEN_APPLE))) {
                        BeUndeadApi.startConverting(player, 0, player);
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection")
    private void checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> info){
        if ((LivingEntity)(Object)this instanceof Player player && BeUndeadApi.getZombieType(player) > 0){
            if (ConfigAccessor.getConfig().getCureRequirements() == 3 && player.hasEffect(MobEffects.WEAKNESS)){
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
                        BeUndeadApi.setZombieType(player, 0);
                    }
                }
            }
        }
    }
}