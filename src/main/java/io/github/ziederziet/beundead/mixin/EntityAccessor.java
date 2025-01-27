package io.github.ziederziet.beundead.mixin;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("entityData")
    SynchedEntityData getEntityData();

    @Accessor("level")
    Level getLevel();

    @Accessor("bb")
    AABB getBoundingBox();

    @Accessor("position")
    Vec3 getPosition();
}
