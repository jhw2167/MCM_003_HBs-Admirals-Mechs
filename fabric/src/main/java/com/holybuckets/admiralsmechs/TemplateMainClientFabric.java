package com.holybuckets.admiralsmechs;

import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;


public class AdmiralsMechsMainFabricClientFabric implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        BalmClient.initialize(Constants.MOD_ID, CommonClass::initClient);
    }

}
