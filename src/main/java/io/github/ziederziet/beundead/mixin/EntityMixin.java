package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "playStepSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", cancellable = true)
    protected void playStepSound(BlockPos pPos, BlockState pState, CallbackInfo info){
        if ((Object)this instanceof Player player && BeUndead.getZombieType(player) > 0){
            BeUndead.playStepSound(player);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isSprinting()Z", cancellable = true)
    public boolean isSprinting(CallbackInfoReturnable<Boolean> info) {
        if ((Object)this instanceof Player player && BeUndead.getZombieType(player) > 0){
            info.setReturnValue(false);
            info.cancel();
            return false;
        }
        return false;
    }

    @Inject(at = @At("HEAD"), method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
    public InteractionResult interact(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> info){
        if ((Object)this instanceof Player revived){
            if (revived.hasEffect(MobEffects.WEAKNESS) && pPlayer.getItemInHand(pHand).is(BeUndead.UNDEAD_CURES)){
                pPlayer.getItemInHand(pHand).consume(1, revived);
                BeUndead.startConverting(revived, 0, pPlayer);
                info.setReturnValue(InteractionResult.SUCCESS);
                info.cancel();
                return InteractionResult.SUCCESS;
            }

        }
        return null;
    }
}
