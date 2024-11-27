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

    @Overwrite
    protected void scanForEntities() {
        Level level = ((EntityAccessor)(Object)this).getLevel();

        Vec3 pos = ((EntityAccessor)(Object)this).getPosition();

        if (this.followingPlayer == null || this.followingPlayer.distanceToSqr((ExperienceOrb)(Object)this) > 64.0) {
            this.followingPlayer = level.getNearestEntity(Player.class, TargetingConditions.forNonCombat().range(8.0).selector(livingEntity -> {
                Player player = (Player) livingEntity;
                if (player.isSpectator() || player.isDeadOrDying()){
                    return false;
                }
                return BeUndead.getZombieType(player) == 0;
            }), null, pos.x(), pos.y(), pos.z(), new AABB(pos.x() - 16D, pos.y() - 16D, pos.z() - 16D, pos.x() + 16D, pos.y() + 16D, pos.z() + 16D));
        }

        if (level instanceof ServerLevel) {
            Iterator<ExperienceOrb> var1 = ((ServerLevel)level).getEntitiesOfClass(ExperienceOrb.class, ((EntityAccessor)this).getBoundingBox().inflate(0.5), experienceOrb -> canMerge(experienceOrb, 1, experienceOrb.getValue())).iterator();

            while(var1.hasNext()) {
                ExperienceOrb experienceorb = (ExperienceOrb)var1.next();
                this.merge(experienceorb);
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "playerTouch(Lnet/minecraft/world/entity/player/Player;)V", cancellable = true)
    public void playerTouch(Player pEntity, CallbackInfo info){
        if (BeUndead.getZombieType(pEntity) > 0){
            info.cancel();
        }
    }
}
