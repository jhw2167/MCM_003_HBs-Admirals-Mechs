package com.holybuckets.admiralsmechs.entity;

import com.holybuckets.admiralsmechs.entity.vehicle.TankEntity;
import net.blay09.mods.balm.api.DeferredObject;
import com.holybuckets.admiralsmechs.Constants;
import net.blay09.mods.balm.api.entity.BalmEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static final float MECH_HEALTH = 100.0f;

    public static DeferredObject<EntityType<LivingEntity>> tank;

    public static void initialize(BalmEntities balmEntities) {
        // Register entities here
        tank = balmEntities.registerEntity( id("tank"),
            getBuilder(TankEntity::new, MobCategory.CREATURE, 1.0f, 2.0f) );

    }

    static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    static <T extends Entity> EntityType.Builder<T> getBuilder(EntityType.EntityFactory<T> factory, MobCategory type, float width, float height) {
        return EntityType.Builder.of(factory, type)
            .sized(width, height);
    }



}
