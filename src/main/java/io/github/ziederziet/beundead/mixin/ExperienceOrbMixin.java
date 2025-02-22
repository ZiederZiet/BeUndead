package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
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

    private static boolean canMerge(ExperienceOrb pOrb, int pAmount, int pOther) {
        return !pOrb.isRemoved() && (pOrb.getId() - pAmount) % 40 == 0 && pOrb.value == pOther;
    }

    @Shadow
    private Player followingPlayer;

    @Shadow
    protected abstract void merge(ExperienceOrb pOrb);

    @Inject(at = @At("TAIL"), method = "scanForEntities")
    protected void scanForEntities(CallbackInfo info) {
//        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr((ExperienceOrb)(Object)this) > 64.0) {
//            Level level = ((EntityAccessor)(Object)this).getLevel();
//
//            Vec3 pos = ((EntityAccessor)(Object)this).getPosition();
//            this.followingPlayer = level.getNearestPlayer(pos.x(), pos.y(), pos.z(), 8.0D, entity -> {
//                if (entity instanceof Player player){
//                    if (player.isSpectator() || player.isDeadOrDying()){
//                        return false;
//                    }
//                    return BeUndead.getZombieType(player) == 0;
//                }
//                return false;
//            });

//            if (level instanceof ServerLevel) {
//                for(ExperienceOrb experienceorb : level.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), ((EntityAccessor)(Object)this).getBoundingBox().inflate((double)0.5F), this::canMerge)) {
//                    this.merge(experienceorb);
//                }
//            }
//        }

        if (this.followingPlayer != null && BeUndead.getZombieType(this.followingPlayer) > 0){
            this.followingPlayer = null;
        }
    }

    @Inject(at = @At("HEAD"), method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", cancellable = true)
    public void playerTouch(Player pEntity, CallbackInfo info){
        if (BeUndead.getZombieType(pEntity) > 0){
            info.cancel();
        }
    }
}
