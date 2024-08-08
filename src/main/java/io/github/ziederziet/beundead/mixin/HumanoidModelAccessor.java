package io.github.ziederziet.beundead.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HumanoidModel.class)
public interface HumanoidModelAccessor {
    @Accessor("leftArm")
    ModelPart getLeftArm();
    @Accessor("rightArm")
    ModelPart getRightArm();
}
