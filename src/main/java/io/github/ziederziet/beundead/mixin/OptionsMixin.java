package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Options.class)
public class OptionsMixin {
    @Shadow
    private int serverRenderDistance;
    @Shadow
    private OptionInstance<Integer> renderDistance;
    @Overwrite
    public int getEffectiveRenderDistance() {
        int effectiveRenderDistance = this.serverRenderDistance > 0 ? Math.min((Integer)this.renderDistance.get(), this.serverRenderDistance) : (Integer)this.renderDistance.get();
        if (Minecraft.getInstance().player != null && BeUndead.getZombieType(Minecraft.getInstance().player) > 0){
            effectiveRenderDistance = Math.min(effectiveRenderDistance, 4);
        }
        if (BeUndead.Mod.getFoggyDay()){
            effectiveRenderDistance = Math.min(effectiveRenderDistance, 3);
        }
        return effectiveRenderDistance;
    }
}
