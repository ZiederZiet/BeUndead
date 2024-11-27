package io.github.ziederziet.beundead.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;


@Mixin(Player.class)
public class PlayerMixin {



    @Inject(at = @At("TAIL"), method = "defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V")
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder, CallbackInfo info) {
        pBuilder.define(BeUndead.DATA_ZOMBIE, 0);
        pBuilder.define(BeUndead.DATA_ZOMBIE_CHEST, false);
        pBuilder.define(BeUndead.DATA_ZOMBIE_RESPAWN_TIME, 0L);
        pBuilder.define(BeUndead.DATA_ZOMBIE_CONVERSION_TIME, -1);
        pBuilder.define(BeUndead.DATA_ZOMBIE_CONVERSION, 0);
    }
    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        int zombietype = pCompound.getInt("ZombieType");
        boolean zombiechest = pCompound.getBoolean("ZombieChest");
        SynchedEntityData entityData = ((EntityAccessor)this).getEntityData();
        if (pCompound.contains("RespawnTimer")){
            entityData.set(BeUndead.DATA_ZOMBIE_RESPAWN_TIME, pCompound.getLong("RespawnTimer"));
        }
        else {
            entityData.set(BeUndead.DATA_ZOMBIE_RESPAWN_TIME, 0L);
        }
        if (pCompound.contains("ConversionTime")){
            entityData.set(BeUndead.DATA_ZOMBIE_CONVERSION_TIME, pCompound.getInt("ConversionTime"));
            entityData.set(BeUndead.DATA_ZOMBIE_CONVERSION, pCompound.getInt("ZombieConversion"));
        }
        else {
            entityData.set(BeUndead.DATA_ZOMBIE_CONVERSION_TIME, -1);
            entityData.set(BeUndead.DATA_ZOMBIE_CONVERSION, 0);
        }
        entityData.set(BeUndead.DATA_ZOMBIE, zombietype);
        entityData.set(BeUndead.DATA_ZOMBIE_CHEST, zombiechest);
    }
    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        SynchedEntityData entityData = ((EntityAccessor)this).getEntityData();
        pCompound.putInt("ZombieType", entityData.get(BeUndead.DATA_ZOMBIE));
        pCompound.putBoolean("ZombieChest", entityData.get(BeUndead.DATA_ZOMBIE_CHEST));
        long dataRespawnTimer = entityData.get(BeUndead.DATA_ZOMBIE_RESPAWN_TIME);
        if (dataRespawnTimer > 0){
            pCompound.putLong("RespawnTimer", dataRespawnTimer);
        }
        int dataConversionTime = entityData.get(BeUndead.DATA_ZOMBIE_CONVERSION_TIME);
        if (dataConversionTime >= 0){
            pCompound.putInt("ConversionTime", dataConversionTime);
        }
        pCompound.putInt("ZombieConversion", entityData.get(BeUndead.DATA_ZOMBIE_CONVERSION));

    }

    private boolean isSunBurnTick() {
        if (BeUndead.Mod.getFoggyDay()){
            return false;
        }
        Player thisPlayer = (Player) (Object)this;
        if (thisPlayer.level().isDay() && !thisPlayer.level().isClientSide) {
            float f = thisPlayer.getLightLevelDependentMagicValue();
            BlockPos blockpos = BlockPos.containing(thisPlayer.getX(), thisPlayer.getEyeY(), thisPlayer.getZ());
            boolean flag = thisPlayer.isInWaterRainOrBubble() || thisPlayer.isInPowderSnow || thisPlayer.wasInPowderSnow;
            if (f > 0.5F && thisPlayer.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && thisPlayer.level().canSeeSky(blockpos)) {
                return true;
            }
        }

        return false;
    }
}
