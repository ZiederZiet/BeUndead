package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class UndeadMixin implements UndeadAccessor {
    private boolean zombieChest;
    private long respawnTimer;
    private long conversionTime;
    private int conversionType;
    private int type;

    private boolean converting;

    @Override
    public boolean hasZombieChest() {
        return zombieChest;
    }

    @Override
    public void setZombieChest(boolean has) {
        zombieChest = has;
    }

    @Override
    public long getZombieRespawnTimer() {
        return respawnTimer;
    }

    @Override
    public void setZombieRespawnTimer(long respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    @Override
    public long getZombieConversionTime() {
        return conversionTime;
    }

    @Override
    public void setZombieConversionTime(long conversionTime) {
        this.conversionTime = conversionTime;
    }

    @Override
    public int getZombieConversionType() {
        return conversionType;
    }

    @Override
    public void setZombieConversionType(int conversionType) {
        this.conversionType = conversionType;
    }

    @Override
    public boolean getConverting() {
        return converting;
    }

    @Override
    public void setConverting(boolean converting) {
        this.converting = converting;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo info){
        if (respawnTimer > 0){
            respawnTimer--;
            BeUndeadApi.sendRespawnTimePacket((Player)(Object)this);
        }
        if (conversionTime > 0){
            conversionTime--;
            if (conversionTime == 0){
                BeUndeadApi.sendUndeadPacket((Player)(Object)this);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        int zombietype = pCompound.getInt("ZombieType");
        boolean zombiechest = pCompound.getBoolean("ZombieChest");
        SynchedEntityData entityData = ((EntityAccessor)this).getEntityData();
        if (pCompound.contains("RespawnTimer")){
            respawnTimer = pCompound.getLong("RespawnTimer");
        }
        else {
            respawnTimer = 0;
        }
        if (pCompound.contains("ConversionTime")){
            conversionTime = pCompound.getInt("ConversionTime");
            conversionType = pCompound.getInt("ZombieConversion");
        }
        else {
            conversionTime = -1;
            conversionType = 0;
        }
        this.type = zombietype;
        this.zombieChest = zombiechest;
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        SynchedEntityData entityData = ((EntityAccessor)this).getEntityData();
        pCompound.putInt("ZombieType", type);
        pCompound.putBoolean("ZombieChest", zombieChest);
        if (respawnTimer > 0){
            pCompound.putLong("RespawnTimer", respawnTimer);
        }
        if (conversionTime >= 0){
            pCompound.putLong("ConversionTime", conversionTime);
        }
        pCompound.putInt("ZombieConversion", conversionType);
    }
}
