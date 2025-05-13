package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Maps;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

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
        if ((livingEntity instanceof Player player && BeUndeadApi.getZombieType(player) > 0) && (pEffectInstance.is(MobEffects.REGENERATION) || pEffectInstance.is(MobEffects.POISON))){
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "dropExperience(Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
    protected void dropExperience(@Nullable Entity pEntity, CallbackInfo info) {
        if (pEntity instanceof Player player && !player.level().isClientSide() && BeUndeadApi.getZombieType(player) > 0){
            info.cancel();
            LivingEntity thisEntity = (LivingEntity) (Object) this;
            if (!(thisEntity instanceof Monster)){
                if (thisEntity instanceof AbstractVillager){
                    int reward = 20;
                    player.giveExperiencePoints(reward);
                }
                else if (thisEntity.shouldDropExperience() && thisEntity.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
                    int reward = ForgeEventFactory.getExperienceDrop(thisEntity, player, thisEntity.getExperienceReward((ServerLevel) player.level(), pEntity));
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
}
