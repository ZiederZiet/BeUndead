package io.github.ziederziet.beundead.api;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.common.ClientInfo;
import io.github.ziederziet.beundead.common.InfectionAccessor;
import io.github.ziederziet.beundead.common.UndeadAccessor;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.networking.RespawnTimerPacket;
import io.github.ziederziet.beundead.networking.UndeadDataPacket;
import io.github.ziederziet.beundead.theyre_coming.TheyreComingAccessor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BeUndeadApi {
    public static final Vector3f[] ZOMBIE_COLORS = new Vector3f[] { new Vector3f(0.8F, 1.0F, 0.85F), new Vector3f(0.7F, 0.8F, 0.8F), new Vector3f(0.80F, 0.72F, 0.49F) };
    public static final Vector3f[] ZOMBIE_COLOR_OFFSETS = new Vector3f[] { new Vector3f(0.0F, 0.1F, 0.0F), new Vector3f(-0.1F, 0.1F, 0.1F), new Vector3f(0.03F, 0.05F, 0.0F) };
    public static final int IN_INFECTION_TO_OUTER_THRESHOLD = 60;
    public static final int MAX_IN_INFECTION = 800;
    public static final int IN_INFECTION_INSTANT_OUT_AMOUNT = 178;
    public static final int OUT_INFECTION_SHOW = 650;
    public static final int INFECTION_KILL_TICKS = 2000;

    public static int getZombieType(Player player) {
        return ((UndeadAccessor)player).getType();
    }

    public static void setZombieType(Player player, int type, boolean packet) {
        ((UndeadAccessor)player).setType(type);

        if (packet){
            sendUndeadPacket(player);
        }

        BeUndeadApi.setZombieRespawnTimer(player, -1, packet);
    }

    public static void setZombieType(Player player, int type) {
        setZombieType(player, type, true);
    }

    public static void sendUndeadPacket(Player player){
        if (!player.level().isClientSide()){
            ModNetworking.sendToAllTrackingAndSelfClients(UndeadDataPacket.getPacket(player), (ServerPlayer) player);
        }
    }

    public static void sendRespawnTimePacket(Player player){
        if (!player.level().isClientSide()){
            ModNetworking.sendToClient(new RespawnTimerPacket(getZombieRespawnTimer(player)), (ServerPlayer) player);
        }
    }

    public static SoundEvent getHurtSound(Player player){
        return switch (BeUndeadApi.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_HURT;
            case 2 -> SoundEvents.HUSK_HURT;
            default -> SoundEvents.ZOGLIN_HURT;
        };
    }

    public static SoundEvent getDeathSound(Player player){
        return switch (BeUndeadApi.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_DEATH;
            case 2 -> SoundEvents.HUSK_DEATH;
            default -> SoundEvents.ZOGLIN_DEATH;
        };
    }

    public static void playAmbientSound(Player player){
        SoundEvent soundEvents = switch (BeUndeadApi.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_AMBIENT;
            case 2 -> SoundEvents.HUSK_AMBIENT;
            default -> SoundEvents.ZOMBIE_AMBIENT;
        };
        player.makeSound(soundEvents);
    }

    public static void playStepSound(Player player){
        SoundEvent soundEvents = switch (BeUndeadApi.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_STEP;
            case 2 -> SoundEvents.HUSK_STEP;
            default -> SoundEvents.ZOMBIE_STEP;
        };
        player.makeSound(soundEvents);
    }

    public static double getWalkingSpeed(Player player){
        if (BeUndeadApi.getZombieType(player) <= 0) return 1D;
        if (player.level().isClientSide()){
            return ClientInfo.zombieWalkingSpeed;
        }
        return ConfigAccessor.getConfig().getZombieWalkSpeed();
    }

    public static boolean canJump(Player player){
        if (BeUndeadApi.getZombieType(player) <= 0) return true;
        if (BeUndeadApi.getZombieType(player) == 3 && player.isInWater()) return true;
        if (player.level().isClientSide()){
            return !player.isInWater() && ClientInfo.zombieJumpOnTheirOwn;
        }
        else {
            return !player.isInWater() && ConfigAccessor.getConfig().getZombieJumpOnTheirOwn();
        }
    }

    public static boolean hasZombieChest(Player player){
        return ((UndeadAccessor)player).hasZombieChest();
    }
    public static void setZombieChest(Player player, boolean has){
        setZombieChest(player, has, true);
    }
    public static void setZombieChest(Player player, boolean has, boolean packet){
        ((UndeadAccessor)player).setZombieChest(has);

        sendUndeadPacket(player);
    }
    public static long getZombieRespawnTimer(Player player){
        return ((UndeadAccessor)player).getZombieRespawnTimer();
    }
    public static void setZombieRespawnTimer(Player player, long respawnTimer){
        setZombieRespawnTimer(player, respawnTimer, true);
    }
    public static void setZombieRespawnTimer(Player player, long respawnTimer, boolean packet){
        ((UndeadAccessor)player).setZombieRespawnTimer(respawnTimer);

        if (packet){
            sendRespawnTimePacket(player);
        }
    }
    public static long getZombieConversionTime(Player player){
        return ((UndeadAccessor)player).getZombieConversionTime();
    }
    public static void setZombieConversionTime(Player player, long conversionTimer){
        ((UndeadAccessor)player).setZombieConversionTime(conversionTimer);

        sendUndeadPacket(player);
    }
    public static int getZombieConversionType(Player player){
        return ((UndeadAccessor)player).getZombieConversionType();
    }
    public static void setZombieConversionType(Player player, int conversionType){
        ((UndeadAccessor)player).setZombieConversionType(conversionType);
    }
    public static boolean isZombieConverting(Player player){
        if (!player.level().isClientSide()){
            return getZombieConversionTime(player) > 0;
        }
        return ((UndeadAccessor)player).getConverting();
    }

    public static void revive(Player player, boolean fromConversion){
        if (fromConversion){
            player.level().playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1F, 1F);
            UUID conversionStarter = ((UndeadAccessor)player).getConversionStarter();
            if (conversionStarter != null){
                if (player.level() instanceof ServerLevel serverLevel){
                    Player reviver = serverLevel.getPlayerByUUID(conversionStarter);
                    if (reviver != null){
                        CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)reviver, null, null);
                    }
                }
            }
        }
        BeUndeadApi.setZombieType(player, 0);
        if (BeUndeadApi.hasZombieChest(player)){
            player.drop(new ItemStack(Items.CHEST), true, false);
        }
        ((InfectionAccessor)player).removeInfection(player);
        player.removeAllEffects();
        player.setHealth(4F);
        setZombieChest(player, false);
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2000, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        player.experienceLevel = (int)Math.floor(player.experienceLevel * 0.3D);
    }

    public static void startConverting(Player player, int toType, Player starter){
        if (getZombieType(player) > 0 && player.hasEffect(MobEffects.WEAKNESS) && !player.hasEffect(MobEffects.DAMAGE_BOOST)){
            player.removeEffect(MobEffects.WEAKNESS);

            ((UndeadAccessor)player).setConversionStarter(starter.getUUID());

            BeUndeadApi.setZombieConversionType(player, toType);
            int conversionTime = player.getRandom().nextInt(2401) + 3600;
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, conversionTime, Math.min(player.level().getDifficulty().getId() - 1, 0)));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1F, 1F);
            setZombieConversionTime(player, conversionTime);
        }
    }

    public static void checkItemsInNewState(Player player, boolean drop){
        int newInvState = BeUndeadApi.getInvStateOfPlayer(player);
        if (newInvState < 2){
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                if (i > 9){
                    if (drop){
                        player.drop(player.getInventory().items.get(i).copy(), true, false);
                    }
                    player.getInventory().items.get(i).setCount(0);
                }
                else if (i != 4 && newInvState < 1){
                    if (drop){
                        player.drop(player.getInventory().items.get(i).copy(), true, false);
                    }
                    player.getInventory().items.get(i).setCount(0);
                }
            }
        }

    }

    public static int getInvStateOfPlayer(Player player){
        if (BeUndeadApi.getZombieType(player) <= 0){
            return 2;
        }

        boolean hasChest = hasZombieChest(player);
        if (player.level().isClientSide()){
            return Math.min(ClientInfo.zombieInvState + (hasChest && ClientInfo.canChestExtension ? 1 : 0), 2);
        }
        else{
            ConfigAccessor config = ConfigAccessor.getConfig();
            return Math.min(config.getZombieInvState() + (hasChest && config.getZombieCanChestExtension() ? 1 : 0), 2);
        }
    }

    // MOVEMENT UP -> HUSK
    // MOVEMENT DOWN -> DROWNED
    public static int getTypeMovementOnDeath(DamageSource damageSource, LivingEntity entity){
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
    public static int moveType(int type, int movement, boolean husks, boolean drowned){
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

    public static boolean isSunBurnTick(Player player) {
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

    public static void checkSideItems(Player player) {
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





    public static void infectBy(LivingEntity infected, Player by, int amount){
        if (ConfigAccessor.getConfig().isInfectionEnabled()){
            ((InfectionAccessor)infected).infectBy(by, amount);
        }
    }

    public static void infectTick(LivingEntity infected){
        if (ConfigAccessor.getConfig().isInfectionEnabled()){
            InfectionAccessor accessor = (InfectionAccessor)infected;
            int inInfection = accessor.getInInfection() - IN_INFECTION_TO_OUTER_THRESHOLD;
            if (inInfection >= 0){
                float chance = 0.2F + Math.min((float) inInfection / MAX_IN_INFECTION, 1) * 0.8F;
                int out = accessor.getOutInfection();
                if (infected.getRandom().nextFloat() < chance){
                    out += 1;
                    accessor.setOutInfection(out);
                }

                if (out > OUT_INFECTION_SHOW){
                    if (!infected.hasEffect(BeUndead.INFECTED_EFFECT_HOLDER)){
                        showInfection(infected);
                    }

                    if (ConfigAccessor.getConfig().getForceTurnWhenInfected() && infected.getRandom().nextBoolean()){
                        int infKill = accessor.getInfectionKillTicks() + 1;
                        accessor.setInfectionKillTicks(infKill);

                        if (infKill >= INFECTION_KILL_TICKS){
                            UUID mainInfecter = accessor.getMainInfecterUUID();
                            Player mainInfecterPlayer = null;
                            if (mainInfecter != null){
                                mainInfecterPlayer = infected.level().getPlayerByUUID(mainInfecter);
                            }

                            Registry<DamageType> registry = infected.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
                            Optional<Holder.Reference<DamageType>> oDamageType = registry.getHolder(BeUndead.INFECTION_KILL);

                            if (oDamageType.isPresent()){
                                infected.hurt(new DamageSource(oDamageType.get(), mainInfecterPlayer), Float.MAX_VALUE);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void infectionReadAdditionalSaveData(CompoundTag tag, InfectionAccessor infectionAccessor, HashMap<UUID, Integer> map){
        map.clear();

        if (ConfigAccessor.getConfig().isInfectionEnabled()){
            infectionAccessor.setInInfection(tag.getInt("InfectionIn"));
            infectionAccessor.setOutInfection(tag.getInt("InfectionOut"));
            infectionAccessor.setInfectionKillTicks(tag.getInt("InfectionKillTicks"));

            if (tag.contains("InfectionList")){
                ListTag mapListTag = (ListTag) tag.get("InfectionList");

                for (int i = mapListTag.size() - 1; i >= 0; i--) {
                    CompoundTag compoundTag = (CompoundTag) mapListTag.get(i);

                    int amount = compoundTag.getInt("Amount");
                    UUID uuid = compoundTag.getUUID("UUID");

                    map.put(uuid, amount);
                }
            }
        }
        else {
            ((LivingEntity)infectionAccessor).removeEffect(BeUndead.INFECTED_EFFECT_HOLDER);
        }
    }

    public static void infectionAddAdditionalSaveData(CompoundTag tag, InfectionAccessor infectionAccessor, HashMap<UUID, Integer> map){
        if (ConfigAccessor.getConfig().isInfectionEnabled()){
            tag.putInt("InfectionIn", infectionAccessor.getInInfection());
            tag.putInt("InfectionOut", infectionAccessor.getOutInfection());
            tag.putInt("InfectionKillTicks", infectionAccessor.getInfectionKillTicks());

            ListTag mapListTag = new ListTag();

            for (Map.Entry<UUID, Integer> entry : map.entrySet()){
                CompoundTag compoundTag = new CompoundTag();

                compoundTag.putUUID("UUID", entry.getKey());
                compoundTag.putInt("Amount", entry.getValue());

                mapListTag.add(compoundTag);
            }
        }
    }

    public static void addedInfectionEffect(LivingEntity livingEntity){
        if (livingEntity instanceof InfectionAccessor infectionAccessor){
            infectionAccessor.setInInfection(Math.max(infectionAccessor.getInInfection(), IN_INFECTION_TO_OUTER_THRESHOLD));
            infectionAccessor.setOutInfection(Math.max(infectionAccessor.getOutInfection(), OUT_INFECTION_SHOW));
        }
    }

    public static void showInfection(LivingEntity livingEntity){
        livingEntity.addEffect(new MobEffectInstance(BeUndead.INFECTED_EFFECT_HOLDER, -1, 0));
    }
}