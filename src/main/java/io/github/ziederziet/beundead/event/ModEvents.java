package io.github.ziederziet.beundead.event;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Either;
import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.commands.ModCommands;
import io.github.ziederziet.beundead.fogandredmoon.FogAndRedMoonSavedData;
import io.github.ziederziet.beundead.goal.CreeperMeleeAttackGoal;
import io.github.ziederziet.beundead.goal.CustomNearestAttackablePlayerGoal;
import io.github.ziederziet.beundead.goal.GetAwayFromCreeperGoal;
import io.github.ziederziet.beundead.goal.GetAwayFromExplodingTnt;
import io.github.ziederziet.beundead.mixin.DeathScreenAccessor;
import io.github.ziederziet.beundead.mixin.DeathScreenMixin;
import io.github.ziederziet.beundead.mixin.EntityAccessor;
import io.github.ziederziet.beundead.mixin.NearestAttackableTargetGoalAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.zombie_capability.ZombiePlayerCapabilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
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
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.*;

@Mod.EventBusSubscriber(modid = BeUndead.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onLivingDamageEvent(LivingDamageEvent event){
        if (event.getSource().getEntity() instanceof Player player && BeUndead.getZombieType(player) == 2){
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
        }
    }
//    @SubscribeEvent
//    public static void onAttackEntityEvent(AttackEntityEvent event){
//        if (BeUndead.getZombieType(event.getEntity()) > 0){
//            if (event.getTarget() instanceof Monster){
//                if (!(event.getTarget() instanceof Mob mob && mob.getTarget() == event.getEntity())){
//                    event.setCanceled(true);
//                }
//            }
//        }
//    }

//    @SubscribeEvent
//    public static void onItemTossEvent(net.minecraftforge.event.entity.item.ItemTossEvent event){
//        event.getPlayer().getInventory().selected = 4;
//    }

//    @SubscribeEvent
//    public static void onInputEvent(InputEvent.Key event){
//        Minecraft.getInstance().player.getInventory().selected = 0;
//    }

    @SubscribeEvent
    public static void onLivingEntityUseItemEvent(LivingEntityUseItemEvent event){
        if (event.getDuration() == 0 && event.getEntity() instanceof Player player && BeUndead.getZombieType(player) > 0 && event.getItem().is(Items.GOLDEN_APPLE)){
            if (player.hasEffect(MobEffects.WEAKNESS)){
                BeUndead.startConverting(player, 0, player);
            }
        }
    }

    private static void checkSideItems(Player player){
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

    private static FogAndRedMoonSavedData fogAndRedMoonSavedData;

    @SubscribeEvent
    public static void onLevelTickEvent(TickEvent.LevelTickEvent event){
        if (!event.level.isClientSide() && event.level.dimension() == ServerLevel.OVERWORLD){
//            if (fogAndRedMoonSavedData == null){
//                fogAndRedMoonSavedData = FogAndRedMoonSavedData.getFogAndRedMoonSavedData(((ServerLevel)event.level).getServer());
//            }
//            if (fogAndRedMoonSavedData != null){
//                long gameTime = event.level.getGameTime();
//                long dayTime = event.level.getDayTime();
//
//                boolean fog = false;
//                boolean redMoon = fogAndRedMoonSavedData.getRedMoonTimer() < gameTime;
//
//                if (fogAndRedMoonSavedData.getFogTimer() >= gameTime){
//                    fog = true;
//                }
//                if (fogAndRedMoonSavedData.getRedMoonTimer() >= gameTime){
//                    redMoon = true;
//                }
//                if (dayTime == 6000){
//                    if (event.level.getRandom().nextInt(10) == 0){
//                        fogAndRedMoonSavedData.setRedMoonTimer(gameTime - dayTime + 18000);
//                        redMoon = true;
//                    }
//                }
//                if (fogAndRedMoonSavedData.getFogTimer() + 72000 >= gameTime){
//                    fogAndRedMoonSavedData.setFogTimer(gameTime + 192000 + event.level.getRandom().nextInt(192000));
//                    fog = false;
//                }
//                if (fog){
//                    redMoon = false;
//                }
//                if (redMoon != BeUndead.Mod.getRedMoon() || fog != BeUndead.Mod.getFoggyDay()){
//                    BeUndead.Mod.setFoggyDay(fog);
//                    BeUndead.Mod.setRedmoon(redMoon);
//                    ModNetworking.sendToAllClients(new ClientGetFogAndRedMoonPacket(fog, redMoon));
//                }
////                if (fogAndRedMoonSavedData.getRedMoonTimer() == gameTime){
////
////                }
//            }
        }

        if (event.level.isClientSide() && Minecraft.getInstance().player != null){
            LocalPlayer player = Minecraft.getInstance().player;
            if (player.level().isClientSide()){
                if (Minecraft.getInstance().screen instanceof DeathScreen deathScreen){
                    long respawnTimer = BeUndead.getZombieRespawnTimer(player);
                    long timeTo = respawnTimer - player.level().getGameTime();
                    if (timeTo == 1){
                        ((DeathScreenAccessor)(Object)deathScreen).getExitButtons().getFirst().active = true;
                        ((DeathScreenAccessor)(Object)deathScreen).getExitButtons().getFirst().setMessage(Component.translatable("deathScreen.respawn"));
                    } else if (timeTo > 0 && timeTo % 20 == 0){
                        int minutes = (int)Math.floor(timeTo / 20D / 60D);
                        int seconds = (int)Math.floor(timeTo / 20D % 60D);
                        ((DeathScreenAccessor)(Object)deathScreen).getExitButtons().getFirst().setMessage(Component.translatable("deathScreen.respawn").append(" " + minutes + ":" + (String.valueOf(seconds).length() == 1 ? "0" : "") + seconds));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event){
        Vec3 vec3 = Vec3.atBottomCenterOf(event.getEntity().blockPosition());
        List<Player> list = event.getEntity().level().getEntitiesOfClass(Player.class, new AABB(vec3.x() - 8.0, vec3.y() - 5.0, vec3.z() - 8.0, vec3.x() + 8.0, vec3.y() + 5.0, vec3.z() + 8.0), (p_9062_) -> {
            return BeUndead.getZombieType((Player) p_9062_) > 0;
        });
        if (!list.isEmpty()) {
            event.setResult(Player.BedSleepingProblem.NOT_SAFE);
        }
    }

//    @SubscribeEvent
//    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
//        if (!event.getEntity().level().isClientSide()){
//            FogAndRedMoonSavedData savedData = FogAndRedMoonSavedData.getFogAndRedMoonSavedData(event.getEntity().getServer());
//            boolean fog = savedData.getFogTimer() >= event.getEntity().level().getGameTime();
//            boolean redmoon = savedData.getRedMoonTimer() < event.getEntity().level().getGameTime();
//            ModNetworking.sendToClient(new ClientGetFogAndRedMoonPacket(fog, redmoon), (ServerPlayer) event.getEntity());
//        }
//    }

    @SubscribeEvent
    public static void onLivingTickEvent(LivingEvent.LivingTickEvent event){
        if (event.getEntity() instanceof Player player){

            int type = BeUndead.getZombieType(player);
            if (type > 0){
                int conversionTime = BeUndead.getZombieConversionTime(player);
                if (conversionTime >= 0){
                    if (conversionTime == 1){
                        if (BeUndead.getZombieConversion(player) > 0){
                            BeUndead.setZombieType(player, BeUndead.getZombieConversion(player));
                        } else {
                            BeUndead.revive(player, true);
                        }
                    } else {
                        BeUndead.setZombieConversionTime(player, conversionTime - 1);
                    }
                }

                if (!BeUndead.zombieHasChest(player)){
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
        }
    }

    private static boolean isSunBurnTick(Player player) {
        if (BeUndead.Mod.getFoggyDay()){
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
    public static void onEntityJoinWorld(EntityJoinLevelEvent event){
        if (event.getEntity() instanceof Monster monster){
            Iterator<WrappedGoal> goals = monster.goalSelector.getAvailableGoals().iterator();
            boolean hasItAlready = false;
            while (!hasItAlready && goals.hasNext()){
                hasItAlready = goals.next().getGoal() instanceof GetAwayFromCreeperGoal;
            }

            if (!hasItAlready && !(event.getEntity() instanceof Creeper)){
                monster.goalSelector.addGoal(0, new GetAwayFromCreeperGoal(monster, 6.0F, 1.0F, 1.2F));
            }monster.goalSelector.addGoal(1, new GetAwayFromExplodingTnt(monster, 6.0F, 1.0F, 1.2F));
        }
        if (event.getEntity() instanceof Creeper creeper){
            creeper.goalSelector.removeAllGoals(goal -> goal instanceof MeleeAttackGoal);
            creeper.goalSelector.addGoal(4, new CreeperMeleeAttackGoal(creeper, 1.0, false));
        }

        if (event.getEntity() instanceof WanderingTrader wanderingTrader){
            wanderingTrader.goalSelector.addGoal(1, new AvoidEntityGoal<Player>(wanderingTrader, Player.class, player -> BeUndead.getZombieType((Player) player) > 0,  8.0F, 0.5, 0.5, livingEntity -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)));
        }

        if (event.getEntity() instanceof Mob mob && (!(event.getEntity() instanceof NeutralMob) || event.getEntity() instanceof IronGolem)){ // && !(event.getEntity() instanceof NeutralMob)

            Iterator<WrappedGoal> goals = new HashSet<>(mob.targetSelector.getAvailableGoals()).iterator();

            List<NearestAttackableTargetGoal> listOfToRemove = new ArrayList<>();

            while (goals.hasNext()){
                WrappedGoal wrappedGoal = goals.next();
                if (wrappedGoal != null && wrappedGoal.getGoal() instanceof NearestAttackableTargetGoal nearestAttackableTargetGoal){

                    Class targetType = ((NearestAttackableTargetGoalAccessor)nearestAttackableTargetGoal).getTargetType();
                    if (targetType == Player.class){

                        mob.targetSelector.addGoal(wrappedGoal.getPriority(), CustomNearestAttackablePlayerGoal.copyFrom(nearestAttackableTargetGoal, 0, false));

                        listOfToRemove.add(nearestAttackableTargetGoal);
                    }

                    if (targetType == Mob.class){

                        mob.targetSelector.addGoal(wrappedGoal.getPriority(), CustomNearestAttackablePlayerGoal.copyFrom(nearestAttackableTargetGoal, -1, true));
                        //System.out.print("HELO");
                    }

//                    if (targetType == Zombie.class || targetType == Monster.class){
//
//                        mob.targetSelector.addGoal(wrappedGoal.getPriority(), CustomNearestAttackablePlayerGoal.copyFrom(nearestAttackableTargetGoal, -1, true));
//
//                        //listOfToRemove.add(nearestAttackableTargetGoal);
//                    }
//
//                    if (targetType == Mob.class){
//                        mob.targetSelector.addGoal(wrappedGoal.getPriority(), CustomNearestAttackablePlayerGoal.copyFrom(nearestAttackableTargetGoal, -1, false));
//                    }
                }
            }

            listOfToRemove.forEach(mob.targetSelector::removeGoal);
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnPhantomsEvent(PlayerSpawnPhantomsEvent event){
        if (BeUndead.getZombieType(event.getEntity()) > 0){
            event.setPhantomsToSpawn(0);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){
        BeUndead.setZombieType(event.getEntity(), BeUndead.getZombieType(event.getOriginal()));
        if (event.getOriginal().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || !event.isWasDeath()){
            BeUndead.setZombieChest(event.getEntity(), BeUndead.zombieHasChest(event.getOriginal()));
        }

        event.getOriginal().getCapability(ZombiePlayerCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(ogZombiePlayerCapability -> {
            event.getEntity().getCapability(ZombiePlayerCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
                zombiePlayerCapability.cloneFrom(ogZombiePlayerCapability);
            });
        });
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player){
            event.addCapability(ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "zombie_capability"), new ZombiePlayerCapabilityProvider());
        }
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        if (event.getEntity() instanceof Player player) {
            int type = BeUndead.getZombieType(player);
            if (type == 0){

                boolean turnZombie = true;

                if (turnZombie){
                    type = moveType(1, getTypeMovementOnDeath(event.getSource(), player));

                    if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) || event.getSource().is(DamageTypes.OUTSIDE_BORDER)){
                        BeUndead.setZombieType(player, moveType(type, getTypeMovementOnDeath(event.getSource(), player)));

                        BeUndead.setZombieRespawnTimer(player, player.level().getGameTime() + ((long) player.level().getGameRules().getInt(BeUndead.RULE_RESPAWN_TIMER_ON_ZOMBIE) * 20 * 60));
                    } else {
                        event.setCanceled(true);

                        player.setHealth(20F);
                        player.getFoodData().setFoodLevel(20);
                        player.getFoodData().setSaturation(20F);

                        ((ServerLevel)player.level()).getServer().sendSystemMessage(player.getCombatTracker().getDeathMessage());

                        if (!player.isSpectator() && !player.level().isClientSide()){
                            player.captureDrops(new ArrayList());
                            DamageSource pDamageSource = event.getSource();

                            if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                                for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                                    ItemStack itemstack = player.getInventory().getItem(i);
                                    if (!itemstack.isEmpty() && EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                                        player.getInventory().removeItemNoUpdate(i);
                                    }
                                }
                                player.getInventory().dropAll();
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
                        }

                        List<Mob> allEntities = player.level().getEntitiesOfClass(Mob.class, new AABB(player.blockPosition()).inflate(64D, 32D, 64D));

                        for (int i = 0; i < allEntities.size(); i++) {
                            allEntities.get(i).setTarget(null);
                        }

                        BeUndead.setZombieType(player, type);
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
                    }
                }
                else {
                    BeUndead.setZombieRespawnTimer(player, player.level().getGameTime() + ((long) player.level().getGameRules().getInt(BeUndead.RULE_RESPAWN_TIMER) * 20));
                }


            }
            else {
                // SET NEW ZOMBIE TYPE
                BeUndead.setZombieType(player, moveType(type, getTypeMovementOnDeath(event.getSource(), player)));

                BeUndead.setZombieRespawnTimer(player, player.level().getGameTime() + ((long) player.level().getGameRules().getInt(BeUndead.RULE_RESPAWN_TIMER_ON_ZOMBIE) * 20));
            }
        }

        if (event.getEntity() instanceof Villager){
            if (event.getSource().getEntity() instanceof Player player && BeUndead.getZombieType(player) > 0){
                player.getFoodData().setFoodLevel(Math.min(20, player.getFoodData().getFoodLevel() + 4));
                player.getFoodData().setSaturation(Math.min(20, player.getFoodData().getSaturationLevel() + FoodConstants.saturationByModifier(4, 3F)));

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
        if (damageSource.is(DamageTypes.DROWN)){
            return -1;
        } else if (damageSource.is(DamageTypes.WITHER)) {
            return 1;
        } else {
            if (entity.isUnderWater()){
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
    private static int moveType(int type, int movement){
        while (movement > 0 && type != 3){
            movement++;
            if (type == 2){
                type = 1;
            } else if (type == 1){
                type = 3;
                break;
            }
        }
        while (movement < 0 && type != 2){
            movement--;
            if (type == 3){
                type = 1;
            } else if (type == 1){
                type = 2;
                break;
            }
        }
        return type;
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event){
        if (BeUndead.getZombieType(event.getEntity()) > 0){
            event.setNewSpeed(event.getNewSpeed() / 3);
        }
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event){
        ModCommands.registerCommand(event);
    }
}
