package io.github.ziederziet.beundead.api;

import io.github.ziederziet.beundead.common.BeUndeadHelper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

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

    public static void startConverting(Player player, @Nullable Player converter){
        BeUndeadHelper.startConverting(player, converter);
    }

    public static void revive(Player player){
        BeUndeadHelper.revive(player, false);
    }

    public static void setUndeadType(Player player, String string){
        BeUndeadHelper.setUndeadType(player, string, true);
    }

    public static String getUndeadType(Player player){
        return BeUndeadHelper.getUndeadTypeName(player);
    }
}