package io.github.ziederziet.beundead.fogandredmoon;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public class FogAndRedMoonSavedData extends SavedData {

    public static Factory<FogAndRedMoonSavedData> FACTORY = new Factory<FogAndRedMoonSavedData>(
            FogAndRedMoonSavedData::create,
            (compoundTag, provider) -> load(compoundTag),
            DataFixTypes.PLAYER);

    private long timeWhenFog = -200;
    private long timeWhenRedMoon = -200;

    public FogAndRedMoonSavedData(){

    }

    public void setFogTimer(long gameTime){
        this.timeWhenFog = gameTime;
        this.setDirty();
    }

    public void setRedMoonTimer(long gameTime){
        this.timeWhenRedMoon = gameTime;
        this.setDirty();
    }

    public long getFogTimer(){
        return this.timeWhenFog;
    }

    public long getRedMoonTimer(){
        return this.timeWhenRedMoon;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putLong("TimeWhenFog", timeWhenFog);
        compoundTag.putLong("TimeWhenRedMoon", timeWhenRedMoon);
        return compoundTag;
    }

    public static FogAndRedMoonSavedData create() {
        return new FogAndRedMoonSavedData();
    }

    public static FogAndRedMoonSavedData load(CompoundTag tag) {
        FogAndRedMoonSavedData data = create();
        data.timeWhenFog = tag.getLong("TimeWhenFog");
        data.timeWhenRedMoon = tag.getLong("TimeWhenRedMoon");
        return data;
    }

    public static FogAndRedMoonSavedData getFogAndRedMoonSavedData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, "fog_and_redmoon");
    }
}
