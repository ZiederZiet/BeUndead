package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.client.UndeadRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerRenderState.class)
public class UndeadPlayerRenderStateMixin implements UndeadRenderState {
    boolean chestExtension;
    boolean shaking;
    String type;

    @Override
    public void setChestExtension(boolean chestExtension) {
        this.chestExtension = chestExtension;
    }

    @Override
    public boolean hasChestExtension() {
        return chestExtension;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isHuman() {
        return type.isBlank();
    }

    @Override
    public boolean isShaking() {
        return shaking;
    }

    @Override
    public void setShaking(boolean shaking) {
        this.shaking = shaking;
    }
}
