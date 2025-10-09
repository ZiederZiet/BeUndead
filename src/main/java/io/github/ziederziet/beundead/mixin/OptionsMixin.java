package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.ClientInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(at = @At("TAIL"), method = "getEffectiveRenderDistance()I", cancellable = true)
    public void getEffectiveRenderDistance(CallbackInfoReturnable<Integer> info) {
        if (ClientInfo.zombieMaxViewDistance > 0){
            if (Minecraft.getInstance().player != null && !BeUndeadHelper.isHuman(Minecraft.getInstance().player)){
                info.setReturnValue(Math.min(info.getReturnValue(), ClientInfo.zombieMaxViewDistance));
            }
        }
    }
}
