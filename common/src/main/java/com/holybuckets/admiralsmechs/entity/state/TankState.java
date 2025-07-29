package com.holybuckets.admiralsmechs.entity.state;

import com.holybuckets.admiralsmechs.entity.vehicle.TankEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Root interface defining finite state machine states.
 */
public class TankState extends State {

    static final String ANIM_BASE = "ironclad.";
    static final String CONTROLLER = "tank_state";

    static final Map<String, TankState> STATES = new HashMap<>();
    static {
        STATES.put("idle", new TankState("idle"));
        STATES.put("forward", new TankState("forward"));
        STATES.put("backward", new TankState("backward"));
        STATES.put("left-turn", new TankState("left-turn"));
        STATES.put("right-turn", new TankState("right-turn"));
    }
    public static TankState getInitialState(TankEntity entity) {
        return STATES.get("idle");
    }

    String animname;

    private TankState(String animname) {
        this.animname = animname;
        STATES.put(animname, this);
    }

    /**
     * Called when the state is entered.
     * This method is invoked when the state is first entered.
     */
    void enter(TankEntity tank) {
        tank.triggerAnim(CONTROLLER, animname);
    }
    private String a(String s) { return ANIM_BASE + s; }

    /**
     * Called when the state is exited.
     * This method is invoked when transitioning out of the state.
     */
    void exit(TankEntity tank) {
        //nothing
    }

    /**
     * Updates the state.
     * This method is called periodically to update the state logic.
     */
    public TankState update(StateEvent event) {
        if(STATES.containsKey(event.eventName)) {
            return STATES.get(event.eventName);
        }
        return this;
    }

}
