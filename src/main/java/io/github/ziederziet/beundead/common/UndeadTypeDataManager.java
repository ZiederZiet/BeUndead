package io.github.ziederziet.beundead.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
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
    private final List<List<String>> typeMoistHeatLists = new ArrayList<>();
    private final List<Integer> moistNaturals = new ArrayList<>();
    private final Map<DamageType, String> damageTo = new HashMap<>();

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

    public String moveType(String type, int moistMovement, int heatMovement, boolean husks, boolean drowned, DamageSource source){
        if (damageTo.containsKey(source.type())){
            return damageTo.get(source.type());
        }


        int xI = 0;
        int xSI = 0;
        int yI = 0;

        boolean found = false;

        List<List<String>> usingTypeMoistHeatLists = new ArrayList<>();
        List<Integer> usingMoistNaturals = new ArrayList<>();

        for (int i = 0; i < typeMoistHeatLists.size(); i++) {
            List<String> usingTypeI = new ArrayList<>();
            int moistNatural = moistNaturals.get(i);
            for (int j = 0; j < typeMoistHeatLists.get(i).size(); j++) {
                String typeI = typeMoistHeatLists.get(i).get(j);
                if ((!typeI.equals("husk") || husks) && (!typeI.equals("drowned") || drowned)){
                    usingTypeI.add(typeI);
                }
                else if (j < moistNatural){
                    moistNatural--;
                }
            }
            usingTypeMoistHeatLists.add(usingTypeI);
            usingMoistNaturals.add(moistNatural);
        }

        for (int i = 0; i < usingTypeMoistHeatLists.size(); i++) {
            int indexOf = usingTypeMoistHeatLists.get(i).indexOf(type);
            if (indexOf >= 0){
                xI = i;
                xSI = i;
                yI = indexOf;
                found = true;
                break;
            }
        }

        if (!found){
            return type;
        }

        xI = Mth.clamp(xI + heatMovement, 0, usingTypeMoistHeatLists.size() - 1);
        if (xI != xSI){
            yI = usingMoistNaturals.get(xI);
        }
        else {
            yI = Mth.clamp(yI + moistMovement, 0, usingTypeMoistHeatLists.get(xI).size() - 1);
        }

        return usingTypeMoistHeatLists.get(xI).get(yI);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(BeUndead.MODID, FOLDER_NAME);
    }

    private void defaults(List<String> types, List<Float> moistnesses, List<Float> heats){
        this.undeadDataMap.clear();
        this.typeMoistHeatLists.clear();

        this.undeadDataMap.put("zombie", new SerializizedServerUndeadType("Zombie", 0.85F, 1.0F, 0.8F, 0.0F, 0.1F, 0.0F, false, false, true, false, false, new MobEffectInstance[0], "minecraft:entity.zombie.step", "minecraft:entity.zombie.hurt",  "minecraft:entity.zombie.death", "minecraft:entity.zombie.ambient"));
        this.undeadDataMap.put("husk", new SerializizedServerUndeadType("Husk", 0.8F, 0.8F, 0.7F, 0.1F, 0.1F, -0.1F, false, false, false, false, false, new MobEffectInstance[] { new MobEffectInstance(MobEffects.HUNGER, 600, 0) }, "minecraft:entity.husk.step", "minecraft:entity.husk.hurt",  "minecraft:entity.husk.death", "minecraft:entity.husk.ambient"));
        this.undeadDataMap.put("drowned", new SerializizedServerUndeadType("Drowned", 0.49F, 0.72F, 0.8F, 0.0F, 0.05F, 0.03F, true, true, true, false, false, new MobEffectInstance[0], "minecraft:entity.drowned.step", "minecraft:entity.drowned.hurt",  "minecraft:entity.drowned.death", "minecraft:entity.drowned.ambient"));
        //this.undeadDataMap.put("freeze", new SerializizedServerUndeadType("Freeze", 0.63F, 0.63F, 0.8F, 0.05F, 0.05F, 0.1F, false, false, true, false, true, new MobEffectInstance[] { new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 0) }, "minecraft:entity.stray.step", "minecraft:entity.stray.hurt",  "minecraft:entity.stray.death", "minecraft:entity.stray.ambient"));
        //this.undeadDataMap.put("molten", new SerializizedServerUndeadType("Molten", 0.8F, 0.35F, 0.35F, 0.1F, 0.0F, 0.0F, false, false, false, true, false, new MobEffectInstance[0], "minecraft:entity.stray.step", "minecraft:entity.stray.hurt",  "minecraft:entity.stray.death", "minecraft:entity.stray.ambient"));

        types.clear();
        moistnesses.clear();
        heats.clear();

        types.add("zombie");
        moistnesses.add(0F);
        heats.add(0F);
        types.add("husk");
        moistnesses.add(-1F);
        heats.add(0F);
        types.add("drowned");
        moistnesses.add(1F);
        heats.add(0F);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        boolean wasAlreadyInitialized = false;
        if (!undeadDataMap.isEmpty()){
            wasAlreadyInitialized = true;
        }

        List<String> types = new ArrayList<>();
        List<Float> moistnesses = new ArrayList<>();
        List<Float> heats = new ArrayList<>();

        defaults(types, moistnesses, heats);

        Iterator<Map.Entry<ResourceLocation, Resource>> resourceIterator = resourceManager.listResources(FOLDER_NAME, path -> path.toString().endsWith(".json") && !path.getPath().endsWith("human.json")).entrySet().iterator();
        while (resourceIterator.hasNext()){
            Map.Entry<ResourceLocation, Resource> entry = resourceIterator.next();
            ResourceLocation id = entry.getKey();
            Resource resource = entry.getValue();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open()));
                JsonElement element = JsonParser.parseReader(reader);
                UnserializizedServerUndeadType data = GSON.fromJson(element, UnserializizedServerUndeadType.class);
                String path = id.getPath().substring(FOLDER_NAME.length() + 1);

                UnserializedUndeadMobEffect[] unserializedMobEffects = data.mobEffects();
                MobEffectInstance[] mobEffects = new MobEffectInstance[0];
                if (unserializedMobEffects != null){
                    mobEffects = new MobEffectInstance[unserializedMobEffects.length];
                    for (int i = 0; i < mobEffects.length; i++) {
                        ResourceKey<MobEffect> resourceKey = ResourceKey.create(Registries.MOB_EFFECT, new ResourceLocation(unserializedMobEffects[i].mobEffectId()));
                        Optional<Holder.Reference<MobEffect>> optional = BuiltInRegistries.MOB_EFFECT.getHolder(resourceKey);
                        Integer duration = unserializedMobEffects[i].duration();
                        Integer amplifier = unserializedMobEffects[i].amplifier();
                        if (optional.isPresent()){
                            mobEffects[i] = new MobEffectInstance(optional.get().value(), duration == null ? 600 : duration, amplifier == null ? 0 : amplifier);
                        }
                    }
                }

                String type = path.substring(0, path.length() - 5);

                damageTo.clear();

                if (data.damageTypes() != null && BeUndead.getServer() != null){
                    RegistryAccess access = BeUndead.getServer().registryAccess();
                    for (String damageTypeId : data.damageTypes()){
                        var reg = access.registryOrThrow(Registries.DAMAGE_TYPE);
                        Optional<Holder.Reference<DamageType>> optional = reg.getHolder(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(damageTypeId)));
                        if (optional.isPresent()){
                            DamageType damageType = optional.get().value();
                            damageTo.put(damageType, type);
                        }
                    }
                }

                if (data.moistness() != null || data.heat() != null){
                    float moistness = 0F;
                    float heat = 0F;
                    if (data.moistness() != null){
                        moistness = data.moistness();
                    }
                    if (data.heat() != null){
                        heat = data.heat();
                    }

                    if (!types.contains(type)){
                        types.add(type);
                        moistnesses.add(moistness);
                        heats.add(heat);
                    }
                    else {
                        int indexOfAlreadyMostHeat = types.indexOf(type);
                        moistnesses.set(indexOfAlreadyMostHeat, moistness);
                        heats.set(indexOfAlreadyMostHeat, heat);
                    }
                }

                this.undeadDataMap.put(type, new SerializizedServerUndeadType(data.name(), data.r(), data.g(), data.b(), data.rOffset(), data.gOffset(), data.bOffset(), data.canSwimInWater(), data.breathUnderwater(), data.burnsInTheSun(), data.fireImmune(), data.freezeImmune(), mobEffects, data.stepSound(), data.hurtSound(), data.deathSound(), data.ambientSound()));
            } catch (Exception e) {
                System.err.println("Failed to load JSON resource " + id + ": " + e);
                defaults(types, moistnesses, heats);
            }
        }


        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < heats.size(); i++) {
            indices.add(i);
        }

        indices.sort(Comparator.comparing(heats::get));

        List<String> sortedTypes = new ArrayList<>();
        List<Float> sortedMoistnesses = new ArrayList<>();
        List<Float> sortedHeats = new ArrayList<>();

        for (int index : indices) {
            sortedTypes.add(types.get(index));
            sortedMoistnesses.add(moistnesses.get(index));
            sortedHeats.add(heats.get(index));
        }

        types = sortedTypes;
        moistnesses = sortedMoistnesses;
        heats = sortedHeats;

        float lastHeat = -100F;

        indices = new ArrayList<>();

        sortedTypes = new ArrayList<>();
        float closestMoistValue = Float.MAX_VALUE;
        int closestMoistI = 0;

        for (int i = 0; i < types.size(); i++) {
            float heat = heats.get(i);
            if (i == 0 || lastHeat != heat){
                lastHeat = heat;
                if (i > 0){
                    indices.sort(Comparator.comparing(moistnesses::get));
                    for (int index : indices) {
                        sortedTypes.add(types.get(index));
                        float moistnessCloseness = Math.abs(moistnesses.get(index));
                        if (closestMoistValue > moistnessCloseness){
                            closestMoistValue = moistnessCloseness;
                            closestMoistI = index;
                        }
                    }
                    typeMoistHeatLists.add(sortedTypes);
                    moistNaturals.add(closestMoistI);
                    closestMoistValue = Float.MAX_VALUE;
                    closestMoistI = 0;
                    sortedTypes = new ArrayList<>();
                    indices.clear();
                }
            }
            indices.add(i);
        }

        indices.sort(Comparator.comparing(moistnesses::get));
        for (int index : indices) {
            sortedTypes.add(types.get(index));
        }
        typeMoistHeatLists.add(sortedTypes);
        moistNaturals.add(closestMoistI);

        if (wasAlreadyInitialized){
            MinecraftServer server = BeUndead.getServer();
            if (server != null){
                ModNetworking.sendToAllClients(server, ServerConfigAccessor.getPacket());
            }
        }
    }
}