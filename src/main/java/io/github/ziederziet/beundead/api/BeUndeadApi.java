package io.github.ziederziet.beundead.api;

import io.github.ziederziet.beundead.event.PlayerRevivedEvent;
import io.github.ziederziet.beundead.event.PlayerTurnedUndeadEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;

public class BeUndeadApi {
    public static void firePlayerTurnedUndead(ServerPlayer player, String type) {
        NeoForge.EVENT_BUS.post(new PlayerTurnedUndeadEvent(player, type));
    }

    public static void firePlayerRevived(ServerPlayer player) {
        NeoForge.EVENT_BUS.post(new PlayerRevivedEvent(player));
    }
}