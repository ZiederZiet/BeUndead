package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.commands.ModCommands;
import io.github.ziederziet.beundead.common.*;
import io.github.ziederziet.beundead.config.PerWorldConfig;
import io.github.ziederziet.beundead.config.ServerModConfig;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.networking.UndeadDataPacket;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = BeUndead.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new UndeadTypeDataManager());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.registerCommands(event);
    }

    @SubscribeEvent
    public static void ServerStartingEvent(ServerStartingEvent event){
        PerWorldConfig.load(event.getServer());
    }

    @SubscribeEvent
    public static void ServerStoppingEvent(ServerStoppingEvent event){
        PerWorldConfig.close(event.getServer());
    }

    @SubscribeEvent
    public static void AllowDeathEvent(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntity();
        DamageSource damageSource = event.getSource();

        if (livingEntity.level().isClientSide()) return;

        if (livingEntity instanceof Player player) {
            boolean husks = ServerModConfig.areHusksEnabled();
            boolean drowned = ServerModConfig.areDrownedEnabled();

            boolean respawnTimer = player.getServer().isDedicatedServer();

            if (BeUndeadHelper.isHuman(player)){

                boolean turnZombie = true;

                if (ServerModConfig.getOnlyTurnWhenInfected()){
                    turnZombie = damageSource.is(BeUndead.INFECTION_KILL) || damageSource.getEntity() instanceof Zombie || ((InfectionAccessor)player).getOutInfection() >= BeUndeadConstants.OUT_INFECTION_SHOW;
                }

                if (turnZombie){
                    String type = BeUndead.UNDEAD_DATA.moveType("zombie", BeUndeadHelper.getMoistMovementOnDeath(damageSource, player), BeUndeadHelper.getHeatMovementOnDeath(damageSource, player), husks, drowned, damageSource);

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

                    BeUndeadHelper.setUndeadType(player, type);
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

                    BeUndeadHelper.checkNotSupposedItems(player, !player.isSpectator());

                    event.setCanceled(true);
                }
                else if (respawnTimer) {
                    BeUndeadHelper.setZombieRespawnTimer(player, player.level().getGameTime() + ServerModConfig.getRespawnTimer());
                }
            }
            else {
                BeUndeadHelper.setUndeadType(player, BeUndead.UNDEAD_DATA.moveType(BeUndeadHelper.getUndeadTypeName(player), BeUndeadHelper.getMoistMovementOnDeath(damageSource, player), BeUndeadHelper.getHeatMovementOnDeath(damageSource, player), husks, drowned, damageSource));

                if (respawnTimer) {
                    BeUndeadHelper.setZombieRespawnTimer(player, player.level().getGameTime() + ServerModConfig.getRespawnTimerToZombie());
                }
            }
        }
    }

    @SubscribeEvent
    public static void AfterDeathEvent(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntity();
        DamageSource damageSource = event.getSource();
        if (livingEntity instanceof Villager villager){
            boolean convert = damageSource.is(BeUndead.INFECTION_KILL);
            if (damageSource.getEntity() instanceof Player player && !BeUndeadHelper.isHuman(player)) {
                player.getFoodData().setFoodLevel(Math.min(20, player.getFoodData().getFoodLevel() + 4));
                player.getFoodData().setSaturation(Math.min(20, player.getFoodData().getSaturationLevel() + FoodConstants.saturationByModifier(4, 3F)));

                if (!convert){
                    if (livingEntity.level().getDifficulty() == Difficulty.NORMAL || livingEntity.level().getDifficulty() == Difficulty.HARD){
                        if (!(villager.level().getDifficulty() != Difficulty.HARD && villager.getRandom().nextBoolean())) {
                            convert = true;
                        }
                    }
                }

                if (convert){
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

    @SubscribeEvent
    public static void StartTrackingEntityEvent(PlayerEvent.StartTracking event){
        Entity entity = event.getTarget();
        if (entity instanceof ServerPlayer toTrack){
            event.getEntity().getServer().execute(() -> {
                ModNetworking.sendToClient(UndeadDataPacket.getPacket(toTrack), (ServerPlayer)event.getEntity());
            });
        }
    }

    @SubscribeEvent
    public static void AfterDamageEvent(LivingDamageEvent event){
        LivingEntity livingEntity = event.getEntity();
        DamageSource damageSource = event.getSource();
        if (damageSource.getEntity() instanceof Player player && !BeUndeadHelper.isHuman(player)){
            if (BeUndeadHelper.getUndeadType(player) instanceof SerializizedServerUndeadType type){
                MobEffectInstance[] array = type.mobEffects();
                for (int i = 0; i < array.length; i++) {
                    MobEffectInstance mobEffectInstance = array[i];
                    livingEntity.addEffect(new MobEffectInstance(mobEffectInstance.getEffect(),
                            mobEffectInstance.getDuration(),
                            mobEffectInstance.getAmplifier(),
                            mobEffectInstance.isAmbient(),
                            mobEffectInstance.isVisible(),
                            mobEffectInstance.showIcon()));
                }
            }
        }

        else if (ServerModConfig.isInfectionEnabled() && livingEntity instanceof Villager || (livingEntity instanceof Player player && BeUndeadHelper.isHuman(player))){
            if (damageSource.getEntity() instanceof Zombie || (damageSource.getEntity() instanceof Player playerAttacker && !BeUndeadHelper.isHuman(playerAttacker))){
                if (livingEntity.getRandom().nextBoolean()){
                    Player infecter = damageSource.getEntity() instanceof Player playerAttacker ? playerAttacker : null;
                    BeUndeadHelper.infectBy(livingEntity, infecter, damageSource.getEntity() instanceof ZombifiedPiglin ? 15 : 28);
                }
            }
        }
    }

    @SubscribeEvent
    public static void PlayerCloneEvent(PlayerEvent.Clone event){
        BeUndeadHelper.setUndeadType(event.getEntity(), BeUndeadHelper.getUndeadTypeName(event.getOriginal()), true);
        if (event.getOriginal().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || !event.isWasDeath()){
            BeUndeadHelper.setZombieChest(event.getEntity(), BeUndeadHelper.hasZombieChest(event.getOriginal()), true);
        }

    }

    @SubscribeEvent
    public static void PlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event){
        BeUndeadHelper.sendUndeadPacket(event.getEntity());
    }

    @SubscribeEvent
    public static void AfterRespawnEvent(PlayerEvent.PlayerRespawnEvent event){
        BeUndeadHelper.sendUndeadPacket(event.getEntity());
    }

    @SubscribeEvent
    public static void JoinServerEvent(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getEntity() instanceof ServerPlayer player){
            BeUndeadHelper.checkAndDropChestExtension(player);
            BeUndeadHelper.checkNotSupposedItems(player, !player.isSpectator());

            BeUndeadHelper.sendUndeadPacket(player);

            ModNetworking.sendToClient(ServerModConfig.getPacket(), player);
        }
    }

    @SubscribeEvent
    public static void AllowSleepingEvent(PlayerSleepInBedEvent event){
        Player player = event.getEntity();
        Vec3 vec3 = Vec3.atBottomCenterOf(player.blockPosition());
        List<Player> list = player.level().getEntitiesOfClass(Player.class, new AABB(vec3.x() - 8.0, vec3.y() - 5.0, vec3.z() - 8.0, vec3.x() + 8.0, vec3.y() + 5.0, vec3.z() + 8.0), (player1) -> {
            return !BeUndeadHelper.isHuman(player1);
        });
        if (!list.isEmpty()) {
            event.setResult(Player.BedSleepingProblem.NOT_SAFE);
        }
    }
}