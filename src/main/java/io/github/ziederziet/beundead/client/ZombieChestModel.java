package io.github.ziederziet.beundead.client;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class ZombieChestModel<T extends EntityRenderState> extends EntityModel<T> {

    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation ZOMBIE_CHEST_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "zombie_chest"), "main");
    private final ModelPart chest;

    public ZombieChestModel(ModelPart root) {
        super(root);
        this.chest = root.getChild("chest");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -14.0F, -4.0F, 5.0F, 8.0F, 8.0F, new CubeDeformation(-1.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}