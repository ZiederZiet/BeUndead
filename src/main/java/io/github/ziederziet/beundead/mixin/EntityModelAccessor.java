package io.github.ziederziet.beundead.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityModel.class)
public interface EntityModelAccessor {
    @Accessor("attackTime")
    float getAttackTime();
}
