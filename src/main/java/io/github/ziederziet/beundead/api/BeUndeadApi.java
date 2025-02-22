package io.github.ziederziet.beundead.api;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.world.entity.player.Player;

public class BeUndeadApi {
    public static int getZombieType(Player player) {
        return BeUndead.getZombieType(player);
    }
}