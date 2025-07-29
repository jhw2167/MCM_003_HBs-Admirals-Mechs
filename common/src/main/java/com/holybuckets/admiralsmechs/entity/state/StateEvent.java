package com.holybuckets.admiralsmechs.entity.state;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animation.AnimationController;

public class StateEvent {

    Entity entity;
    //AnimationController controller;
    String eventName;

    public StateEvent(Entity entity, String eventName) {
        this.entity = entity;
        this.eventName = eventName;
    }
}
