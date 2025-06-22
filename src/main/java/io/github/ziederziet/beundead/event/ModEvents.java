package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.commands.ModCommands;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.networking.UndeadDataPacket;
import io.github.ziederziet.beundead.theyre_coming.TheyreComingAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = BeUndead.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onMobEffectEventAdded(MobEffectEvent.Added event){
        if (event.getEntity() instanceof InfectionAccessor infectionAccessor){
            infectionAccessor.infectBy(null, 30, 30);
        }
    }

    @SubscribeEvent
    public static void onLivingDamageEvent(LivingDamageEvent event){
        if (event.getSource().getEntity() instanceof Player player && BeUndeadApi.getZombieType(player) == 2){
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
        }

        if (event.getEntity() instanceof Villager || (event.getEntity() instanceof Player player && BeUndeadApi.getZombieType(player) <= 0)){
            if (event.getSource().getEntity() instanceof Zombie || (event.getSource().getEntity() instanceof Player playerAttacker && BeUndeadApi.getZombieType(playerAttacker) > 0)){

                if (event.getEntity().getRandom().nextBoolean()){
                    @Nullable Player infecter = event.getSource().getEntity() instanceof Player playerAttacker ? playerAttacker : null;
                    ((InfectionAccessor)event.getEntity()).infectBy(infecter, event.getSource().getEntity() instanceof ZombifiedPiglin ? 15 : 28, 0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEntityUseItemEvent(LivingEntityUseItemEvent event) {
        if (event.getDuration() == 0 && event.getEntity() instanceof Player player && BeUndeadApi.getZombieType(player) > 0 && event.getItem().is(BeUndead.UNDEAD_CURES)){
            if (player.hasEffect(MobEffects.WEAKNESS)){
                BeUndeadApi.startConverting(player, 0, player);
            }
        }
    }

    private static void checkSideItems(Player player) {
        boolean emptyFirstSlot = player.getInventory().items.get(4).isEmpty();
        for (int i = 0; i < 9; i++) {
            if (i != 4){
                if (!player.getInventory().items.get(i).isEmpty()){
                    if (emptyFirstSlot){
                        player.getInventory().items.set(4, player.getInventory().items.get(i));
                        player.getInventory().items.set(i, ItemStack.EMPTY);
                        emptyFirstSlot = false;
                    } else {
                        player.drop(player.getInventory().items.get(i), true);
                        player.getInventory().items.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event){
        Vec3 vec3 = Vec3.atBottomCenterOf(event.getEntity().blockPosition());
        List<Player> list = event.getEntity().level().getEntitiesOfClass(Player.class, new AABB(vec3.x() - 8.0, vec3.y() - 5.0, vec3.z() - 8.0, vec3.x() + 8.0, vec3.y() + 5.0, vec3.z() + 8.0), (player) -> {
            return BeUndeadApi.getZombieType(player) > 0;
        });
        if (!list.isEmpty()) {
            event.setResult(Player.BedSleepingProblem.NOT_SAFE);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        if (!event.getEntity().level().isClientSide()){
            ConfigAccessor config = ConfigAccessor.getConfig();

            if (!config.getZombieCanChestExtension()){
                if (BeUndeadApi.hasZombieChest(event.getEntity())){
                    event.getEntity().drop(new ItemStack(Items.CHEST), true, false);
                    BeUndeadApi.setZombieChest(event.getEntity(), false);
                }
            }

            BeUndeadApi.sendUndeadPacket(event.getEntity());

            ModNetworking.sendToClient(ConfigAccessor.getPacket(), (ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event){
        if (event.getTarget() instanceof Player toTrack && !toTrack.level().isClientSide()){
            ModNetworking.sendToClient(UndeadDataPacket.getPacket(toTrack), (ServerPlayer) event.getEntity());
        }
    }

//    @SubscribeEvent
//    public static void onStopTracking(PlayerEvent.StopTracking event){
//
//    }

    @SubscribeEvent
    public static void onLivingTickEvent(LivingEvent.LivingTickEvent event){
        boolean human = event.getEntity() instanceof Villager;
        if (event.getEntity() instanceof Player player){
            int type = BeUndeadApi.getZombieType(player);

            if (type > 0){
                if (ConfigAccessor.getConfig().getZombieNightVision()){
                    MobEffectInstance currentEffect = player.getEffect(MobEffects.NIGHT_VISION);
                    if (currentEffect == null || currentEffect.getDuration() < 61){
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, false, false));
                    }
                }

                if (event.getEntity() instanceof InfectionAccessor infectionAccessor){
                    infectionAccessor.tick(event.getEntity());
                }

                long conversionTime = BeUndeadApi.getZombieConversionTime(player);
                if (conversionTime >= 0){
                    if (conversionTime == 1){
                        if (BeUndeadApi.getZombieConversionType(player) > 0){
                            BeUndeadApi.setZombieType(player, BeUndeadApi.getZombieConversionType(player));
                        } else {
                            BeUndeadApi.revive(player, true);
                        }
                    }
                }

                if (BeUndeadApi.getInvStateOfPlayer(player) == 0){
                    checkSideItems(player);
                    player.getInventory().selected = 4;
                }

                if (type != 2 && isSunBurnTick(player)){
                    ItemStack itemstack = player.getItemBySlot(EquipmentSlot.HEAD);
                    if (!itemstack.isEmpty()) {
                        if (itemstack.isDamageableItem()) {
                            itemstack.hurtAndBreak(player.getRandom().nextInt(2), player, EquipmentSlot.HEAD);
//                            Item item = itemstack.getItem();
//                            itemstack.setDamageValue(itemstack.getDamageValue() + player.getRandom().nextInt(2));
//                            if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
//                                player.onEquippedItemBroken(item, EquipmentSlot.HEAD);
//                                player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
//                            }
                        }
                    }
                    else {
                        player.igniteForSeconds(8.0F);
                    }
                }
            }
            else {
                human = true;
            }
        }

        if (human){
            LivingEntity entity = event.getEntity();
            if (entity.level().getRandom().nextInt(81) == 0) {
                double zombieRange = 16D;

                AABB box = new AABB(
                        entity.getX() - zombieRange,
                        entity.getEyeY() - zombieRange / 2D,
                        entity.getZ() - zombieRange,
                        entity.getX() + zombieRange,
                        entity.getEyeY() + zombieRange / 2D,
                        entity.getZ() + zombieRange);

                int zombieAroundCount = entity.level().getEntitiesOfClass(Zombie.class, box)
                        .size();

                int zombiePiglinAroundCount = entity.level().getEntitiesOfClass(ZombifiedPiglin.class, box)
                        .size();

                zombieAroundCount += entity.level().getEntitiesOfClass(Player.class, box, player1 -> BeUndeadApi.getZombieType(player1) > 0)
                        .size();

                int finalZombieAroundCount = Math.round(zombieAroundCount - zombiePiglinAroundCount / 2F);

                if (entity.hasEffect(BeUndead.INFECTED_EFFECT.getHolder().get())){
                    ((InfectionAccessor)entity).infectBy(null, finalZombieAroundCount, 0);
                }
                else {
                    ((InfectionAccessor)entity).infectBy(null, finalZombieAroundCount, 20);
                }
            }
            if (event.getEntity() instanceof InfectionAccessor infectionAccessor){
                infectionAccessor.tick(event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onCriticalHitEvent(CriticalHitEvent event){
        if (BeUndeadApi.getZombieType(event.getEntity()) > 0 && !ConfigAccessor.getConfig().getZombieCanCrit()){
            event.setResult(Event.Result.DENY);
        }
    }

    private static boolean isSunBurnTick(Player player) {
        if (player.level().isClientSide() || TheyreComingAccessor.getPhase(player.getServer()) == 2){
            return false;
        }
        if (player.level().isDay() && !player.level().isClientSide) {
            float f = player.getLightLevelDependentMagicValue();
            BlockPos blockpos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
            boolean flag = player.isInWaterRainOrBubble() || player.isInPowderSnow || player.wasInPowderSnow;
            if (f > 0.5F && player.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && player.level().canSeeSky(blockpos)) {
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event){
        if (event.getEntity() instanceof WanderingTrader wanderingTrader){
            wanderingTrader.goalSelector.addGoal(1, new AvoidEntityGoal<Player>(wanderingTrader, Player.class, player -> BeUndeadApi.getZombieType((Player) player) > 0,  8.0F, 0.5, 0.5, livingEntity -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)));
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnPhantomsEvent(PlayerSpawnPhantomsEvent event){
        if (BeUndeadApi.getZombieType(event.getEntity()) > 0){
            event.setPhantomsToSpawn(0);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){
        BeUndeadApi.setZombieType(event.getEntity(), BeUndeadApi.getZombieType(event.getOriginal()));
        if (event.getOriginal().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || !event.isWasDeath()){
            BeUndeadApi.setZombieChest(event.getEntity(), BeUndeadApi.hasZombieChest(event.getOriginal()));
        }

        BeUndeadApi.sendUndeadPacket(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        if (event.getEntity() instanceof Player player) {
            ConfigAccessor config = ConfigAccessor.getConfig();
            boolean husks = config.areHusksEnabled();
            boolean drowned = config.areDrownedEnabled();

            boolean respawnTimer = player.getServer().isDedicatedServer();

            int type = BeUndeadApi.getZombieType(player);
            if (type == 0){

                boolean turnZombie = true;

                if (turnZombie){
                    type = moveType(1, getTypeMovementOnDeath(event.getSource(), player), husks, drowned);

                    if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) || event.getSource().is(DamageTypes.OUTSIDE_BORDER)){
                        BeUndeadApi.setZombieType(player, moveType(type, getTypeMovementOnDeath(event.getSource(), player), husks, drowned));

                        if (respawnTimer){
                            BeUndeadApi.setZombieRespawnTimer(player, player.level().getGameTime() + config.getRespawnTimer());
                        }
                    } else {
                        event.setCanceled(true);

                        player.setHealth(20F);
                        player.getFoodData().setFoodLevel(20);
                        player.getFoodData().setSaturation(20F);

                        ((ServerLevel)player.level()).getServer().sendSystemMessage(player.getCombatTracker().getDeathMessage());

                        player.captureDrops(new ArrayList());
                        DamageSource pDamageSource = event.getSource();

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
                                int reward = ForgeEventFactory.getExperienceDrop(player, null, player.getExperienceReward(serverlevel, pDamageSource.getEntity()));
                                ExperienceOrb.award(serverlevel, player.position(), reward);
                                player.totalExperience = 0;
                                player.experienceLevel = 0;
                                player.experienceProgress = 0;
                            }
                        }
                        Collection<ItemEntity> drops = player.captureDrops((Collection)null);
                        if (!ForgeEventFactory.onLivingDrops(player, pDamageSource, drops, true)) {
                            drops.forEach((e) -> {
                                player.level().addFreshEntity(e);
                            });
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
                    }
                }
                else if (respawnTimer) {
                    BeUndeadApi.setZombieRespawnTimer(player, player.level().getGameTime() + config.getRespawnTimer());
                }
            }
            else {
                BeUndeadApi.setZombieType(player, moveType(type, getTypeMovementOnDeath(event.getSource(), player), husks, drowned));

                if (respawnTimer) {
                    BeUndeadApi.setZombieRespawnTimer(player, player.level().getGameTime() + config.getRespawnTimerToZombie());
                }
            }
        }

        if (event.getEntity() instanceof Villager){
            boolean convert = event.getSource().is(BeUndead.INFECTION_KILL);
            if (!convert && event.getSource().getEntity() instanceof Player player && BeUndeadApi.getZombieType(player) > 0) {
                convert = true;
                player.getFoodData().setFoodLevel(Math.min(20, player.getFoodData().getFoodLevel() + 4));
                player.getFoodData().setSaturation(Math.min(20, player.getFoodData().getSaturationLevel() + FoodConstants.saturationByModifier(4, 3F)));
            }
            if (convert){
                if ((event.getEntity().level().getDifficulty() == Difficulty.NORMAL || event.getEntity().level().getDifficulty() == Difficulty.HARD) && event.getEntity() instanceof Villager villager){
                    if (!(villager.level().getDifficulty() != Difficulty.HARD && villager.getRandom().nextBoolean())) {
                        if (ForgeEventFactory.canLivingConvert(villager, EntityType.ZOMBIE_VILLAGER, (timer) -> {
                        })) {
                            ZombieVillager zombievillager = (ZombieVillager)villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                            if (zombievillager != null) {
                                zombievillager.finalizeSpawn((ServerLevelAccessor) villager.level(), villager.level().getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true));
                                zombievillager.setVillagerData(villager.getVillagerData());
                                zombievillager.setGossips((Tag)villager.getGossips().store(NbtOps.INSTANCE));
                                zombievillager.setTradeOffers(villager.getOffers().copy());
                                zombievillager.setVillagerXp(villager.getVillagerXp());
                                ForgeEventFactory.onLivingConvert(villager, zombievillager);
                                if (!villager.isSilent()) {
                                    villager.level().levelEvent((Player)null, 1026, villager.blockPosition(), 0);
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    // MOVEMENT UP -> HUSK
    // MOVEMENT DOWN -> DROWNED
    private static int getTypeMovementOnDeath(DamageSource damageSource, LivingEntity entity){
        if (damageSource.is(DamageTypes.DROWN) || damageSource.is(DamageTypes.TRIDENT)){
            return -1;
        } else if (damageSource.is(DamageTypes.WITHER)) {
            return 1;
        } else {
            if (entity.isInWater()){
                return -1;
            } else {
                boolean hasDesert = true;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        hasDesert = entity.level().getBlockState(new BlockPos(entity.getBlockX() + x, (int)Math.floor(entity.getY() - 0.8D), entity.getBlockZ() + z)).is(BlockTags.SAND);
                        if (!hasDesert){
                            break;
                        }
                    }
                }
                if (hasDesert){
                    return 1;
                }
            }
        }
        return 0;
    }

    // MOVEMENT UP -> HUSK
    // MOVEMENT DOWN -> DROWNED
    private static int moveType(int type, int movement, boolean husks, boolean drowned){
        while (movement < 0 && type != 3){
            movement++;
            if (type == 2){
                type = 1;
            } else if (type == 1 && drowned){
                type = 3;
                break;
            }
            else {
                break;
            }
        }
        while (movement > 0 && type != 2){
            movement--;
            if (type == 3){
                type = 1;
            } else if (type == 1 && husks){
                type = 2;
                break;
            }
            else {
                break;
            }
        }
        return type;
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event){
        if (BeUndeadApi.getZombieType(event.getEntity()) > 0){
            event.setNewSpeed(event.getNewSpeed() / 3);
        }
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        ModCommands.registerCommand(event);
    }
}
