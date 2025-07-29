package com.holybuckets.admiralsmechs.entity;

import com.holybuckets.foundation.event.EventRegistrar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public abstract class MechBase extends LivingEntity implements GeoEntity, GeoAnimatable {

    /**
     * Register all mech events from subclasses
     * @param reg
     */
    public static void init(EventRegistrar reg) {
        reg.registerOnClientInput(MechMountable::updateFromClient);
    }

    public MechBase(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

}
