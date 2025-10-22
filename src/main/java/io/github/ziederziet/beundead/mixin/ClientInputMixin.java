package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientInput.class)
public class ClientInputMixin  {
    @Inject(at = @At("HEAD"), method = "makeJump")
    public void makeJump(CallbackInfo info){
        if (Minecraft.getInstance().player != null){
            LocalPlayer player = Minecraft.getInstance().player;
            if (!BeUndeadHelper.canJump(player) && !player.getAbilities().flying && !(player.getAbilities().mayfly && !player.onGround())){
                info.cancel();
            }
        }
    }
}
