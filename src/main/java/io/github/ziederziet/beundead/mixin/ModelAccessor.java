package io.github.ziederziet.beundead.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@Mixin(Model.class)
public interface ModelAccessor {
    @Accessor("renderType")
    Function<ResourceLocation, RenderType> getUsualRendertype();
}
