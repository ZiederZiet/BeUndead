package io.github.ziederziet.beundead.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;


@Mixin(Player.class)
public class PlayerMixin {
    private static final EntityDataAccessor<Integer> DATA_ZOMBIE;


    private int zombietype = 0;

    @Inject(at = @At("TAIL"), method = "defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V")
    private void defineSynchedData(SynchedEntityData.Builder pBuilder, CallbackInfo info) {
        pBuilder.define(DATA_ZOMBIE, 0);
    }
    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        this.zombietype = pCompound.getInt("ZombieType");
        SynchedEntityData entityData = ((EntityAccessor)this).getEntityData();
        entityData.set(DATA_ZOMBIE, this.zombietype);
    }
    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        pCompound.putInt("ZombieType", this.zombietype);
    }

    public void setZombieType(int type){
        this.zombietype = type;
        SynchedEntityData entityData = ((EntityAccessor)this).getEntityData();
        entityData.set(DATA_ZOMBIE, zombietype);
    }

    public int getZombieType(){
        return ((EntityAccessor)this).getEntityData().get(DATA_ZOMBIE);
    }

    public static int getZombieTypeOfPlayer(Player player){
        return ((PlayerMixin)(Object)player).getZombieType();
    }

    public static int getZombieTypeOfClient(){
        return getZombieTypeOfPlayer(Minecraft.getInstance().player);
    }

    static {
        DATA_ZOMBIE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    }
}
