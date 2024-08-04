package io.github.ziederziet.beundead.goal;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class GetAwayFromExplodingTnt extends Goal {
    protected Monster mob;
    protected PrimedTnt toAvoid;
    protected final float maxDist;
    protected Path path;
    protected final PathNavigation pathNav;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    public GetAwayFromExplodingTnt(Monster mob, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier){
        this.mob = mob;
        this.pathNav = this.mob.getNavigation();
        this.maxDist = pMaxDistance;
        this.walkSpeedModifier = pWalkSpeedModifier;
        this.sprintSpeedModifier = pSprintSpeedModifier;

        this.setFlags(EnumSet.of(Flag.MOVE));
    }
    @Override
    public boolean canUse() {
        if (this.mob.level().getDifficulty() != Difficulty.HARD){
            return false;
        }

        this.toAvoid = getNearestEntity(this.mob.level().getEntitiesOfClass(PrimedTnt.class, this.mob.getBoundingBox().inflate((double)this.maxDist, 3.0, (double)this.maxDist), (p_148078_) -> {
            return true;
        }), entity -> true, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3 $$0 = DefaultRandomPos.getPosAway(this.mob, 11, 7, this.toAvoid.position());
            if ($$0 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr($$0.x, $$0.y, $$0.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath($$0.x, $$0.y, $$0.z, 0);
                return this.path != null;
            }
        }
    }

    @Nullable
    private  <T extends Entity> T getNearestEntity(List<? extends T> pEntities, Predicate<Entity> pPredicate, @Nullable LivingEntity pTarget, double pX, double pY, double pZ) {
        double $$6 = -1.0;
        T $$7 = null;
        Iterator var13 = pEntities.iterator();

        while(true) {
            Entity $$8;
            double $$9;
            do {
                do {
                    if (!var13.hasNext()) {
                        return $$7;
                    }

                    $$8 = (Entity) var13.next();
                } while(!pPredicate.test($$8));

                $$9 = $$8.distanceToSqr(pX, pY, pZ);
            } while($$6 != -1.0 && !($$9 < $$6));

            $$6 = $$9;
            $$7 = (T) $$8;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.pathNav.isDone() && !this.toAvoid.isRemoved();
    }

    @Override
    public void start() {
        this.pathNav.moveTo(this.path, 1F);
    }

    @Override
    public void stop() {
        this.toAvoid = null;
    }

    @Override
    public void tick() {
        if (this.mob.distanceToSqr(this.toAvoid) < 49.0) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }
    }
}
