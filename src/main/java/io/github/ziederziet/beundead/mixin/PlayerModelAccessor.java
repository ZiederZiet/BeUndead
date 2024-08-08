package io.github.ziederziet.beundead.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerModel.class)
public interface PlayerModelAccessor {
    @Accessor("leftSleeve")
    ModelPart getLeftSleeve();
    @Accessor("rightSleeve")
    ModelPart getRightSleeve();
}
