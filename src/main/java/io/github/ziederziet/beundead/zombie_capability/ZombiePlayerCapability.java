package io.github.ziederziet.beundead.zombie_capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import javax.annotation.Nullable;
import java.util.UUID;

@AutoRegisterCapability
public class ZombiePlayerCapability {
    private @Nullable UUID conversionStarter;
    public void cloneFrom(ZombiePlayerCapability other){
    }
    public ZombiePlayerCapability(){
        conversionStarter = null;
    }
    public void saveNBTData(CompoundTag tag){
        if (conversionStarter != null){
            tag.putUUID("ConversionStarter", conversionStarter);
        }
    }

    public void loadNBTData(CompoundTag tag){
        if (tag.contains("ConversionStarter")){
            conversionStarter = tag.getUUID("ConversionStarter");
        }
    }

    public UUID getConversionStarter(){
        return conversionStarter;
    }

    public void setConversionStarter(@Nullable UUID conversionStarter){
        this.conversionStarter = conversionStarter;
    }
}
