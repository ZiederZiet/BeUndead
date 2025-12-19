package io.github.ziederziet.beundead.api;

import io.github.ziederziet.beundead.event.PlayerRevivedEvent;
import io.github.ziederziet.beundead.event.PlayerTurnedUndeadEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

public class BeUndeadApi {
    public static void firePlayerTurnedUndead(ServerPlayer player, String type) {
        MinecraftForge.EVENT_BUS.post(new PlayerTurnedUndeadEvent(player, type));
    }

    public static void firePlayerRevived(ServerPlayer player) {
        MinecraftForge.EVENT_BUS.post(new PlayerRevivedEvent(player));
    }
}