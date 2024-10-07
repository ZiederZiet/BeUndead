package io.github.ziederziet.beundead.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.event.ModEventBusClientEvents;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class CustomRenderTypes extends RenderType {
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ZOMBIE_ENTITY_TRANSLUCENT;
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ZOMBIE_ENTITY_SOLID;

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static ShaderInstance zombieEntityTranslucentShader;
    @OnlyIn(Dist.CLIENT)
    public static ShaderInstance zombieEntitySolidShader;

    @OnlyIn(Dist.CLIENT)
    public static ShaderInstance getZombieEntityTranslucentShader(){
        return zombieEntityTranslucentShader;
    }
    @OnlyIn(Dist.CLIENT)
    public static ShaderInstance getZombieEntitySolidShaderShader(){
        return zombieEntitySolidShader;
    }

    protected static final ShaderStateShard RENDERTYPE_ZOMBIE_ENTITY_TRANSLUCENT_SHADER = new ShaderStateShard(CustomRenderTypes::getZombieEntityTranslucentShader);
    protected static final ShaderStateShard RENDERTYPE_ZOMBIE_ENTITY_SOLID_SHADER = new ShaderStateShard(CustomRenderTypes::getZombieEntitySolidShaderShader);

    public static RenderType zombieEntityTranslucent(ResourceLocation resourceLocation){
        return ZOMBIE_ENTITY_TRANSLUCENT.apply(resourceLocation, true);
    }

    public static RenderType zombieEntitySolid(ResourceLocation resourceLocation){
        return ZOMBIE_ENTITY_SOLID.apply(resourceLocation, true);
    }

    static {
        ZOMBIE_ENTITY_TRANSLUCENT = Util.memoize((p_286156_, p_286157_) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ZOMBIE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(p_286156_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(p_286157_);
            return create("zombie_entity_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, rendertype$compositestate);
        });

        ZOMBIE_ENTITY_SOLID = Util.memoize((p_286156_, p_286157_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ZOMBIE_ENTITY_SOLID_SHADER).setTextureState(new RenderStateShard.TextureStateShard(p_286156_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(p_286157_);
            return create("zombie_entity_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, rendertype$compositestate);
        });
    }

    public CustomRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }
}
