package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
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
    protected void playStepSound(BlockPos pos, BlockState state, CallbackInfo info){
        if ((Object)this instanceof Player player && BeUndeadApi.getZombieType(player) > 0) {
            BeUndeadApi.playStepSound(player);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isSprinting()Z", cancellable = true)
    public void isSprinting(CallbackInfoReturnable<Boolean> info) {
        if ((Object)this instanceof Player player && BeUndeadApi.getZombieType(player) > 0) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
    public void interact(Player player, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> info){
        if ((Object)this instanceof Player revived && BeUndeadApi.getZombieType(revived) > 0){
            int cureRequirements = ConfigAccessor.getConfig().getCureRequirements();
            if (cureRequirements > 0 && cureRequirements != 3){
                if (cureRequirements > 1 && !revived.hasEffect(MobEffects.WEAKNESS)){
                    return;
                }
                if ((cureRequirements < 4 && player.getItemInHand(pHand).is(BeUndead.UNDEAD_CURES)) || (cureRequirements > 3 && player.getItemInHand(pHand).is(Items.ENCHANTED_GOLDEN_APPLE))) {
                    player.getItemInHand(pHand).consume(1, revived);
                    BeUndeadApi.startConverting(revived, 0, player);
                    info.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onClientRemoval")
    public void onClientRemoval(CallbackInfo info){
        if ((Object)this instanceof AbstractClientPlayer abstractClientPlayer){
            UndeadSkinManager.removeSkin(abstractClientPlayer.getSkin().texture());
        }
    }
}