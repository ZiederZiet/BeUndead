package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.goal.CreeperMeleeAttackGoal;
import io.github.ziederziet.beundead.goal.CustomNearestAttackablePlayerGoal;
import io.github.ziederziet.beundead.goal.GetAwayFromCreeperGoal;
import io.github.ziederziet.beundead.goal.GetAwayFromExplodingTnt;
import io.github.ziederziet.beundead.mixin.DeathScreenMixin;
import io.github.ziederziet.beundead.mixin.NearestAttackableTargetGoalAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = BeUndead.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onAttackEntityEvent(AttackEntityEvent event){
        if (BeUndead.getZombieType(event.getEntity()) > 0){
            if (event.getTarget() instanceof Monster){
                event.setCanceled(true);
            }
        }
    }

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
        if (event.getDuration() == 0 && event.getEntity() instanceof Player player && BeUndead.getZombieType(player) > 0){
            if (player.hasEffect(MobEffects.WEAKNESS)){
                BeUndead.revive(player);
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
                        player.getInventory().items.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTickEvent(LivingEvent.LivingTickEvent event){
        if (event.getEntity() instanceof Player player && BeUndead.getZombieType(player) > 0){
            if (BeUndead.getZombieType(player) > 0){
                checkSideItems(player);

                int respawnTimer = BeUndead.getZombieRespawnTimer(player);
                if (!player.level().isClientSide() && respawnTimer > 0){
                    BeUndead.setZombieRespawnTimer(player, respawnTimer - 1);
                }

                if (player.level().isClientSide() && respawnTimer == 1){
                    if (Minecraft.getInstance().screen instanceof DeathScreen deathScreen) {
                        ((DeathScreenMixin)(Object)deathScreen).setButtonsActive(true);
                    }
                }

//                if (respawnTimer == 5999){
//                    Minecraft.getInstance().setScreen(new ZombieDeathScreen(Component.literal("You can respawn"), false));
//                }

                if (isSunBurnTick(player)){
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

                player.getInventory().selected = 4;
            }
        }
    }

    private static boolean isSunBurnTick(Player player) {
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
//        if (event.getEntity() instanceof Player player){
//            if (BeUndead.getZombieType(player) > 0){
//                NonNullList<ItemStack> newItems = NonNullList.withSize(1, ItemStack.EMPTY);
//                newItems.addAll(player.getInventory().items);
//                player.getInventory().items = newItems;
//            }
//        }

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

        if (event.getEntity() instanceof Mob mob){

            //Set<WrappedGoal> copiedGoals = new HashSet<>(mob.targetSelector.getAvailableGoals());

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
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (BeUndead.getZombieType(player) == 0){
                event.setCanceled(true);

                player.setHealth(20F);
                player.getFoodData().setFoodLevel(20);
                player.getFoodData().setSaturation(20F);

                player.sendSystemMessage(player.getCombatTracker().getDeathMessage());

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

                BeUndead.setZombieType(player, 1);
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
            else {
                BeUndead.setZombieRespawnTimer(player, 6000);
                player.removeAllEffects();
            }
        }

        if (event.getEntity() instanceof Villager villager){
            if (event.getSource().is(DamageTypes.PLAYER_ATTACK) && event.getSource().getEntity() instanceof Player player && BeUndead.getZombieType(player) > 0){
                ZombieVillager newZombieVillager = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, player.level());
                newZombieVillager.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                newZombieVillager.setVillagerData(villager.getVillagerData());
                if (villager.hasCustomName()){
                    newZombieVillager.setCustomName(villager.getCustomName());
                }
                player.level().addFreshEntity(newZombieVillager);
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event){
        if (BeUndead.getZombieType(event.getEntity()) > 0){
            event.setNewSpeed(event.getNewSpeed() / 3);
        }
    }
}
