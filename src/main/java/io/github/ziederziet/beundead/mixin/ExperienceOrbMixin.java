package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Shadow
    private Player followingPlayer;

    @Inject(at = @At("TAIL"), method = "scanForEntities")
    protected void scanForEntities(CallbackInfo info) {
        if (ServerConfigAccessor.getConfig().getZombieOnlyKillExperience()){
            if (this.followingPlayer != null && !BeUndeadHelper.isHuman(this.followingPlayer)){
                this.followingPlayer = null;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", cancellable = true)
    public void playerTouch(Player pEntity, CallbackInfo info){
        if (ServerConfigAccessor.getConfig().getZombieOnlyKillExperience()){
            if (!BeUndeadHelper.isHuman(pEntity)){
                info.cancel();
            }
        }
    }
}