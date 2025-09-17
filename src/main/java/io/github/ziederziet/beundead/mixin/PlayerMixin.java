package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.config.ClientConfigAccessor;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Player.class)
public class PlayerMixin {
    public int ambientSoundTime;

    @Inject(at = @At("HEAD"), method = "tick()V")
    public void tick(CallbackInfo info){
        if (ClientConfigAccessor.getConfig().hasZombieSoundsPlayers()){
            Player player = (Player)(Object)this;
            if (BeUndeadApi.getZombieType(player) > 0){
                if (player.isAlive() && player.getRandom().nextInt(1500) < this.ambientSoundTime++) {
                    this.ambientSoundTime = -160;
                    BeUndeadApi.playAmbientSound(player);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "getSpeed()F", cancellable = true)
    public void getSpeed(CallbackInfoReturnable<Float> info){
        Player player = (Player)(Object)this;
        if (BeUndeadApi.getZombieType(player) > 0){
            info.setReturnValue((float) (info.getReturnValueF() * BeUndeadApi.getWalkingSpeed(player)));
        }
    }

    @Inject(at = @At("TAIL"), method = "getDestroySpeed", cancellable = true)
    public void getDestroySpeed(BlockState blockState, CallbackInfoReturnable<Float> info){
        if (BeUndeadApi.getZombieType((Player)(Object)this) > 0){
            info.setReturnValue(info.getReturnValueF() * ServerConfigAccessor.getConfig().getZombieBreakSpeed());
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z"), method = "attack")
    public boolean noCritRedirect(Player instance){
        if (BeUndeadApi.getZombieType(instance) > 0 && !ServerConfigAccessor.getConfig().getZombieCanCrit()){
            return true;
        }

        return instance.onClimbable();
    }

    @Inject(at = @At("HEAD"), method = "getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;", cancellable = true)
    protected void getHurtSound(DamageSource pDamageSource, CallbackInfoReturnable<SoundEvent> info) {
        if (ClientConfigAccessor.getConfig().hasZombieSoundsPlayers()){
            Player player = (Player)(Object)this;
            if (BeUndeadApi.getZombieType(player) > 0){
                SoundEvent hurtSound = BeUndeadApi.getHurtSound(player);
                info.setReturnValue(hurtSound);
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "getDeathSound()Lnet/minecraft/sounds/SoundEvent;", cancellable = true)
    protected void getDeathSound(CallbackInfoReturnable<SoundEvent> info) {
        if (ClientConfigAccessor.getConfig().hasZombieSoundsPlayers()){
            Player player = (Player)(Object)this;
            if (BeUndeadApi.getZombieType(player) > 0){
                SoundEvent deathSound = BeUndeadApi.getDeathSound(player);
                info.setReturnValue(deathSound);
            }
        }
    }
}
