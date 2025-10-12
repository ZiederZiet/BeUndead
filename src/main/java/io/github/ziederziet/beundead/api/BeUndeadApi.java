package io.github.ziederziet.beundead.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public class BeUndeadApi {
    public static final Event<OnPlayerTurnedUndead> PLAYER_TURNED_UNDEAD_EVENT = EventFactory.createArrayBacked(OnPlayerTurnedUndead.class,
            listeners -> (player, type) -> {
                for (OnPlayerTurnedUndead listener : listeners) {
                    listener.onPlayerTurnedUndead(player, type);
                }
            });

    public static final Event<OnPlayerRevived> PLAYER_REVIVED_EVENT = EventFactory.createArrayBacked(OnPlayerRevived.class,
            listeners -> (player) -> {
                for (OnPlayerRevived listener : listeners) {
                    listener.onPlayerRevived(player);
                }
            });

    @FunctionalInterface
    public interface OnPlayerTurnedUndead {
        void onPlayerTurnedUndead(ServerPlayer player, String type);
    }

    @FunctionalInterface
    public interface OnPlayerRevived {
        void onPlayerRevived(ServerPlayer player);
    }
}