package io.github.ziederziet.beundead.theyre_coming;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

public class TheyreComingAccessor {
    public static boolean hasTheyreComing() {
        return ModList.get().isLoaded("theyrecoming");
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
