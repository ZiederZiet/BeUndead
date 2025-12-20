package io.github.ziederziet.beundead.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class PerWorldConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static PerWorldConfig config = null;

    public boolean undeadMode = false;

    private static File getConfigFile(Path worldPath) {
        File configDir = new File(worldPath.toFile(), "perworldconfig");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        return new File(configDir, "beundead.json");
    }

    public static PerWorldConfig get() {
        return config;
    }

    public static PerWorldConfig getOrCreate() {
        if (config == null){
            config = new PerWorldConfig();
        }
        return config;
    }

    public static void clearConfig() {
        config = null;
    }

    public static void close(MinecraftServer server){
        save(server);
        clearConfig();
    }

    public boolean hasUndeadMode() {
        return undeadMode;
    }

    public static void load(MinecraftServer server) {
        if (config == null){
            File file = getConfigFile(server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT));
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    config = GSON.fromJson(reader, PerWorldConfig.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    config = new PerWorldConfig();
                }
            } else {
                config = new PerWorldConfig();
                save(server);
            }
        }
    }

    public static PerWorldConfig load(LevelStorageSource.LevelDirectory directory) {
        File file = getConfigFile(directory.path());
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                return GSON.fromJson(reader, PerWorldConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void save(MinecraftServer server) {
        File file = getConfigFile(server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT));
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
