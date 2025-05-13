package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
