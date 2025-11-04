package io.github.ziederziet.beundead.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.mixin.PlayerModelMixin;
import io.github.ziederziet.beundead.mixin.PlayerRendererMixin;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ZombieChestLayer extends RenderLayer<PlayerRenderState, PlayerModel> {

    private final ZombieChestModel<EntityRenderState> model;
    private static final ResourceLocation CHEST_LOCATION = ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "textures/entity/player/zombie_chest.png");
    public ZombieChestLayer(RenderLayerParent pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        model = new ZombieChestModel<EntityRenderState>(pModelSet.bakeLayer(ZombieChestModel.ZOMBIE_CHEST_LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, PlayerRenderState entityRenderState, float f, float g) {
        if (((UndeadRenderState)entityRenderState).hasChestExtension()){
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolid(CHEST_LOCATION));
            model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}