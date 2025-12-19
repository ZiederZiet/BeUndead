package io.github.ziederziet.beundead.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class PlayerTurnedUndeadEvent extends Event {
    private final ServerPlayer player;
    private final String type;

    public PlayerTurnedUndeadEvent(ServerPlayer player, String type) {
        this.player = player;
        this.type = type;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public String getType() {
        return type;
    }
}