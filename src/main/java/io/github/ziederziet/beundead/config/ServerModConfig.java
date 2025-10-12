package io.github.ziederziet.beundead.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//@Config(name = BeUndead.MODID)
public class ServerModConfig implements ServerConfigAccessor {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static ServerModConfig config = null;

    private static File getConfigFile(MinecraftServer server) {
        File configDir = new File(server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).toFile(), "serverconfigs");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        return new File(configDir, "beundead.json");
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerModConfig::load);

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            save(server);
            config = null;
        });
    }

    public static ServerModConfig get() {
        return config;
    }

    public static ServerModConfig getOrCreate() {
        if (config == null){
            config = new ServerModConfig();
        }
        return config;
    }

    public static void clearConfig(){
        config = null;
    }

    private static void load(MinecraftServer server) {
        if (config == null){
            File file = getConfigFile(server);
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    config = GSON.fromJson(reader, ServerModConfig.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    config = new ServerModConfig();
                }
            } else {
                config = new ServerModConfig();
                save(server);
            }
        }
    }

    public static void save(MinecraftServer server) {
        File file = getConfigFile(server);
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public InventoryState zombieInvState = InventoryState.ONE_SLOT;
    public boolean zombieCanChestExtension = true;
    public CureRequirements cureRequirements = CureRequirements.WEAKNESS_AND_APPLE;
    public boolean husksEnabled = true;
    public boolean drownedEnabled = true;
    public float zombieBreakSpeed = 0.5F;
    public boolean zombieNightVision = true;
    public boolean zombieJumpOnTheirOwn = true;
    public boolean zombieCanCrit = true;
    public boolean zombieOnlyKillExperience = true;
    public int zombieMaxViewDistance = 4;
    public double zombieWalkSpeed = 1D;
    public boolean zombieSprintEnabled = false;
    public boolean infectionEnabled = false;
    public boolean onlyTurnWhenInfected = false;
    public boolean forceTurnWhenInfected = true;
    public long respawnTimerToHuman = 0L;
    public long respawnTimerToZombie = 0L;

    public boolean undeadMode;

    @Override
    public int getZombieInvState() {
        return zombieInvState.getId();
    }

    @Override
    public boolean getZombieCanChestExtension() {
        return zombieCanChestExtension;
    }

    @Override
    public boolean areHusksEnabled() {
        return husksEnabled;
    }

    @Override
    public boolean areDrownedEnabled() {
        return drownedEnabled;
    }

    @Override
    public boolean getZombieNightVision() {
        return zombieNightVision;
    }

    @Override
    public boolean getZombieJumpOnTheirOwn() {
        return zombieJumpOnTheirOwn;
    }

    @Override
    public boolean getZombieCanCrit() {
        return zombieCanCrit;
    }

    @Override
    public int getZombieMaxViewDistance() {
        return zombieMaxViewDistance;
    }

    @Override
    public boolean isInfectionEnabled() {
        return infectionEnabled;
    }

    @Override
    public boolean getOnlyTurnWhenInfected() {
        return onlyTurnWhenInfected;
    }

    @Override
    public boolean getForceTurnWhenInfected() {
        return forceTurnWhenInfected;
    }

    @Override
    public long getRespawnTimer() {
        return respawnTimerToHuman;
    }

    @Override
    public long getRespawnTimerToZombie() {
        return respawnTimerToZombie;
    }

    @Override
    public double getZombieWalkSpeed() {
        return zombieWalkSpeed;
    }

    @Override
    public float getZombieBreakSpeed() {
        return zombieBreakSpeed;
    }

    @Override
    public boolean getZombieOnlyKillExperience() {
        return zombieOnlyKillExperience;
    }

    @Override
    public boolean isZombieSprintingEnabled() {
        return zombieSprintEnabled;
    }

    @Override
    public int getCureRequirements() {
        return cureRequirements.getId();
    }

    @Override
    public boolean hasUndeadMode() {
        return undeadMode;
    }
}