package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z"), method = "updateLightTexture")
    public boolean updateLightTexture(LocalPlayer instance, Holder<MobEffect> holder){
        if (BeUndeadApi.getZombieType(instance) > 0 && ClientInfo.zombieNightVision){
            return true;
        }

        return instance.hasEffect(holder);
    }
}
