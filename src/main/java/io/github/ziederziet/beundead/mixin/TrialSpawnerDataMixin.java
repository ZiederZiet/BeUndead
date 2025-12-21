package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.UUID;

@Mixin(TrialSpawnerData.class)
public class TrialSpawnerDataMixin {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/trialspawner/PlayerDetector;detect(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/entity/trialspawner/PlayerDetector$EntitySelector;Lnet/minecraft/core/BlockPos;DZ)Ljava/util/List;"), method = "tryDetectPlayers")
    private List<UUID> detectRedirect(PlayerDetector instance, ServerLevel serverLevel, PlayerDetector.EntitySelector entitySelector, BlockPos blockPos, double v, boolean b){
        List<UUID> uuidList = instance.detect(serverLevel, entitySelector, blockPos, v, b);

        return uuidList.stream().filter(uuid -> {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof Player player && !BeUndeadHelper.isHuman(player)){
                return false;
            }
            return true;
        }).toList();
    }
}
