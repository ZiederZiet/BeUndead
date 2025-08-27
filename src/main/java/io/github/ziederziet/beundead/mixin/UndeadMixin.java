package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(Player.class)
public class UndeadMixin implements UndeadAccessor, InfectionAccessor {
    private boolean zombieChest;
    private long respawnTimer;
    private long conversionTime;
    private int conversionType;
    private int type;

    private boolean converting;

    private UUID conversionStarter;

    private UUID infecter = null;
    private int infected = 0;
    private int infectDieTicks = 0;


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

        if (pCompound.contains("ConversionStarter")){
            conversionStarter = pCompound.getUUID("ConversionStarter");
        }

        if (pCompound.contains("Infecter")){
            infecter = pCompound.getUUID("Infecter");
        }
        infected = pCompound.getInt("Infected");
        infectDieTicks = pCompound.getInt("InfectedDieTicks");
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        pCompound.putInt("ZombieType", type);
        pCompound.putBoolean("ZombieChest", zombieChest);
        if (respawnTimer > 0){
            pCompound.putLong("RespawnTimer", respawnTimer);
        }
        if (conversionTime >= 0){
            pCompound.putLong("ConversionTime", conversionTime);
        }
        pCompound.putInt("ZombieConversion", conversionType);

        if (conversionStarter != null){
            pCompound.putUUID("ConversionStarter", conversionStarter);
        }

        if (infecter != null){
            pCompound.putUUID("Infecter", infecter);
        }
        pCompound.putInt("Infected", infected);
        pCompound.putInt("InfectedDieTicks", infectDieTicks);
    }

    @Override
    public UUID getConversionStarter(){
        return conversionStarter;
    }

    @Override
    public void setConversionStarter(UUID conversionStarter){
        this.conversionStarter = conversionStarter;
    }

    @Override
    public void infectBy(Player playerInfecter, int infect, int max){
        if (max <= 0){
            if (this.infected < 30 && this.infected + infect >= 30){
                if (playerInfecter != null){
                    this.infecter = playerInfecter.getUUID();
                }
            }
            this.infected += infect;
        }
        else {
            this.infected = Math.max(this.infected, Math.min(max, this.infected + infect));
        }
    }

    @Override
    public void removeInfection(Player player){
        this.infected = 0;
        this.infectDieTicks = 0;
        player.removeEffect(BeUndead.INFECTED_EFFECT_HOLDER);
    }

    @Override
    public void tick(LivingEntity livingEntity){
        if (livingEntity instanceof Villager || (livingEntity instanceof Player player && BeUndeadApi.getZombieType(player) <= 0)){
            if (this.infected > 30){
                if (!livingEntity.hasEffect(BeUndead.INFECTED_EFFECT_HOLDER)){
                    livingEntity.addEffect(new MobEffectInstance(BeUndead.INFECTED_EFFECT_HOLDER, -1, 0));
                }

                if (!(livingEntity instanceof Player player && player.isCreative()) && !livingEntity.isSpectator()){
                    double dieTickRate = 0.2D + this.infected / 120D;
                    if (livingEntity.getRandom().nextDouble() < dieTickRate){
                        this.infectDieTicks++;
                    }
                    if (this.infectDieTicks > 1440){
                        ServerPlayer player = null;
                        if (infecter != null){
                            List<ServerPlayer> list = ((ServerLevel)livingEntity.level()).getServer().getPlayerList().getPlayers();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getUUID().getMostSignificantBits() == this.infecter.getMostSignificantBits() &&
                                        list.get(i).getUUID().getLeastSignificantBits() == this.infecter.getLeastSignificantBits()){
                                    player = list.get(i);
                                }
                            }
                        }
                        DamageSource damageSources = new DamageSource(livingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(BeUndead.INFECTION_KILL), player);
                        livingEntity.hurt(damageSources, Float.MAX_VALUE);
                    }
                }
            }
        }
        else {
            if (livingEntity.hasEffect(BeUndead.INFECTED_EFFECT_HOLDER)){
                livingEntity.removeEffect(BeUndead.INFECTED_EFFECT_HOLDER);
            }
        }
    }

    @Override
    public boolean isInfected(){
        return infected >= 30;
    }

    @Override
    public int getInfected(){
        return infected;
    }
}
