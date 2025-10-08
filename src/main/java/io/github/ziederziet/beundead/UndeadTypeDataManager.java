package io.github.ziederziet.beundead;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.ziederziet.beundead.common.SerializizedServerUndeadType;
import io.github.ziederziet.beundead.common.UndeadType;
import io.github.ziederziet.beundead.common.UnserializizedServerUndeadType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class UndeadTypeDataManager implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FOLDER_NAME = "undead_types";
    private final Map<String, UndeadType> undeadDataMap = new HashMap<>();

    public UndeadTypeDataManager(){
        this.undeadDataMap.put("zombie", new SerializizedServerUndeadType("Zombie", 0.85F, 1.0F, 0.8F, 0.0F, 0.1F, 0.0F, false, false, true, false, false, new MobEffectInstance[0], "minecraft:entity.zombie.step", "minecraft:entity.zombie.hurt",  "minecraft:entity.zombie.death", "minecraft:entity.zombie.ambient"));
        this.undeadDataMap.put("husk", new SerializizedServerUndeadType("Husk", 0.8F, 0.8F, 0.7F, 0.1F, 0.1F, -0.1F, false, false, false, false, false, new MobEffectInstance[] { new MobEffectInstance(MobEffects.HUNGER, 600, 0) }, "minecraft:entity.husk.step", "minecraft:entity.husk.hurt",  "minecraft:entity.husk.death", "minecraft:entity.husk.ambient"));
        this.undeadDataMap.put("drowned", new SerializizedServerUndeadType("Drowned", 0.49F, 0.72F, 0.8F, 0.0F, 0.05F, 0.03F, true, true, true, false, false, new MobEffectInstance[0], "minecraft:entity.drowned.step", "minecraft:entity.drowned.hurt",  "minecraft:entity.drowned.death", "minecraft:entity.drowned.ambient"));
    }

    public UndeadType get(String id) {
        if (!undeadDataMap.containsKey(id)){
            return null;
        }
        return undeadDataMap.get(id);
    }

    public Set<String> undeadKeys(){
        return undeadDataMap.keySet();
    }

    public Map<String, UndeadType> map(){
        return undeadDataMap;
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, FOLDER_NAME);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for(ResourceLocation id : resourceManager.listResources(FOLDER_NAME, path -> path.toString().endsWith(".json") && !path.getPath().endsWith("human.json")).keySet()) {
            resourceManager.getResource(id).ifPresent(resource -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open()));
                    JsonElement element = JsonParser.parseReader(reader);
                    UnserializizedServerUndeadType data = GSON.fromJson(element, UnserializizedServerUndeadType.class);
                    String path = id.getPath().substring(FOLDER_NAME.length() + 1);

                    String[] mobEffectIds = data.mobEffects();
                    MobEffectInstance[] mobEffects = new MobEffectInstance[mobEffectIds.length];
                    for (int i = 0; i < mobEffects.length; i++) {
                        Optional<Holder.Reference<MobEffect>> optional = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(mobEffectIds[i]));
                        int finalI = i;
                        optional.ifPresent(mobEffectReference -> mobEffects[finalI] = new MobEffectInstance(mobEffectReference, 600, 0));
                    }

                    undeadDataMap.put(path.substring(0, path.length() - 5), new SerializizedServerUndeadType(data.name(), data.r(), data.g(), data.b(), data.rOffset(), data.gOffset(), data.bOffset(), data.canSwimInWater(), data.breathUnderwater(), data.burnsInTheSun(), data.fireImmune(), data.freezeImmune(), mobEffects, data.stepSound(), data.hurtSound(), data.deathSound(), data.ambientSound()));
                } catch (Exception e) {
                    System.err.println("Failed to load JSON resource " + id + ": " + e);
                }
            });
        }
    }
}