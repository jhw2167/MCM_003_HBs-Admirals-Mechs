package com.holybuckets.admiralsmechs.config;

import com.holybuckets.admiralsmechs.Constants;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.Synced;


@Config(Constants.MOD_ID)
public class AdmiralsMechsConfig {

    @Comment("devMode==true disables portal spawns so the player can build and save new challenges")
    public boolean devMode = false;
    @Comment("Where the loot rules json configuration can be found. This file determines what loot is available in each level of pool")
    public String lootRulesConfig = "config/challengeTempleslootRules.json";

}