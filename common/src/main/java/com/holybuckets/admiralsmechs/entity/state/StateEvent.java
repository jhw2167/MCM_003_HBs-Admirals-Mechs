package com.holybuckets.admiralsmechs.entity.state;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animation.AnimationController;

public class StateEvent {

    Entity entity;
    //AnimationController controller;
    String eventName;
    String secondaryEvent;

    public StateEvent(Entity entity, String eventName) {
        this(entity, eventName, null);
    }

    public StateEvent(Entity entity, String eventName, String secondaryEvent) {
        this.entity = entity;
        this.eventName = eventName;
        this.secondaryEvent = secondaryEvent;
    }

    public String getEventName() {
        return eventName;
    }

    public String getSecondaryEvent() {
        return secondaryEvent;
    }
}
