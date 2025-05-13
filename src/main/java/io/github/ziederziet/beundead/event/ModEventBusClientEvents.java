package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.client.ZombieChestModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BeUndead.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ZombieChestModel.ZOMBIE_CHEST_LAYER_LOCATION, ZombieChestModel::createBodyLayer);
    }

//    @SubscribeEvent
//    public static void onRegisterShadersEvent(RegisterShadersEvent event){
//        try {
//            event.registerShader(new ShaderInstance(event.getResourceProvider(), "rendertype_zombie_entity_translucent", DefaultVertexFormat.NEW_ENTITY), (shaderInstance) -> {
//                CustomRenderTypes.zombieEntityTranslucentShader = shaderInstance;
//            });
//            event.registerShader(new ShaderInstance(event.getResourceProvider(), "rendertype_zombie_entity_solid", DefaultVertexFormat.NEW_ENTITY), (shaderInstance) -> {
//                CustomRenderTypes.zombieEntitySolidShader = shaderInstance;
//            });
//        } catch (IOException e){
//            System.out.println(e);
//        }
//    }
}
