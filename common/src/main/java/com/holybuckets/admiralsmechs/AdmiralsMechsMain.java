package com.holybuckets.admiralsmechs;


import com.holybuckets.admiralsmechs.entity.MechBase;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.admiralsmechs.config.TemplateConfig;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;


/**
 * Main instance of the mod, initialize this class statically via commonClass
 * This class will init all major Manager instances and events for the mod
 */
public class AdmiralsMechsMain {
    private static boolean DEV_MODE = false;;
    private static TemplateConfig CONFIG;
    public static AdmiralsMechsMain INSTANCE;

    public AdmiralsMechsMain()
    {
        super();
        INSTANCE = this;
        init();
        // LoggerProject.logInit( "001000", this.getClass().getName() ); // Uncomment if you have a logging system in place
    }

    private void init()
    {

        /*
        Proxy for external APIs which are platform dependent
        this.portalApi = (PortalApi) Balm.platformProxy()
            .withFabric("com.holybuckets.challengetemple.externalapi.FabricPortalApi")
            .withForge("com.holybuckets.challengetemple.externalapi.ForgePortalApi")
            .build();
            */

        //Events
        EventRegistrar registrar = EventRegistrar.getInstance();
        MechBase.init(registrar);
        //ChallengeBlockBehavior.init(registrar);


        //register local events
        registrar.registerOnBeforeServerStarted(this::onServerStarting);

    }

    private void onServerStarting(ServerStartingEvent e) {
        //CONFIG = Balm.getConfig().getActiveConfig(TemplateConfig.class);
        //this.DEV_MODE = CONFIG.devMode;
        this.DEV_MODE = false;
    }


}
