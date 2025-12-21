package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadConstants;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(Player.class)
public class UndeadMixin implements UndeadAccessor, InfectionAccessor {
    private boolean zombieChest;
    private long respawnTimer;
    private long conversionTime;
    private int conversionType;
    private String type = "";

    private boolean converting;

    private UUID conversionStarter;

    private HashMap<UUID, Integer> infecters = new HashMap<>();
    private int inInfection = 0;
    private int outInfection = 0;
    private int infectionKillTicks = 0;

    private boolean loaded = false;


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
    public boolean getConverting() {
        return converting;
    }

    @Override
    public void setConverting(boolean converting) {
        this.converting = converting;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo info){
//        if (respawnTimer > 0){
//            respawnTimer--;
//            BeUndeadHelper.sendRespawnTimePacket((Player)(Object)this);
//        }
        if (conversionTime > 0){
            conversionTime--;
            if (conversionTime == 0){
                BeUndeadHelper.sendUndeadPacket((Player)(Object)this);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        this.type = pCompound.getString("UndeadType");
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
        this.zombieChest = zombiechest;

        if (pCompound.contains("ConversionStarter")){
            conversionStarter = pCompound.getUUID("ConversionStarter");
        }

        BeUndeadHelper.infectionReadAdditionalSaveData(pCompound, this, infecters);

        loaded = true;
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        pCompound.putString("UndeadType", type);
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

        BeUndeadHelper.infectionAddAdditionalSaveData(pCompound, this, infecters);
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
    public boolean wasLoaded() {
        return loaded;
    }

    @Override
    public int infectBy(Player infecter, int amount) {
        if (infecter != null){
            UUID infectorUUID = infecter.getUUID();
            int prevAmount = 0;
            if (infecters.containsKey(infectorUUID)){
                prevAmount = infecters.get(infectorUUID);
            }

            infecters.put(infectorUUID, prevAmount + amount);
        }

        inInfection = Math.min(inInfection + amount, BeUndeadConstants.MAX_IN_INFECTION);

        if (inInfection >= BeUndeadConstants.IN_INFECTION_INSTANT_OUT_AMOUNT){
            BeUndeadHelper.showInfection((LivingEntity)(Object)this);
        }

        return inInfection;
    }

    @Override
    public void removeInfection(Player player) {
        inInfection = 0;
        outInfection = 0;
        infectionKillTicks = 0;
        infecters.clear();
    }

    @Override
    public void setInInfection(int amount) {
        inInfection = amount;
    }

    @Override
    public void setOutInfection(int amount) {
        outInfection = amount;
    }

    @Override
    public int getInInfection() {
        return inInfection;
    }

    @Override
    public int getOutInfection() {
        return outInfection;
    }

    @Override
    public void setInfectionKillTicks(int ticks) {
        infectionKillTicks = ticks;
    }

    @Override
    public int getInfectionKillTicks() {
        return infectionKillTicks;
    }

    @Override
    public UUID getMainInfecterUUID() {
        UUID highestInfecter = null;
        int highestValue = -1;
        for (Map.Entry<UUID, Integer> entry : infecters.entrySet()){
            if (entry.getValue() > highestValue){
                highestInfecter = entry.getKey();
                highestValue = entry.getValue();
            }
        }

        return highestInfecter;
    }
}
