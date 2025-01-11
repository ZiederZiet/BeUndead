package io.github.ziederziet.beundead.zombie_capability;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AutoRegisterCapability
public class InfectionZombieCapability {
    private @Nullable UUID infecter = null;
    private int infected = 0;
    private int infectDieTicks = 0;
    public InfectionZombieCapability(){
        infecter = null;
    }
    public void saveNBTData(CompoundTag tag){
        if (infecter != null){
            tag.putUUID("Infecter", infecter);
        }
        tag.putInt("Infected", infected);
        tag.putInt("InfectedDieTicks", infectDieTicks);
    }

    public void loadNBTData(CompoundTag tag){
        if (tag.contains("Infecter")){
            infecter = tag.getUUID("Infecter");
        }
        infected = tag.getInt("Infected");
        infectDieTicks = tag.getInt("InfectedDieTicks");
    }

    public void infectBy(@Nullable Player playerInfecter, int infect, int max){
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

    public void removeInfection(Player player){
        this.infected = 0;
        this.infectDieTicks = 0;
        player.removeEffect(BeUndead.INFECTED_EFFECT.getHolder().get());
    }

    public void tick(LivingEntity livingEntity){
        if (livingEntity instanceof Villager || (livingEntity instanceof Player player && BeUndead.getZombieType(player) <= 0)){
            if (this.infected > 30){
                if (!livingEntity.hasEffect(BeUndead.INFECTED_EFFECT.getHolder().get())){
                    livingEntity.addEffect(new MobEffectInstance(BeUndead.INFECTED_EFFECT.getHolder().get(), -1, 0));
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
            if (livingEntity.hasEffect(BeUndead.INFECTED_EFFECT.getHolder().get())){
                livingEntity.removeEffect(BeUndead.INFECTED_EFFECT.getHolder().get());
            }
        }
    }

    public boolean isInfected(){
        return infected >= 30;
    }

    public int getInfected(){
        return infected;
    }
}
