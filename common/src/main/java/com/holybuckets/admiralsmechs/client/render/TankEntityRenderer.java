package com.holybuckets.admiralsmechs.client.render;

import com.holybuckets.admiralsmechs.client.model.TankeEntityModel;
import com.holybuckets.admiralsmechs.entity.vehicle.TankEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TankEntityRenderer extends GeoEntityRenderer<TankEntity> {

    public TankEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TankeEntityModel());
    }


}
