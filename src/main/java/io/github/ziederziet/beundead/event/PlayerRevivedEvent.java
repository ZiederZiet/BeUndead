package io.github.ziederziet.beundead.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

public class PlayerRevivedEvent extends Event {
    private final ServerPlayer player;

    public PlayerRevivedEvent(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}