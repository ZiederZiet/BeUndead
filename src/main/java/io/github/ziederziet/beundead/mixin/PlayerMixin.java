package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.config.ClientModConfig;
import io.github.ziederziet.beundead.config.ServerModConfig;
import net.minecraft.server.level.ServerPlayer;
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
        if (ClientModConfig.hasZombieSoundsPlayers()){
            Player player = (Player)(Object)this;
            if (!BeUndeadHelper.isHuman(player)){
                if (player.isAlive() && player.getRandom().nextInt(1500) < this.ambientSoundTime++) {
                    this.ambientSoundTime = -160;
                    BeUndeadHelper.playAmbientSound(player);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "getSpeed()F", cancellable = true)
    public void getSpeed(CallbackInfoReturnable<Float> info){
        Player player = (Player)(Object)this;
        if (!BeUndeadHelper.isHuman(player)){
            info.setReturnValue((float) (info.getReturnValueF() * BeUndeadHelper.getWalkingSpeed(player)));
        }
    }

    @Inject(at = @At("TAIL"), method = "getDestroySpeed", cancellable = true)
    public void getDestroySpeed(BlockState blockState, CallbackInfoReturnable<Float> info){
        if (!BeUndeadHelper.isHuman((Player)(Object)this)){
            info.setReturnValue(info.getReturnValueF() * (((Player)(Object)this).level().isClientSide() ? ClientInfo.zombieBreakingSpeed : ServerModConfig.getZombieBreakSpeed()));
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z"), method = "attack")
    public boolean noCritRedirect(Player instance){
        if (instance instanceof ServerPlayer serverPlayer && !BeUndeadHelper.isHuman(serverPlayer) && !ServerModConfig.getZombieCanCrit()){
            return true;
        }

        return instance.onClimbable();
    }

    @Inject(at = @At("HEAD"), method = "getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;", cancellable = true)
    protected void getHurtSound(DamageSource pDamageSource, CallbackInfoReturnable<SoundEvent> info) {
        if (ClientModConfig.hasZombieSoundsPlayers()){
            Player player = (Player)(Object)this;
            if (!BeUndeadHelper.isHuman(player)){
                SoundEvent hurtSound = BeUndeadHelper.getHurtSound(player);
                if (hurtSound != null){
                    info.setReturnValue(hurtSound);
                }
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "getDeathSound()Lnet/minecraft/sounds/SoundEvent;", cancellable = true)
    protected void getDeathSound(CallbackInfoReturnable<SoundEvent> info) {
        if (ClientModConfig.hasZombieSoundsPlayers()){
            Player player = (Player)(Object)this;
            if (!BeUndeadHelper.isHuman(player)){
                SoundEvent deathSound = BeUndeadHelper.getDeathSound(player);
                if (deathSound != null){
                    info.setReturnValue(deathSound);
                }
            }
        }
    }
}
