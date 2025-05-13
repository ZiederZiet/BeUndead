package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Shadow
    private Player followingPlayer;

    @Inject(at = @At("TAIL"), method = "scanForEntities")
    protected void scanForEntities(CallbackInfo info) {
        if (this.followingPlayer != null && BeUndeadApi.getZombieType(this.followingPlayer) > 0){
            this.followingPlayer = null;
        }
    }

    @Inject(at = @At("HEAD"), method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", cancellable = true)
    public void playerTouch(Player pEntity, CallbackInfo info){
        if (BeUndeadApi.getZombieType(pEntity) > 0){
            info.cancel();
        }
    }
}