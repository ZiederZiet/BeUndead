package io.github.ziederziet.beundead.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperMeleeAttackGoal extends MeleeAttackGoal {
    private boolean startedExplodingOnEnd = false;

    public CreeperMeleeAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
    }

    @Override
    public void tick() {
        if (!startedExplodingOnEnd && mob.getNavigation().isDone() && this.mob.distanceToSqr(this.mob.getTarget().position()) > 20F){
            startedExplodingOnEnd = true;
            ((Creeper)mob).setSwellDir(1);
        }
        if (startedExplodingOnEnd){
            mob.getNavigation().stop();
            //((Creeper)mob).setSwellDir(1);
        }
        else {
            super.tick();
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (startedExplodingOnEnd){
            return true;
        }
        return super.canContinueToUse();
    }

    @Override
    public void stop() {
        this.startedExplodingOnEnd = false;
        super.stop();
    }
}
