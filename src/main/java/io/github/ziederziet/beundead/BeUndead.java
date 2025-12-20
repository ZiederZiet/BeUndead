package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.common.InfectedMobEffect;
import io.github.ziederziet.beundead.common.UndeadTypeDataManager;
import io.github.ziederziet.beundead.config.ClientModConfig;
import io.github.ziederziet.beundead.config.ServerModConfig;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

@Mod(BeUndead.MODID)
public class BeUndead
{
    public static UndeadTypeDataManager UNDEAD_DATA;

    public static final Vector3f[] ZOMBIE_COLORS;
    public static final Vector3f[] ZOMBIE_COLOR_OFFSETS;

    public static final String MODID = "beundead";

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BeUndead.MODID);

    public static final RegistryObject<MobEffect> INFECTED_EFFECT = MOB_EFFECTS.register("infected",
            () -> new InfectedMobEffect(MobEffectCategory.NEUTRAL, 1784089));

    public static final ResourceKey<DamageType> INFECTION_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "infection_kill"));

    public BeUndead(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MOB_EFFECTS.register(modEventBus);

        context.registerConfig(ModConfig.Type.CLIENT, ClientModConfig.SPEC);
        ClientModConfig.loadConfig(ClientModConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve(BeUndead.MODID + "-client.toml"));

        context.registerConfig(ModConfig.Type.COMMON, ServerModConfig.SPEC);
        //ServerModConfig.loadConfig(ServerModConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve(BeUndead.MODID + "-common.toml"));

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            ModNetworking.register();
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    public static final TagKey<Item> UNDEAD_CURES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "undead_cures"));
    public static final TagKey<Item> UNDEAD_EATABLES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "undead_eatables"));

    static {
        ZOMBIE_COLORS = new Vector3f[] { new Vector3f(0.8F, 1.0F, 0.85F), new Vector3f(0.80F, 0.72F, 0.49F), new Vector3f(0.7F, 0.8F, 0.8F) };
        ZOMBIE_COLOR_OFFSETS = new Vector3f[] { new Vector3f(0.0F, 0.1F, 0.0F), new Vector3f(0.03F, 0.05F, 0.0F), new Vector3f(-0.1F, 0.1F, 0.1F) };
    }
}