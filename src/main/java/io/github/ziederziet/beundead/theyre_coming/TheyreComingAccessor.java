package io.github.ziederziet.beundead.theyre_coming;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Method;

public class TheyreComingAccessor {
    public static boolean hasTheyreComing() {
        return FabricLoader.getInstance().isModLoaded("theyrecoming");
    }

    public static int getPhase(MinecraftServer server) {
        if (hasTheyreComing()){
            try {
                Class<?> apiClass = Class.forName("io.github.ziederziet.theyrecoming.api.TheyreComingApi");
                Method method = apiClass.getMethod("getPhase", MinecraftServer.class);
                return (int) method.invoke(null, server);
            } catch (Exception e) {
            }
        }
        return 0;
    }
}
