package com.holybuckets.admiralsmechs.client;

import com.holybuckets.admiralsmechs.Constants;
import com.holybuckets.admiralsmechs.client.render.TankEntityRenderer;
import com.holybuckets.admiralsmechs.entity.ModEntities;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.minecraft.resources.ResourceLocation;

public class ModRenderers {

    //public static ModelLayerLocation someModel;

    public static void clientInitialize(BalmRenderers renderers) {
        //waystoneModel = renderers.registerModel(new ResourceLocation(Waystones.MOD_ID, "waystone"), () -> WaystoneModel.createLayer(CubeDeformation.NONE));
        //renderers.setBlockRenderType(() -> ModBlocks.stoneBrickBlockEntity, RenderType.cutout());
        renderers.registerEntityRenderer( id("tank"),
        ModEntities.tank::get, (context) -> new TankEntityRenderer(context) );
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}
