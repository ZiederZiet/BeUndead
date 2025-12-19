package io.github.ziederziet.beundead.common;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.config.ServerConfigAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.networking.RespawnTimerPacket;
import io.github.ziederziet.beundead.networking.UndeadDataPacket;
import io.github.ziederziet.beundead.theyre_coming.TheyreComingAccessor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BeUndeadHelper {
    public static boolean isHuman(Player player) {
        return getUndeadTypeName(player).isBlank();
    }

    public static UndeadType getUndeadType(Player player) {
        if (player.level().isClientSide()){
            return getClientUndeadType(getUndeadTypeName(player));
        }
        else {
            String typeName = getUndeadTypeName(player);
            UndeadType type = getServerUndeadType(getUndeadTypeName(player));
            if (!typeName.isBlank() && type == null){
                setUndeadType(player, "");
            }
            return type;
        }
    }

    public static UndeadType getClientUndeadType(String typeName) {
        return ClientInfo.undeadTypes.get(typeName);
    }

    public static UndeadType getServerUndeadType(String typeName) {
        return BeUndead.UNDEAD_DATA.get(typeName);
    }

    public static String getUndeadTypeName(Player player) {
        return ((UndeadAccessor)player).getType();
    }

    public static void setUndeadType(Player player, String type, boolean packet) {
        ((UndeadAccessor)player).setType(type);

        if (player instanceof ServerPlayer serverPlayer){
            if (type.isBlank()){
                BeUndeadApi.firePlayerRevived(serverPlayer);
            }
            else {
                BeUndeadApi.firePlayerTurnedUndead(serverPlayer, type);
            }
        }

        if (type.isBlank()){
            checkAndDropChestExtension(player);
        }

        if (packet){
            sendUndeadPacket(player);
        }

        BeUndeadHelper.setZombieRespawnTimer(player, -1, packet);
    }

    public static void setUndeadType(Player player, String type) {
        setUndeadType(player, type, true);
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
        if (getUndeadType(player) instanceof ClientUndeadType type){
            return type.hurtSound();
        }
        return null;
    }

    public static SoundEvent getDeathSound(Player player){
        if (getUndeadType(player) instanceof ClientUndeadType type){
            return type.deathSound();
        }
        return null;
    }

    public static void playAmbientSound(Player player){
        if (getUndeadType(player) instanceof ClientUndeadType type){
            player.makeSound(type.ambientSound());
        }
    }

    public static void playStepSound(Player player){
        if (getUndeadType(player) instanceof ClientUndeadType type){
            player.makeSound(type.stepSound());
        }
    }

    public static double getWalkingSpeed(Player player){
        if (BeUndeadHelper.isHuman(player)) return 1D;
        if (player.level().isClientSide()){
            return ClientInfo.zombieWalkingSpeed;
        }
        return ServerConfigAccessor.getConfig().getZombieWalkSpeed();
    }

    public static boolean canJump(Player player){
        if (BeUndeadHelper.isHuman(player)) return true;
        UndeadType type = BeUndeadHelper.getUndeadType(player);
        if (type == null) return true;
        if (type.canSwimInWater() && player.isInWater()) return true;
        if (player.level().isClientSide()){
            return !player.isInWater() && ClientInfo.zombieJumpOnTheirOwn;
        }
        else {
            return !player.isInWater() && ServerConfigAccessor.getConfig().getZombieJumpOnTheirOwn();
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

        if (!has){
            checkNotSupposedItems(player, !player.isSpectator());
        }
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
        BeUndeadHelper.setUndeadType(player, "");
        if (BeUndeadHelper.hasZombieChest(player)){
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

    public static void startConverting(Player player, Player starter){
        if (!isHuman(player)){
            if (ServerConfigAccessor.getConfig().getCureRequirements() > 1){
                player.removeEffect(MobEffects.WEAKNESS);
            }

            if (starter != null){
                ((UndeadAccessor)player).setConversionStarter(starter.getUUID());
            }

            int conversionTime = player.getRandom().nextInt(2401) + 3600;
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, conversionTime, Math.min(player.level().getDifficulty().getId() - 1, 0)));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1F, 1F);
            setZombieConversionTime(player, conversionTime);
        }
    }

    public static void stopConverting(Player player){
        if (!isHuman(player)){
            ((UndeadAccessor)player).setConversionStarter(null);

            BeUndeadHelper.setZombieConversionTime(player, 0);
        }
    }

    public static void checkNotSupposedItems(Player player, boolean drop) {
        int invState = BeUndeadHelper.getInvStateOfPlayer(player);

        if (invState < 2){
            for (int i = 9; i < player.getInventory().items.size(); i++) {
                if (drop){
                    player.drop(player.getInventory().items.get(i), true, false);
                }

                player.getInventory().items.set(i, ItemStack.EMPTY);
            }
        }
        if (invState < 1){
            player.getInventory().selected = 4;

            checkSideItems(player);
        }
    }

    public static void checkSideItems(Player player){
        for (int i = 0; i < 9; i++) {
            if (i != 4){
                if (!player.getInventory().items.get(i).isEmpty()){
                    player.drop(player.getInventory().items.get(i).copyAndClear(), true, false);
                }
            }
        }
    }

    public static void checkAndDropChestExtension(Player player){
        if (BeUndeadHelper.hasZombieChest(player)){
            if (!ServerConfigAccessor.getConfig().getZombieCanChestExtension() || BeUndeadHelper.isHuman(player)){
                BeUndeadHelper.setZombieChest(player, false);
                player.drop(new ItemStack(Items.CHEST, 1), true, false);
            }
        }
    }

    public static void checkNotSupposedItemsAndDropChestExtensionForAllPlayers(){
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null){
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                checkAndDropChestExtension(serverPlayer);
                checkNotSupposedItems(serverPlayer, !serverPlayer.isSpectator());
            });
        }
    }

    public static int getInvStateOfPlayer(Player player){
        if (BeUndeadHelper.isHuman(player)){
            return 2;
        }

        boolean hasChest = hasZombieChest(player);
        if (player.level().isClientSide()){
            return Math.min(ClientInfo.zombieInvState + (hasChest && ClientInfo.canChestExtension ? 1 : 0), 2);
        }
        else{
            ServerConfigAccessor config = ServerConfigAccessor.getConfig();
            return Math.min(config.getZombieInvState() + (hasChest && config.getZombieCanChestExtension() ? 1 : 0), 2);
        }
    }

    public static int getMoistMovementOnDeath(DamageSource damageSource, LivingEntity entity){
        if (damageSource.is(DamageTypes.DROWN) || damageSource.is(DamageTypes.TRIDENT)){
            return 1;
        } else if (damageSource.is(DamageTypes.WITHER)) {
            return -1;
        } else {
            if (entity.isInWater()){
                return 1;
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
                    return -1;
                }
            }
        }
        return 0;
    }

    public static int getHeatMovementOnDeath(DamageSource damageSource, LivingEntity entity){
        if (damageSource.is(DamageTypes.LAVA) || entity.level().dimensionType().ultraWarm()){
            return 1;
        } else if (damageSource.is(DamageTypes.FREEZE)) {
            return -1;
        }
        return 0;
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

    public static BlockPos getOverworldRespawnPosForUndead(ServerLevel serverLevel, int i, int j) {
        boolean bl = serverLevel.dimensionType().hasCeiling();
        LevelChunk levelChunk = serverLevel.getChunk(SectionPos.blockToSectionCoord(i), SectionPos.blockToSectionCoord(j));
        int k = bl ? serverLevel.getChunkSource().getGenerator().getSpawnHeight(serverLevel) : levelChunk.getHeight(Heightmap.Types.MOTION_BLOCKING, i & 15, j & 15);
        if (k < serverLevel.getMinBuildHeight()) {
            return null;
        } else {
            int l = levelChunk.getHeight(Heightmap.Types.WORLD_SURFACE, i & 15, j & 15);
            if (l <= k && l > levelChunk.getHeight(Heightmap.Types.OCEAN_FLOOR, i & 15, j & 15)) {
                return null;
            } else {
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                for(int m = k + 1; m >= serverLevel.getMinBuildHeight(); --m) {
                    mutableBlockPos.set(i, m, j);
                    BlockState blockState = serverLevel.getBlockState(mutableBlockPos);
                    if (!blockState.getFluidState().isEmpty()) {
                        break;
                    }

                    if (m < k && Block.isFaceFull(blockState.getCollisionShape(serverLevel, mutableBlockPos), Direction.UP)) {
                        return mutableBlockPos.above().immutable();
                    }
                }

                return null;
            }
        }
    }


    public static void infectBy(LivingEntity infected, Player by, int amount){
        if (ServerConfigAccessor.getConfig().isInfectionEnabled()){
            ((InfectionAccessor)infected).infectBy(by, amount);
        }
    }

    public static void infectTick(LivingEntity infected){
        if (ServerConfigAccessor.getConfig().isInfectionEnabled()){
            InfectionAccessor accessor = (InfectionAccessor)infected;
            int inInfection = accessor.getInInfection() - BeUndeadConstants.IN_INFECTION_TO_OUTER_THRESHOLD;
            if (inInfection >= 0){
                float chance = 0.2F + Math.min((float) inInfection / BeUndeadConstants.MAX_IN_INFECTION, 1) * 0.8F;
                int out = accessor.getOutInfection();
                if (infected.getRandom().nextFloat() < chance){
                    out += 1;
                    accessor.setOutInfection(out);
                }

                if (out > BeUndeadConstants.OUT_INFECTION_SHOW){
                    if (!infected.hasEffect(BeUndead.INFECTED_EFFECT.getHolder().get())){
                        showInfection(infected);
                    }

                    if (ServerConfigAccessor.getConfig().getForceTurnWhenInfected() && infected.getRandom().nextBoolean()){
                        int infKill = accessor.getInfectionKillTicks() + 1;
                        accessor.setInfectionKillTicks(infKill);

                        if (infKill >= BeUndeadConstants.INFECTION_KILL_TICKS){
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

        if (ServerConfigAccessor.getConfig() != null && ServerConfigAccessor.getConfig().isInfectionEnabled()){
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
            ((LivingEntity)infectionAccessor).removeEffect(BeUndead.INFECTED_EFFECT.getHolder().get());
        }
    }

    public static void infectionAddAdditionalSaveData(CompoundTag tag, InfectionAccessor infectionAccessor, HashMap<UUID, Integer> map){
        if (ServerConfigAccessor.getConfig() == null || ServerConfigAccessor.getConfig().isInfectionEnabled()){
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
            infectionAccessor.setInInfection(Math.max(infectionAccessor.getInInfection(), BeUndeadConstants.IN_INFECTION_TO_OUTER_THRESHOLD));
            infectionAccessor.setOutInfection(Math.max(infectionAccessor.getOutInfection(), BeUndeadConstants.OUT_INFECTION_SHOW));
        }
    }

    public static void showInfection(LivingEntity livingEntity){
        livingEntity.addEffect(new MobEffectInstance(BeUndead.INFECTED_EFFECT.getHolder().get(), -1, 0));
    }
}
