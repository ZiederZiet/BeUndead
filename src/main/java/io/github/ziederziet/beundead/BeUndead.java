package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.commands.ModCommands;
import io.github.ziederziet.beundead.mixin.EntityAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.zombie_capability.ZombiePlayerCapabilityProvider;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    public static final GameRules.Key<GameRules.IntegerValue> RULE_RESPAWN_TIMER = GameRules.register("respawnTimer", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0));
    //public static final GameRules.Key<GameRules.BooleanValue> RULE_ALWAYS_TO_ZOMBIE = GameRules.register("alwaysToZombie", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> RULE_RESPAWN_TIMER_ON_ZOMBIE = GameRules.register("respawnTimerOnZombie", GameRules.Category.PLAYER, GameRules.IntegerValue.create(180));
    //public static final GameRules.Key<GameRules.BooleanValue> RULE_INFECTION = GameRules.register("infection", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));

    public static BeUndead Mod;

    public static final Vector3f[] ZOMBIE_COLORS;
    public static final Vector3f[] ZOMBIE_COLOR_OFFSETS;

    public static final String MODID = "beundead";

    public BeUndead()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

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

    public boolean getFoggyDay(){
        return false;
    }

    public boolean getRedMoon(){
        return false;
    }

    public static void setZombieType(Player player, int type){
        setZombieConversionTime(player, -1);
        setZombieConversion(player, 0);
        SynchedEntityData entityData = ((EntityAccessor)player).getEntityData();
        entityData.set(DATA_ZOMBIE, type);
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
        player.removeAllEffects();
        player.setHealth(4F);
        setZombieChest(player, false);
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2000, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        player.experienceLevel = (int)Math.floor(player.experienceLevel * 0.3D);
    }

    public static void startConverting(Player player, int toType, Player starter){
        if (getZombieType(player) > 0){
            player.getCapability(ZombiePlayerCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
                zombiePlayerCapability.setConversionStarter(starter.getUUID());
            });

            setZombieConversion(player, toType);
            int conversionTime = player.getRandom().nextInt(2401) + 3600;
            setZombieConversionTime(player, conversionTime);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    static {
        ZOMBIE_COLORS = new Vector3f[] { new Vector3f(0.8F, 1.0F, 0.85F), new Vector3f(0.80F, 0.72F, 0.49F), new Vector3f(0.7F, 0.8F, 0.8F) };
        ZOMBIE_COLOR_OFFSETS = new Vector3f[] { new Vector3f(0.0F, 0.1F, 0.0F), new Vector3f(0.03F, 0.05F, 0.0F), new Vector3f(-0.1F, 0.1F, 0.1F) };
    }
}