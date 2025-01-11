package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class OptionsMixin {
    @Shadow
    private int serverRenderDistance;
    @Shadow
    private OptionInstance<Integer> renderDistance;
    @Inject(at = @At("TAIL"), method = "getEffectiveRenderDistance()I", cancellable = true)
    public int getEffectiveRenderDistance(CallbackInfoReturnable<Integer> info) {
        int effectiveRenderDistance = info.getReturnValue();
        if (BeUndead.Mod.clientZombieMaxViewDistance > 0){
            if (Minecraft.getInstance().player != null && BeUndead.getZombieType(Minecraft.getInstance().player) > 0){
                effectiveRenderDistance = Math.min(effectiveRenderDistance, BeUndead.Mod.clientZombieMaxViewDistance);
            }
            info.setReturnValue(effectiveRenderDistance);
        }
        return effectiveRenderDistance;
    }
}
