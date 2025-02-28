package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Player.class)
public class PlayerMixin {
    public int ambientSoundTime;

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo info){
        Player player = (Player)(Object)this;
        if (BeUndead.getZombieType(player) > 0){
            if (player.isAlive() && player.getRandom().nextInt(1500) < this.ambientSoundTime++) {
                this.ambientSoundTime = -160;
                BeUndead.playAmbientSound(player);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "getSpeed()F", cancellable = true)
    public void getSpeed(CallbackInfoReturnable<Float> info){
        Player player = (Player)(Object)this;
        if (BeUndead.getZombieType(player) > 0){
            info.setReturnValue((float) (info.getReturnValueF() * BeUndead.getWalkingSpeed(player)));
        }
    }

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
}
