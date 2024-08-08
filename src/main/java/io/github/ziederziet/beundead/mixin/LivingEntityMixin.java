package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Maps;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public boolean dead;

    @Shadow
    protected final Map<Holder<MobEffect>, MobEffectInstance> activeEffects = Maps.newHashMap();

    @Shadow
    protected abstract void onEffectUpdated(MobEffectInstance pEffectInstance, boolean pForced, @Nullable Entity pEntity);

    @Shadow
    protected abstract void onEffectAdded(MobEffectInstance pEffectInstance, @Nullable Entity pEntity);

    @Overwrite
    public boolean isInvertedHealAndHarm() {
        return ((EntityAccessor)this).getType().is(EntityTypeTags.INVERTED_HEALING_AND_HARM) || ((Object)this instanceof Player player && BeUndead.getZombieType(player) > 0);
    }

//    @Inject(at = @At("TAIL"), method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V")
//    public void die(DamageSource pCause, CallbackInfo info){
//        if (((EntityAccessor)this).getType() == EntityType.PLAYER){
//            Level level =  ((Player)(Object)this).level();
//            if (!level.isClientSide() && !pCause.is(DamageTypes.GENERIC_KILL)){
//                dropAllDeathLoot((ServerLevel) level, pCause);
//            }
//        }
//    }

    //@Inject(at = @At("HEAD"), method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
//    @Overwrite
//    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity)//, CallbackInfoReturnable<Boolean> info)
//    {
//        if (!this.canBeAffected(pEffectInstance)) {
//            //info.setReturnValue(false);
//            return false;
//        } else {
//            if (pEffectInstance.getEffect().get() == MobEffects.HARM){
//                pEffectInstance = new MobEffectInstance(MobEffects.HEAL, pEffectInstance.getDuration(), pEffectInstance.getAmplifier(), pEffectInstance.isVisible(), pEffectInstance.showIcon());
//            } if (pEffectInstance.getEffect().get() == MobEffects.HEAL){
//                pEffectInstance = new MobEffectInstance(MobEffects.HARM, pEffectInstance.getDuration(), pEffectInstance.getAmplifier(), pEffectInstance.isVisible(), pEffectInstance.showIcon());
//            }
//
//
//            MobEffectInstance mobeffectinstance = (MobEffectInstance)this.activeEffects.get(pEffectInstance.getEffect());
//            boolean flag = false;
//            ForgeEventFactory.onLivingEffectAdd((LivingEntity) (Object)this, mobeffectinstance, pEffectInstance, pEntity);
//            if (mobeffectinstance == null) {
//                this.activeEffects.put(pEffectInstance.getEffect(), pEffectInstance);
//                this.onEffectAdded(pEffectInstance, pEntity);
//                flag = true;
//                pEffectInstance.onEffectAdded((LivingEntity) (Object)this);
//            } else if (mobeffectinstance.update(pEffectInstance)) {
//                this.onEffectUpdated(mobeffectinstance, true, pEntity);
//                flag = true;
//            }
//
//            pEffectInstance.onEffectStarted((LivingEntity) (Object)this);
//            //info.setReturnValue(flag);
//            return flag;
//        }
//    }

    @Overwrite
    public boolean canBeAffected(MobEffectInstance pEffectInstance) {
        LivingEntity livingEntity = (LivingEntity) (Object)this;

        Event.Result eventResult = ForgeEventFactory.onLivingEffectCanApply(livingEntity, pEffectInstance).getResult();
        if (!eventResult.isDefault()) {
            return eventResult.isAllowed();
        } else if (livingEntity.getType().is(EntityTypeTags.IMMUNE_TO_INFESTED)) {
            return !pEffectInstance.is(MobEffects.INFESTED);
        } else if (livingEntity.getType().is(EntityTypeTags.IMMUNE_TO_OOZING)) {
            return !pEffectInstance.is(MobEffects.OOZING);
        } else {
            return !(livingEntity.getType().is(EntityTypeTags.IGNORES_POISON_AND_REGEN) || (livingEntity instanceof Player player && BeUndead.getZombieType(player) > 0)) || !pEffectInstance.is(MobEffects.REGENERATION) && !pEffectInstance.is(MobEffects.POISON);
        }
    }

    @Inject(at = @At("HEAD"), method = "canBreatheUnderwater()Z", cancellable = true)
    public boolean canBreatheUnderwater(CallbackInfoReturnable<Boolean> info) {
        if (((EntityAccessor)this).getType() == EntityType.PLAYER && BeUndead.getZombieType((Player) (Object) this) > 0){
            info.setReturnValue(true);
            info.cancel();
            return true;
        }
        return false;
    }
}
