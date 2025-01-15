package io.github.ziederziet.beundead.zombie_settings;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.networking.ModNetworking;
import io.github.ziederziet.beundead.networking.ZombieSettingsPacket;
import io.github.ziederziet.beundead.zombie_capability.InfectionZombieCapabilityProvider;
import io.github.ziederziet.beundead.zombie_capability.ZombiePlayerCapabilityProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.SavedData;

public class ZombieSettingsSavedData extends SavedData {

    public static Factory<ZombieSettingsSavedData> FACTORY = new Factory<ZombieSettingsSavedData>(
            ZombieSettingsSavedData::create,
            (compoundTag, provider) -> load(compoundTag),
            DataFixTypes.PLAYER);

    private int zombieInvState = 0; // 0 = one slot | 1 = hotbar | 2 = full inv            CLIENT SIDE
    private boolean zombieCanChestExtension = true; // chest increases inv state by one    CLIENT SIDE
    private boolean husks = true;
    private boolean drowned = true;
    private boolean zombieNightVision = true; //                                           CLIENT SIDE
    private boolean zombieJumpOnTheirOwn = true; //                                        CLIENT SIDE
    private boolean zombieCanCrit = true;
    private int zombieMaxViewDistance = 4; //                                              CLIENT SIDE
    private boolean infection = false;
    private boolean onlyTurnWhenInfected = false;
    private boolean forceTurnWhenInfected = true;
    // Timer in seconds
    private int respawnTimer = 0;
    private int respawnTimerToZombie = 180;
    private double zombieWalkSpeed = 0.46D;

    public ZombieSettingsSavedData(){
    }

    // this.setDirty(); when set

    public double getZombieWalkSpeed() { return zombieWalkSpeed; }

    public int getInvState(){
        return zombieInvState;
    }

    public boolean canChestExtension(){
        return zombieCanChestExtension;
    }

    public boolean hasInfection(){
        return infection;
    }

    public boolean hasHusks(){
        return husks;
    }

    public boolean hasDrowned(){
        return drowned;
    }

    public int getRespawnTimer(){
        return respawnTimer;
    }

    public int getRespawnTimerToZombie(){
        return respawnTimerToZombie;
    }

    public boolean getZombieJumpOnTheirOwn() { return zombieJumpOnTheirOwn; }

    public void setHusks(boolean husks, MinecraftServer server){
        if (!husks){
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                if (BeUndead.getZombieType(serverPlayer) == 2){
                    BeUndead.setZombieType(serverPlayer, 1);
                }
            });
        }
        this.husks = husks;
    }

    public void setDrowned(boolean drowned, MinecraftServer server){
        if (!drowned){
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                if (BeUndead.getZombieType(serverPlayer) == 3){
                    BeUndead.setZombieType(serverPlayer, 1);
                }
            });
        }
        this.drowned = drowned;
    }

    public void setInfection(boolean infection, MinecraftServer server){
        if (!infection){
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                serverPlayer.getCapability(InfectionZombieCapabilityProvider.ZOMBIE_CAPABILITY).ifPresent(zombiePlayerCapability -> {
                    zombiePlayerCapability.removeInfection(serverPlayer);
                });
            });
        }

        this.infection = infection;
    }

    public void setZombieSpeed(double amount){
        this.zombieWalkSpeed = amount;
        sendUpdatePacketToAll();
        this.setDirty();
    }


    public void setZombieInvState(int zombieInvState, MinecraftServer server){
        if (this.zombieInvState < zombieInvState){
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                BeUndead.checkItemsInNewState(serverPlayer, true);
            });
        }
        this.zombieInvState = zombieInvState;
        sendUpdatePacketToAll();
        setDirty();
    }

    public void setZombieCanChestExtension(boolean zombieCanChestExtension, MinecraftServer server){
        if (!zombieCanChestExtension){
            server.getPlayerList().getPlayers().forEach(serverPlayer -> {
                if (BeUndead.zombieHasChest(serverPlayer)){
                    serverPlayer.drop(new ItemStack(Items.CHEST), true, false);
                    BeUndead.setZombieChest(serverPlayer, false);
                }
            });
        }

        this.zombieCanChestExtension = zombieCanChestExtension;
        sendUpdatePacketToAll();
        setDirty();
    }

    public void setZombieNightVision(boolean zombieNightVision){
        this.zombieNightVision = zombieNightVision;
        sendUpdatePacketToAll();
        setDirty();
    }

    public void setZombieJumpOnTheirOwn(boolean zombieJumpOnTheirOwn){
        this.zombieJumpOnTheirOwn = zombieJumpOnTheirOwn;
        sendUpdatePacketToAll();
        setDirty();
    }

    public void setZombieMaxViewDistance(int zombieMaxViewDistance){
        this.zombieMaxViewDistance = zombieMaxViewDistance;
        sendUpdatePacketToAll();
        setDirty();
    }

    public void setRespawnTimer(int respawnTimer){
        this.respawnTimer = respawnTimer;
        setDirty();
    }

    public void setRespawnTimerToZombie(int respawnTimerToZombie){
        this.respawnTimerToZombie = respawnTimerToZombie;
        setDirty();
    }


    public void sendUpdatePacketTo(ServerPlayer to){
        ModNetworking.sendToClient(getPacket(), to);
    }

    public void sendUpdatePacketToAll(){
        ModNetworking.sendToAllClients(getPacket());
    }

    public ZombieSettingsPacket getPacket(){
        return new ZombieSettingsPacket(zombieInvState,
                zombieCanChestExtension,
                zombieNightVision,
                zombieJumpOnTheirOwn,
                zombieMaxViewDistance,
                zombieWalkSpeed);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putInt("ZombieInvState", zombieInvState);
        compoundTag.putBoolean("ZombieCanChestExtension", zombieCanChestExtension);
        compoundTag.putBoolean("Husks", husks);
        compoundTag.putBoolean("Drowns", drowned);
        compoundTag.putBoolean("ZombieNightVision", zombieNightVision);
        compoundTag.putBoolean("ZombieJumpOnTheirOwn", zombieJumpOnTheirOwn);
        compoundTag.putBoolean("ZombieCanCrit", zombieCanCrit);
        compoundTag.putInt("ZombieMaxViewDistance", zombieMaxViewDistance);
        compoundTag.putBoolean("Infection", infection);
        compoundTag.putBoolean("OnlyTurnWhenInfected", onlyTurnWhenInfected);
        compoundTag.putBoolean("ForceTurnWhenInfected", forceTurnWhenInfected);
        compoundTag.putInt("RespawnTimer", respawnTimer);
        compoundTag.putInt("RespawnTimerToZombie", respawnTimerToZombie);
        compoundTag.putDouble("ZombieWalkSpeed", zombieWalkSpeed);
        return compoundTag;
    }

    public static ZombieSettingsSavedData create() {
        return new ZombieSettingsSavedData();
    }

    public static ZombieSettingsSavedData load(CompoundTag tag) {
        ZombieSettingsSavedData data = create();
        data.zombieInvState = tag.getInt("ZombieInvState");
        data.zombieCanChestExtension = tag.getBoolean("ZombieCanChestExtension");
        data.husks = tag.getBoolean("Husks");
        data.drowned = tag.getBoolean("Drowned");
        data.zombieNightVision = tag.getBoolean("ZombieNightVision");
        data.zombieJumpOnTheirOwn = tag.getBoolean("ZombieJumpOnTheirOwn");
        data.zombieCanCrit = tag.getBoolean("ZombieCanCrit");
        data.zombieMaxViewDistance = tag.getInt("ZombieMaxViewDistance");
        data.infection = tag.getBoolean("Infection");
        data.onlyTurnWhenInfected = tag.getBoolean("OnlyTurnWhenInfected");
        data.forceTurnWhenInfected = tag.getBoolean("ForceTurnWhenInfected");
        data.respawnTimer = tag.getInt("RespawnTimer");
        data.respawnTimerToZombie = tag.getInt("RespawnTimerToZombie");
        data.zombieWalkSpeed = tag.getDouble("ZombieWalkSpeed");
        return data;
    }

    public static ZombieSettingsSavedData getZombieSettingsSavedData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, "zombie_settings");
    }
}
