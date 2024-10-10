package io.github.ziederziet.beundead;

import io.github.ziederziet.beundead.commands.ModCommands;
import io.github.ziederziet.beundead.mixin.EntityAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
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

@Mod(BeUndead.MODID)
public class BeUndead
{
    public static final EntityDataAccessor<Integer> DATA_ZOMBIE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Long> DATA_ZOMBIE_RESPAWN_TIME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.LONG);
    public static final GameRules.Key<GameRules.IntegerValue> RULE_RESPAWN_TIMER = GameRules.register("respawnTimer", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0));
    public static final GameRules.Key<GameRules.BooleanValue> RULE_ALWAYS_TO_ZOMBIE = GameRules.register("alwaysToZombie", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> RULE_RESPAWN_TIMER_ON_ZOMBIE = GameRules.register("respawnTimerOnZombie", GameRules.Category.PLAYER, GameRules.IntegerValue.create(5));
    public static final GameRules.Key<GameRules.BooleanValue> RULE_INFECTION = GameRules.register("infection", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));

    public static BeUndead Mod;

    public static final Vector3f[] ZOMBIE_COLORS;
    public static final Vector3f[] ZOMBIE_COLOR_OFFSETS;

//    public boolean Fog;
//    public boolean RedMoon;

//    public boolean getFoggyDay(){
//        return Fog;
//    }
//
//    public boolean getRedMoon(){
//        return RedMoon;
//    }

//    public void setFoggyDay(boolean set){
//        this.Fog = set;
//    }
//
//    public void setRedmoon(boolean set){
//        this.RedMoon = set;
//    }

    public boolean getFoggyDay(){
        return false;
    }

    public boolean getRedMoon(){
        return false;
    }

    public void setFoggyDay(boolean set){
    }

    public void setRedmoon(boolean set){
    }

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

    public static void setZombieType(Player player, int type){
        SynchedEntityData entityData = ((EntityAccessor)player).getEntityData();
        entityData.set(DATA_ZOMBIE, type);
    }

    public static int getZombieType(Player player){
        return ((EntityAccessor)player).getEntityData().get(DATA_ZOMBIE);
    }

    public static long getZombieRespawnTimer(Player player){
        return ((EntityAccessor)player).getEntityData().get(DATA_ZOMBIE_RESPAWN_TIME);
    }

    public static void setZombieRespawnTimer(Player player, long zombieRespawnTimer){
        ((EntityAccessor)player).getEntityData().set(DATA_ZOMBIE_RESPAWN_TIME, zombieRespawnTimer);
    }

    public static void revive(Player player){
        setZombieType(player, 0);
        player.removeAllEffects();
        player.setHealth(4F);
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2000, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        player.experienceLevel = (int)Math.floor(player.experienceLevel * 0.3D);
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