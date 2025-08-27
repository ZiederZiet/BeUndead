package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.client.UndeadSkinManager;
import io.github.ziederziet.beundead.commands.ModCommands;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import io.github.ziederziet.beundead.config.ModConfig;
import io.github.ziederziet.beundead.mixin.DeathScreenAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.networking.UndeadDataPacket;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class BeUndead implements ModInitializer {
	public static final Vector3f[] ZOMBIE_COLORS;
	public static final Vector3f[] ZOMBIE_COLOR_OFFSETS;

	private static MinecraftServer serverInstance;

	public static final String MODID = "beundead";

	public static final ResourceKey<MobEffect> INFECTED_EFFECT_KEY =
			ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(MODID, "infected"));

	public static final MobEffect INFECTED_EFFECT =  new InfectedMobEffect(MobEffectCategory.NEUTRAL, 1784089);
	public static Holder<MobEffect> INFECTED_EFFECT_HOLDER;

	public static final ResourceKey<DamageType> INFECTION_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "infection_kill"));

//	public BeUndead(FMLJavaModLoadingContext context)
//	{
//		IEventBus modEventBus = context.getModEventBus();
//
//		modEventBus.addListener(this::commonSetup);
//
//		context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
//		ModConfig.loadConfig(ModConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve(BeUndead.MODID + "-common.toml"));
//
//		MinecraftForge.EVENT_BUS.register(this);
//
//		modEventBus.addListener(this::addCreative);
//	}

//	private void commonSetup(final FMLCommonSetupEvent event)
//	{
//		event.enqueueWork(() -> {
//			ModNetworking.register();
//		});
//	}

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
		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);

		Registry.register(BuiltInRegistries.MOB_EFFECT,
				INFECTED_EFFECT_KEY.location(),
				INFECTED_EFFECT);
		INFECTED_EFFECT_HOLDER = BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(INFECTED_EFFECT_KEY);
		ModCommands.registerCommands();
		ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, v) -> {
			if (livingEntity.level().isClientSide()) return true;

			if (livingEntity instanceof Player player) {
				ConfigAccessor config = ConfigAccessor.getConfig();
				boolean husks = config.areHusksEnabled();
				boolean drowned = config.areDrownedEnabled();

				boolean respawnTimer = player.getServer().isDedicatedServer();

				int type = BeUndeadApi.getZombieType(player);
				if (type == 0){

					boolean turnZombie = true;

//					if (config.getOnlyTurnWhenInfected() &&){
//
//					}

					if (turnZombie){
						type = BeUndeadApi.moveType(1, BeUndeadApi.getTypeMovementOnDeath(damageSource, player), husks, drowned);

						if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.OUTSIDE_BORDER)){
							BeUndeadApi.setZombieType(player, BeUndeadApi.moveType(type, BeUndeadApi.getTypeMovementOnDeath(damageSource, player), husks, drowned));

							if (respawnTimer){
								BeUndeadApi.setZombieRespawnTimer(player, player.level().getGameTime() + config.getRespawnTimer());
							}
						} else {
							player.setHealth(20F);
							player.getFoodData().setFoodLevel(20);
							player.getFoodData().setSaturation(20F);

							((ServerLevel)player.level()).getServer().sendSystemMessage(player.getCombatTracker().getDeathMessage());

							if (!player.isSpectator()){
								if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
									for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
										ItemStack itemstack = player.getInventory().getItem(i);
										if (!itemstack.isEmpty() && EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
											player.getInventory().removeItemNoUpdate(i);
										}
									}
								}
							}

							if (player.level() instanceof ServerLevel serverlevel) {
								if (!player.wasExperienceConsumed()) {
									ExperienceOrb.award(serverlevel, player.position(), player.getExperienceReward(serverlevel, player));
									player.totalExperience = 0;
									player.experienceLevel = 0;
									player.experienceProgress = 0;
								}
							}
							player.getInventory().dropAll();

							player.level().getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(64D, 32D, 64D)).forEach(mob -> {
								if (mob.getTarget() != null && mob.getTarget().is(player)){
									if (!(mob instanceof IronGolem)){
										mob.setTarget(null);
										if (mob instanceof NeutralMob neutralMob){
											neutralMob.stopBeingAngry();
										}
									}
								}
								if (mob.getLastHurtByMob() != null && mob.getLastHurtByMob().is(player)){
									mob.setLastHurtByMob(null);
								}
							});

							BeUndeadApi.setZombieType(player, type);
							player.removeAllEffects();
							player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
							player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
							player.awardStat(Stats.DEATHS);
							player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
							player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
							player.clearFire();
							player.setSharedFlagOnFire(false);
							player.getCombatTracker().recheckStatus();
							player.setLastDeathLocation(Optional.of(GlobalPos.of(player.level().dimension(), player.blockPosition())));

							BeUndeadApi.checkItemsInNewState(player, !player.isSpectator());

							return false;
						}
					}
					else if (respawnTimer) {
						BeUndeadApi.setZombieRespawnTimer(player, player.level().getGameTime() + config.getRespawnTimer());
					}
				}
				else {
					BeUndeadApi.setZombieType(player, BeUndeadApi.moveType(type, BeUndeadApi.getTypeMovementOnDeath(damageSource, player), husks, drowned));

					if (respawnTimer) {
						BeUndeadApi.setZombieRespawnTimer(player, player.level().getGameTime() + config.getRespawnTimerToZombie());
					}
				}
			}

			return true;
		});

		ServerLivingEntityEvents.AFTER_DAMAGE.register((livingEntity, damageSource, v, v1, b) -> {
			if (livingEntity instanceof Villager){
				boolean convert = damageSource.is(BeUndead.INFECTION_KILL);
				if (!convert && damageSource.getEntity() instanceof Player player && BeUndeadApi.getZombieType(player) > 0) {
					convert = true;
					player.getFoodData().setFoodLevel(Math.min(20, player.getFoodData().getFoodLevel() + 4));
					player.getFoodData().setSaturation(Math.min(20, player.getFoodData().getSaturationLevel() + FoodConstants.saturationByModifier(4, 3F)));
				}
				if (convert){
					if ((livingEntity.level().getDifficulty() == Difficulty.NORMAL || livingEntity.level().getDifficulty() == Difficulty.HARD) && livingEntity instanceof Villager villager){
						if (!(villager.level().getDifficulty() != Difficulty.HARD && villager.getRandom().nextBoolean())) {
							ZombieVillager zombievillager = (ZombieVillager)villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
							if (zombievillager != null) {
								zombievillager.finalizeSpawn((ServerLevelAccessor) villager.level(), villager.level().getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true));
								zombievillager.setVillagerData(villager.getVillagerData());
								zombievillager.setGossips((Tag)villager.getGossips().store(NbtOps.INSTANCE));
								zombievillager.setTradeOffers(villager.getOffers().copy());
								zombievillager.setVillagerXp(villager.getVillagerXp());
								if (!villager.isSilent()) {
									villager.level().levelEvent((Player)null, 1026, villager.blockPosition(), 0);
								}
							}
						}
					}
				}
			}
		});

		ClientTickEvents.START_CLIENT_TICK.register(clientLevel -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null && player.level().isClientSide()){
				if (Minecraft.getInstance().screen instanceof DeathScreen deathScreen){
					long respawnTimer = BeUndeadApi.getZombieRespawnTimer(player);
					long timeTo = respawnTimer - player.level().getGameTime();
					if (timeTo < 2){
						Button button = ((DeathScreenAccessor)deathScreen).getExitButtons().getFirst();
						button.active = true;
						button.setMessage(Component.translatable("deathScreen.respawn"));
					} else if (timeTo % 20 == 0){
						int minutes = (int)Math.floor(timeTo / 20D / 60D);
						int seconds = (int)Math.floor(timeTo / 20D % 60D);
						((DeathScreenAccessor)deathScreen).getExitButtons().getFirst().setMessage(Component.translatable("deathScreen.respawn").append(" " + minutes + ":" + (String.valueOf(seconds).length() == 1 ? "0" : "") + seconds));
					}
				}
			}
		});

		ModNetworking.registerS2C();

		ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
			UndeadSkinManager.removeAll();
		});

		EntityTrackingEvents.START_TRACKING.register((entity, serverPlayer) -> {
			if (entity instanceof Player toTrack && !toTrack.level().isClientSide()){
				ModNetworking.sendToClient(UndeadDataPacket.getPacket(toTrack), serverPlayer);
			}
		});

		ServerLivingEntityEvents.AFTER_DAMAGE.register((livingEntity, damageSource, v, v1, b) -> {
			if (damageSource.getEntity() instanceof Player player && BeUndeadApi.getZombieType(player) == 2){
				livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
			}

			if (livingEntity instanceof Villager || (livingEntity instanceof Player player && BeUndeadApi.getZombieType(player) <= 0)){
				if (damageSource.getEntity() instanceof Zombie || (damageSource.getEntity() instanceof Player playerAttacker && BeUndeadApi.getZombieType(playerAttacker) > 0)){

					if (livingEntity.getRandom().nextBoolean()){
						Player infecter = damageSource.getEntity() instanceof Player playerAttacker ? playerAttacker : null;
						((InfectionAccessor)livingEntity).infectBy(infecter, damageSource.getEntity() instanceof ZombifiedPiglin ? 15 : 28, 0);
					}
				}
			}
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			BeUndeadApi.setZombieType(newPlayer, BeUndeadApi.getZombieType(oldPlayer), false);
			if (oldPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || alive){
				BeUndeadApi.setZombieChest(newPlayer, BeUndeadApi.hasZombieChest(oldPlayer), false);
			}
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			BeUndeadApi.sendUndeadPacket(newPlayer);
		});

		ServerPlayConnectionEvents.JOIN.register((serverGamePacketListener, packetSender, minecraftServer) -> {
			ConfigAccessor config = ConfigAccessor.getConfig();

			if (!config.getZombieCanChestExtension()){
				if (BeUndeadApi.hasZombieChest(serverGamePacketListener.getPlayer())){
					serverGamePacketListener.getPlayer().drop(new ItemStack(Items.CHEST), true, false);
					BeUndeadApi.setZombieChest(serverGamePacketListener.getPlayer(), false, false);
				}
			}

			BeUndeadApi.sendUndeadPacket(serverGamePacketListener.getPlayer());

			ModNetworking.sendToClient(ConfigAccessor.getPacket(), (ServerPlayer) serverGamePacketListener.getPlayer());
		});

		EntitySleepEvents.ALLOW_SLEEPING.register((player, blockPos) -> {
			Vec3 vec3 = Vec3.atBottomCenterOf(player.blockPosition());
			List<Player> list = player.level().getEntitiesOfClass(Player.class, new AABB(vec3.x() - 8.0, vec3.y() - 5.0, vec3.z() - 8.0, vec3.x() + 8.0, vec3.y() + 5.0, vec3.z() + 8.0), (player1) -> {
				return BeUndeadApi.getZombieType(player1) > 0;
			});
			if (!list.isEmpty()) {
				return Player.BedSleepingProblem.NOT_SAFE;
			}
			return null;
		});

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			serverInstance = server;
		});

		ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
			serverInstance = null;
		});

//		PlayerBlockBreakEvents.BEFORE.register((level, player, blockPos, blockState, blockEntity) -> {
//			return true;
//		});
	}
}