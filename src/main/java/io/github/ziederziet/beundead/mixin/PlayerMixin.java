package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.config.ConfigAccessor;
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
        Player player = (Player)(Object)this;
        if (BeUndeadApi.getZombieType(player) > 0){
            if (player.isAlive() && player.getRandom().nextInt(1500) < this.ambientSoundTime++) {
                this.ambientSoundTime = -160;
                BeUndeadApi.playAmbientSound(player);
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
            info.setReturnValue(info.getReturnValueF() * ConfigAccessor.getConfig().getZombieBreakSpeed());
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z"), method = "attack")
    public boolean noCritRedirect(Player instance){
        if (BeUndeadApi.getZombieType(instance) > 0 && !ConfigAccessor.getConfig().getZombieCanCrit()){
            return true;
        }

        return instance.onClimbable();
    }
}
