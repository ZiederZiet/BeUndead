package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.client.LevelSummaryAccessor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelSummary.class)
public class LevelSummaryMixin implements LevelSummaryAccessor {
    private boolean undeadMode = false;

    @Override
    public void setUndead(boolean undead) {
        undeadMode = undead;
    }

    @Override
    public boolean getUndead() {
        return undeadMode;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameType;getName()Ljava/lang/String;"), method = "createInfo")
    private String createInfoRedirect(GameType instance){
        if (undeadMode){
            return "undead";
        }
        return instance.getName();
    }
}