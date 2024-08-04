package io.github.ziederziet.beundead.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Creeper.class)
public abstract class CreeperMixin {

//    @Inject(at = @At("TAIL"), method = "tick()V")
//    private void tick(CallbackInfo callbackInfo){
//        Creeper thisC = ((Creeper)(Object)this);
//        if (thisC.getNavigation().isDone() && thisC.getTarget() != null){// && thisC.getTarget().distanceToSqr(thisC.position()) > 6F){
//            setSwellDir(1);
//        }
//    }

//    @Shadow
//    protected abstract void explodeCreeper();

//    @Shadow
//    protected abstract void setSwellDir(int pState);
}
