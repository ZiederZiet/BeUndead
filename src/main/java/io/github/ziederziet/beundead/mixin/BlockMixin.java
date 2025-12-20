package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.config.ServerModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at = @At("HEAD"), method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", cancellable = true)
    private static void dropResources(BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity, Entity entity, ItemStack itemStack, CallbackInfo info){
        if (level instanceof ServerLevel && ServerModConfig.getZombieOnlyKillExperience()) {
            if (entity instanceof ServerPlayer serverPlayer && (blockState.getBlock() instanceof DropExperienceBlock || blockState.getBlock() instanceof RedStoneOreBlock || blockState.getBlock() instanceof SpawnerBlock || blockState.getBlock() instanceof SculkCatalystBlock || blockState.getBlock() instanceof SculkShriekerBlock || blockState.getBlock() instanceof SculkSensorBlock) && !BeUndeadHelper.isHuman(serverPlayer)){
                Block.getDrops(blockState, (ServerLevel)level, blockPos, blockEntity, entity, itemStack).forEach((itemStackx) -> Block.popResource(level, blockPos, itemStackx));
                info.cancel();
            }
        }
    }
}
