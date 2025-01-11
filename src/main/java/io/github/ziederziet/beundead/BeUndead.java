package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.mixin.EntityAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.zombie_capability.InfectionZombieCapabilityProvider;
import io.github.ziederziet.beundead.zombie_capability.ZombiePlayerCapabilityProvider;
import io.github.ziederziet.beundead.zombie_settings.ZombieSettingsSavedData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod(BeUndead.MODID)
public class BeUndead
{
    public static final EntityDataAccessor<Integer> DATA_ZOMBIE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> DATA_ZOMBIE_CHEST = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Long> DATA_ZOMBIE_RESPAWN_TIME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);
    public static final EntityDataAccessor<Integer> DATA_ZOMBIE_CONVERSION_TIME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DATA_ZOMBIE_CONVERSION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    public static BeUndead Mod;
    public int clientZombieInvState;
    public boolean clientCanChestExtension;
    public boolean clientZombieNightVision;
    public boolean clientZombieJumpOnTheirOwn;
    public int clientZombieMaxViewDistance = 4;
    public double clientZombieWalkingSpeed;

    public static final Vector3f[] ZOMBIE_COLORS;
    public static final Vector3f[] ZOMBIE_COLOR_OFFSETS;

    public static final String MODID = "beundead";


    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BeUndead.MODID);

    public static final RegistryObject<MobEffect> INFECTED_EFFECT = MOB_EFFECTS.register("infected",
            () -> new InfectedMobEffect(MobEffectCategory.NEUTRAL, 1784089));

    public static final ResourceKey<DamageType> INFECTION_KILL = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "infection_kill"));


    public BeUndead()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MOB_EFFECTS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        Mod = this;
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

    public static SoundEvent getHurtSound(Player player){
        return switch (BeUndead.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_HURT;
            case 2 -> SoundEvents.HUSK_HURT;
            default -> SoundEvents.ZOGLIN_HURT;
        };
    }

    public static SoundEvent getDeathSound(Player player){
        return switch (BeUndead.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_DEATH;
            case 2 -> SoundEvents.HUSK_DEATH;
            default -> SoundEvents.ZOGLIN_DEATH;
        };
    }

    public static void playAmbientSound(Player player){
        SoundEvent soundEvents = switch (BeUndead.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_AMBIENT;
            case 2 -> SoundEvents.HUSK_AMBIENT;
            default -> SoundEvents.ZOMBIE_AMBIENT;
        };
        player.makeSound(soundEvents);
    }

    public static void playStepSound(Player player){
        SoundEvent soundEvents = switch (BeUndead.getZombieType(player)) {
            case 3 -> SoundEvents.DROWNED_STEP;
            case 2 -> SoundEvents.HUSK_STEP;
            default -> SoundEvents.ZOMBIE_STEP;
        };
        player.makeSound(soundEvents);
    }

    public static double getWalkingSpeed(Player player){
        if (BeUndead.getZombieType(player) <= 0) return 1D;
        if (player.level().isClientSide()){
            return Mod.clientZombieWalkingSpeed;
        }
        return ZombieSettingsSavedData.getZombieSettingsSavedData(((ServerPlayer)player).getServer()).getZombieWalkSpeed();
    }

    public static boolean canJump(Player player){
        if (BeUndead.getZombieType(player) <= 0) return true;
        if (BeUndead.getZombieType(player) == 3 && player.isInWater()) return true;
        if (player.level().isClientSide()){
            return !player.isInWater() && Mod.clientZombieJumpOnTheirOwn;
        }
        else {
            return !player.isInWater() && ZombieSettingsSavedData.getZombieSettingsSavedData(((ServerPlayer)player).getServer()).getZombieJumpOnTheirOwn();
        }
    }

    public boolean isFoggy(){
        return false; // GET FROM OTHER MODS
    }

    public static void setZombieColors(int type){
        float r = ZOMBIE_COLORS[type - 1].x;
        float g = ZOMBIE_COLORS[type - 1].y;
        float b = ZOMBIE_COLORS[type - 1].z;
        float rO = ZOMBIE_COLOR_OFFSETS[type - 1].x;
        float gO = ZOMBIE_COLOR_OFFSETS[type - 1].y;
        float bO = ZOMBIE_COLOR_OFFSETS[type - 1].z;
        setZombieColor("rendertype_zombie_entity_solid", r, g, b, rO, gO, bO);
        setZombieColor("rendertype_zombie_entity_translucent", r, g, b, rO, gO, bO);
    }

    public static void setZombieColor(String name, float r, float g, float b, float rO, float gO, float bO){
        ShaderInstance glintShaderInstance = Minecraft.getInstance().gameRenderer.getShader(name);

        if (glintShaderInstance != null){
            glintShaderInstance.apply();

            glintShaderInstance.getUniform("ZombieColor").set(r, g, b);
            glintShaderInstance.getUniform("ZombieColorOffset").set(rO, gO, bO);

            glintShaderInstance.clear();
        }
    }

    public static void setZombieType(Player player, int type){
        setZombieConversionTime(player, -1);
        setZombieConversion(player, 0);
        SynchedEntityData entityData = ((EntityAccessor)player).getEntityData();
        entityData.set(DATA_ZOMBIE, type);
        if (type != 0){
            player.getCapability(InfectionZombieCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(infectionZombieCapability -> {
                infectionZombieCapability.removeInfection(player);
            });
        }
    }

    public static int getZombieType(Player player){
        return ((EntityAccessor)player).getEntityData().get(DATA_ZOMBIE);
    }

    public static boolean zombieHasChest(Player player){
        return ((EntityAccessor)player).getEntityData().get(DATA_ZOMBIE_CHEST);
    }

    public static void setZombieChest(Player player, boolean chest){
        ((EntityAccessor)player).getEntityData().set(DATA_ZOMBIE_CHEST, chest);
    }

    public static long getZombieRespawnTimer(Player player){
        return ((EntityAccessor)player).getEntityData().get(DATA_ZOMBIE_RESPAWN_TIME);
    }

    public static void setZombieRespawnTimer(Player player, long zombieRespawnTimer){
        ((EntityAccessor)player).getEntityData().set(DATA_ZOMBIE_RESPAWN_TIME, zombieRespawnTimer);
    }

    public static int getZombieConversionTime(Player player){
        return ((EntityAccessor)player).getEntityData().get(DATA_ZOMBIE_CONVERSION_TIME);
    }

    public static void setZombieConversionTime(Player player, int zombieConversionTime){
        ((EntityAccessor)player).getEntityData().set(DATA_ZOMBIE_CONVERSION_TIME, zombieConversionTime);
    }

    public static int getZombieConversion(Player player){
        return ((EntityAccessor)player).getEntityData().get(DATA_ZOMBIE_CONVERSION);
    }

    public static void setZombieConversion(Player player, int type){
        ((EntityAccessor)player).getEntityData().set(DATA_ZOMBIE_CONVERSION, type);
    }

    public static void revive(Player player, boolean fromConversion){
        if (fromConversion){
            player.level().playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS, 1F, 1F);
            player.getCapability(ZombiePlayerCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
                @Nullable UUID conversionStarter = zombiePlayerCapability.getConversionStarter();
                if (conversionStarter != null){
                    if (player.level() instanceof ServerLevel serverLevel){
                        @Nullable Player reviver = serverLevel.getPlayerByUUID(conversionStarter);
                        if (reviver != null){
                            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer)reviver, null, null);
                        }
                    }
                }
            });
        }
        setZombieType(player, 0);
        if (BeUndead.zombieHasChest(player)){
            player.drop(new ItemStack(Items.CHEST), true, false);
        }
        player.getCapability(InfectionZombieCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
            zombiePlayerCapability.removeInfection(player);
        });
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

            player.getCapability(ZombiePlayerCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
                zombiePlayerCapability.setConversionStarter(starter.getUUID());
            });

            setZombieConversion(player, toType);
            int conversionTime = player.getRandom().nextInt(2401) + 3600;
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, conversionTime, Math.min(player.level().getDifficulty().getId() - 1, 0)));
            player.level().playSound(null, player.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1F, 1F);
            setZombieConversionTime(player, conversionTime);
        }
    }

    public static void checkItemsInNewState(Player player, boolean drop){
        int newInvState = BeUndead.getInvStateOfPlayer(player);
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
        if (BeUndead.getZombieType(player) <= 0){
            return 2;
        }

        boolean hasChest = zombieHasChest(player);
        if (player.level().isClientSide()){
            return Math.min(Mod.clientZombieInvState + (hasChest && Mod.clientCanChestExtension ? 1 : 0), 2);
        }
        else{
            ZombieSettingsSavedData savedData = ZombieSettingsSavedData.getZombieSettingsSavedData(((ServerPlayer)player).getServer());
            return Math.min(savedData.getInvState() + (hasChest && savedData.canChestExtension() ? 1 : 0), 2);
        }
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