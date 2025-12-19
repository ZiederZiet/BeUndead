package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.common.InfectedMobEffect;
import io.github.ziederziet.beundead.common.UndeadTypeDataManager;
import io.github.ziederziet.beundead.config.ClientModConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(BeUndead.MODID)
public class BeUndead {
	public static UndeadTypeDataManager UNDEAD_DATA;

	private static MinecraftServer serverInstance;

	public static final String MODID = "beundead";

	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
	public static final DeferredHolder<MobEffect, InfectedMobEffect> INFECTED_EFFECT = MOB_EFFECTS.register("infected", () -> new InfectedMobEffect(MobEffectCategory.NEUTRAL, 1784089));


//	public static final ResourceKey<MobEffect> INFECTED_EFFECT_KEY =
//			ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(MODID, "infected"));
//
//	public static final MobEffect INFECTED_EFFECT = new InfectedMobEffect(MobEffectCategory.NEUTRAL, 1784089);
//	public static Holder<MobEffect> INFECTED_EFFECT_HOLDER;

	public static final ResourceKey<DamageType> INFECTION_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "infection_kill"));

	public static final TagKey<Item> UNDEAD_CURES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "undead_cures"));
	public static final TagKey<Item> UNDEAD_EATABLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "undead_eatables"));

	public static MinecraftServer getServer(){
		return serverInstance;
	}

	public BeUndead(IEventBus modEventBus, ModContainer modContainer){
		modEventBus.addListener(this::commonSetup);

		// Register the Deferred Register
		MOB_EFFECTS.register(modEventBus);

		NeoForge.EVENT_BUS.register(this);

		modContainer.registerConfig(ModConfig.Type.COMMON, ClientModConfig.SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
	}

	@SubscribeEvent
	public void ServerStartingEvent(ServerStartingEvent event){
		serverInstance = event.getServer();
	}

	@SubscribeEvent
	public void ServerStoppingEvent(ServerStoppingEvent event){
		serverInstance = null;
	}
}