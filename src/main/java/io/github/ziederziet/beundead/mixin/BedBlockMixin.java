package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedBlockMixin {
    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    protected void use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> info){
        if (BeUndeadApi.getZombieType(pPlayer) > 0){
            if (pLevel.isClientSide()){
                info.setReturnValue(InteractionResult.CONSUME);
            }
            if (pPlayer instanceof ServerPlayer serverPlayer){
                serverPlayer.setRespawnPosition(pLevel.dimension(), pPos, pPlayer.getYRot(), false, true);
            }
            info.setReturnValue(InteractionResult.PASS);
            info.cancel();
        }
    }
}
