package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @Inject(at = @At("TAIL"), method = "getSkinTextureLocation", cancellable = true)
    public void getSkinTextureLocation(CallbackInfoReturnable<ResourceLocation> info) {
        ResourceLocation location = info.getReturnValue();

        int type = BeUndeadApi.getZombieType((AbstractClientPlayer)(Object)this);
        if (type > 0){
            info.setReturnValue(UndeadSkinManager.getOrCreateSkin(location, type, (AbstractClientPlayer)(Object)this));
        }
    }
}
