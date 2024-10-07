package io.github.ziederziet.beundead.mixin;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(SleepStatus.class)
public abstract class SleepStatusMixin {
    private static int beforeActivePlayers;
    private static int beforeSleepingPlayers;
    @Shadow
    private int activePlayers;
    @Shadow
    private int sleepingPlayers;
    @Inject(at = @At("HEAD"), method = "update(Ljava/util/List;)Z")
    public boolean updateBefore(List<ServerPlayer> pPlayers, CallbackInfoReturnable<Boolean> info){
        beforeActivePlayers = activePlayers;
        beforeSleepingPlayers = sleepingPlayers;
        return false;
    }
    @Inject(at = @At("TAIL"), method = "update(Ljava/util/List;)Z")
    public boolean update(List<ServerPlayer> pPlayers, CallbackInfoReturnable<Boolean> info){
        Iterator<ServerPlayer> players = pPlayers.iterator();
        while (players.hasNext() && activePlayers > 0){
            ServerPlayer player = players.next();
            if (BeUndead.getZombieType(player) > 0){
                activePlayers--;
                if (player.isSleeping()){
                    sleepingPlayers--;
                }
            }
        }
        return (beforeSleepingPlayers > 0 || sleepingPlayers > 0) && (beforeActivePlayers != activePlayers || beforeSleepingPlayers != this.sleepingPlayers);
    }
}
