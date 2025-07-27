package com.holybuckets.admiralsmechs.client.model;

import com.holybuckets.admiralsmechs.Constants;
import com.holybuckets.admiralsmechs.entity.vehicle.TankEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TankeEntityModel extends GeoModel<TankEntity> {

    @Override
    public ResourceLocation getModelResource(TankEntity animatable) {
        return new ResourceLocation(Constants.MOD_ID, "geo/tank.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TankEntity animatable) {
        return new ResourceLocation(Constants.MOD_ID, "textures/entity/tank.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TankEntity animatable) {
        return new ResourceLocation(Constants.MOD_ID, "animations/tank.animation.json");
    }
}
