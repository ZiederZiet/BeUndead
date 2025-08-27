package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import net.minecraft.core.registries.Registries;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(Villager.class)
public class VillagerMixin implements InfectionAccessor {

    private UUID infecter = null;
    private int infected = 0;
    private int infectDieTicks = 0;

    @Inject(at = @At("HEAD"), method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
    public void mobInteract(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> info){
        if (BeUndeadApi.getZombieType(pPlayer) > 0){
            info.setReturnValue(InteractionResult.PASS);
            info.cancel();
        }
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
