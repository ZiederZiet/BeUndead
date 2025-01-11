package io.github.ziederziet.beundead.zombie_capability;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfectionZombieCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<InfectionZombieCapability> ZOMBIE_CAPABILITY = CapabilityManager.get(new CapabilityToken<InfectionZombieCapability>() {});
    private InfectionZombieCapability zombieCapability = null;
    private final LazyOptional<InfectionZombieCapability> optional = LazyOptional.of(this::createZombieCapability);

    private InfectionZombieCapability createZombieCapability() {
        if (this.zombieCapability == null){
            this.zombieCapability = new InfectionZombieCapability();
        }
        return this.zombieCapability;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (capability == ZOMBIE_CAPABILITY){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        createZombieCapability().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        createZombieCapability().loadNBTData(compoundTag);
    }
}
