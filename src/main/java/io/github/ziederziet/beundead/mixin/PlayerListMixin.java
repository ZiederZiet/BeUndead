package io.github.ziederziet.beundead.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Shadow
    public abstract void sendActivePlayerEffects(ServerPlayer pPlayer);

    @Shadow
    public abstract void sendLevelInfo(ServerPlayer pPlayer, ServerLevel pLevel);

    @Shadow
    private List<ServerPlayer> players = Lists.newArrayList();

    @Shadow
    private Map<UUID, ServerPlayer> playersByUUID = Maps.newHashMap();

    @Shadow
    private MinecraftServer server;

    @Shadow
    public abstract void sendPlayerPermissionLevel(ServerPlayer pPlayer);

    @Inject(at = @At("HEAD"), method = "respawn(Lnet/minecraft/server/level/ServerPlayer;ZLnet/minecraft/world/entity/Entity$RemovalReason;)Lnet/minecraft/server/level/ServerPlayer;", cancellable = true)
    public ServerPlayer respawn(ServerPlayer pPlayer, boolean pKeepInventory, Entity.RemovalReason pReason, CallbackInfoReturnable<ServerPlayer> info) {
        if (BeUndead.getZombieType(pPlayer) > 0){
            this.players.remove(pPlayer);
            pPlayer.serverLevel().removePlayerImmediately(pPlayer, pReason);
            //DimensionTransition dimensiontransition = pPlayer.findRespawnPositionAndUseSpawnBlock(pKeepInventory, DimensionTransition.DO_NOTHING);
            ServerLevel serverlevel = (ServerLevel) pPlayer.level();
            ServerPlayer serverplayer = new ServerPlayer(this.server, serverlevel, pPlayer.getGameProfile(), pPlayer.clientInformation());
            serverplayer.connection = pPlayer.connection;
            serverplayer.restoreFrom(pPlayer, pKeepInventory);
            serverplayer.setId(pPlayer.getId());
            serverplayer.setMainArm(pPlayer.getMainArm());
            serverplayer.copyRespawnPosition(pPlayer);

            Iterator var7 = pPlayer.getTags().iterator();

            while(var7.hasNext()) {
                String s = (String)var7.next();
                serverplayer.addTag(s);
            }

            Vec3 vec3 = pPlayer.position();
            serverplayer.moveTo(vec3.x, vec3.y, vec3.z, pPlayer.getYRot(), pPlayer.getXRot());
//            if (dimensiontransition.missingRespawnBlock()) {
//                serverplayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
//            }

            byte b0 = (byte)(pKeepInventory ? 1 : 0);
            ServerLevel serverlevel1 = serverplayer.serverLevel();
            LevelData leveldata = serverlevel1.getLevelData();
            serverplayer.connection.send(new ClientboundRespawnPacket(serverplayer.createCommonSpawnInfo(serverlevel1), b0));
            serverplayer.connection.teleport(serverplayer.getX(), serverplayer.getY(), serverplayer.getZ(), serverplayer.getYRot(), serverplayer.getXRot());
            serverplayer.connection.send(new ClientboundSetDefaultSpawnPositionPacket(serverlevel.getSharedSpawnPos(), serverlevel.getSharedSpawnAngle()));
            serverplayer.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
            serverplayer.connection.send(new ClientboundSetExperiencePacket(serverplayer.experienceProgress, serverplayer.totalExperience, serverplayer.experienceLevel));
            this.sendActivePlayerEffects(serverplayer);
            this.sendLevelInfo(serverplayer, serverlevel);
            this.sendPlayerPermissionLevel(serverplayer);
            serverlevel.addRespawnedPlayer(serverplayer);
            this.players.add(serverplayer);
            this.playersByUUID.put(serverplayer.getUUID(), serverplayer);
            BeUndead.setZombieType(serverplayer, BeUndead.getZombieType(pPlayer));
            serverplayer.initInventoryMenu();
            serverplayer.setHealth(serverplayer.getHealth());
            ForgeEventFactory.firePlayerRespawnEvent(serverplayer, pKeepInventory);
//            if (!pKeepInventory) {
//                BlockPos blockpos = BlockPos.containing(dimensiontransition.pos());
//                BlockState blockstate = serverlevel.getBlockState(blockpos);
//                if (blockstate.is(Blocks.RESPAWN_ANCHOR)) {
//                    serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0F, 1.0F, serverlevel.getRandom().nextLong()));
//                }
//            }

            info.setReturnValue(serverplayer);
            return serverplayer;
        }
        return null;
    }
}
