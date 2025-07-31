package com.holybuckets.admiralsmechs.entity.state;

import com.holybuckets.admiralsmechs.entity.MechBase;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Root interface defining finite state machine states.
 */
public abstract class State {

    public static final Map<String, State> STATES = new HashMap<>();

    /**
     * Called when the state is entered.
     * This method is invoked when the state is first entered.
     */
    public void enter(MechBase entity) {
        //nothing
    }

    /**
     * Called when the state is exited.
     * This method is invoked when transitioning out of the state.
     */
    public void exit(MechBase entity) {
        //nothing
    }

    /**
     * Updates the state.
     * This method is called periodically to update the state logic.
     */
    public State update(StateEvent event) {
        if(STATES.containsKey(event.eventName)) {
            return STATES.get(event.eventName);
        }
        return this;
    }

}
