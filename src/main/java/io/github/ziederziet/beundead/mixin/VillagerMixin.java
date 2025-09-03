package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(Villager.class)
public class VillagerMixin implements InfectionAccessor {

    private HashMap<UUID, Integer> infecters = new HashMap<>();
    private int inInfection = 0;
    private int outInfection = 0;
    private int infectionKillTicks = 0;

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        BeUndeadApi.infectionAddAdditionalSaveData(pCompound, this, infecters);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag pCompound, CallbackInfo info) {
        BeUndeadApi.infectionReadAdditionalSaveData(pCompound, this, infecters);
    }

    @Inject(at = @At("HEAD"), method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
    public void mobInteract(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> info){
        if (BeUndeadApi.getZombieType(pPlayer) > 0){
            info.setReturnValue(InteractionResult.PASS);
            info.cancel();
        }
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

        inInfection = Math.min(inInfection + amount, BeUndeadApi.MAX_IN_INFECTION);

        if (inInfection >= BeUndeadApi.IN_INFECTION_INSTANT_OUT_AMOUNT){
            BeUndeadApi.showInfection((LivingEntity)(Object)this);
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
