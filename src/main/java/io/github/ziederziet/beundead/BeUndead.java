package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.commands.ModCommands;
import io.github.ziederziet.beundead.config.ModConfig;
import io.github.ziederziet.beundead.event.ModEvents;
import io.github.ziederziet.beundead.networking.ModNetworking;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import org.joml.Vector3f;

public class BeUndead implements ModInitializer {
	public static final Vector3f[] ZOMBIE_COLORS;
	public static final Vector3f[] ZOMBIE_COLOR_OFFSETS;

	private static MinecraftServer serverInstance;

	public static final String MODID = "beundead";

	public static final ResourceKey<MobEffect> INFECTED_EFFECT_KEY =
			ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(MODID, "infected"));

	public static final MobEffect INFECTED_EFFECT = new InfectedMobEffect(MobEffectCategory.NEUTRAL, 1784089);
	public static Holder<MobEffect> INFECTED_EFFECT_HOLDER;

	public static final ResourceKey<DamageType> INFECTION_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "infection_kill"));

	public static final TagKey<Item> UNDEAD_CURES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "undead_cures"));
	public static final TagKey<Item> UNDEAD_EATABLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "undead_eatables"));

	static {
		ZOMBIE_COLORS = new Vector3f[] { new Vector3f(0.8F, 1.0F, 0.85F), new Vector3f(0.80F, 0.72F, 0.49F), new Vector3f(0.7F, 0.8F, 0.8F) };
		ZOMBIE_COLOR_OFFSETS = new Vector3f[] { new Vector3f(0.0F, 0.1F, 0.0F), new Vector3f(0.03F, 0.05F, 0.0F), new Vector3f(-0.1F, 0.1F, 0.1F) };
	}

	public static MinecraftServer getServer(){
		return serverInstance;
	}

	@Override
	public void onInitialize() {
		//AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);

		Registry.register(BuiltInRegistries.MOB_EFFECT,
				INFECTED_EFFECT_KEY.location(),
				INFECTED_EFFECT);
		INFECTED_EFFECT_HOLDER = BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(INFECTED_EFFECT_KEY);
		ModCommands.registerCommands();

		ModNetworking.registerS2C();

		ServerLivingEntityEvents.ALLOW_DEATH.register(ModEvents::AllowDeathEvent);

		ServerLivingEntityEvents.AFTER_DEATH.register(ModEvents::AfterDeathEvent);

		ClientTickEvents.START_CLIENT_TICK.register(ModEvents::StartClientTick);

		ClientPlayConnectionEvents.DISCONNECT.register(ModEvents::ClientDisconnectEvent);

		EntityTrackingEvents.START_TRACKING.register(ModEvents::StartTrackingEntityEvent);

		ServerLivingEntityEvents.AFTER_DAMAGE.register(ModEvents::AfterDamageEvent);

		ServerPlayerEvents.COPY_FROM.register(ModEvents::PlayerCloneEvent);

		ServerPlayerEvents.AFTER_RESPAWN.register(ModEvents::AfterRespawnEvent);

		ServerPlayConnectionEvents.JOIN.register(ModEvents::JoinServerEvent);

		EntitySleepEvents.ALLOW_SLEEPING.register(ModEvents::AllowSleepingEvent);

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			serverInstance = server;
		});

		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
			serverInstance = null;
		});
	}
}