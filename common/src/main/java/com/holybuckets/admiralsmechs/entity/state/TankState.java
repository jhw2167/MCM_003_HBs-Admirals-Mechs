package com.holybuckets.admiralsmechs.entity.state;

import com.holybuckets.admiralsmechs.entity.MechBase;
import com.holybuckets.admiralsmechs.entity.vehicle.TankEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Root interface defining finite state machine states.
 */
public class TankState extends State {

    static final String ANIM_BASE = "ironclad.";
    static final String CONTROLLER = "tank";

    static final Map<String, TankState> STATES = new HashMap<>();
    public static final TankState IDLE = new TankState("idle");
    public static final TankState FORWARD = new TankState("forward");
    public static final TankState BACKWARD = new TankState("backward");
    public static final TankState LEFT_TURN = new TankState("left_turn");
    public static final TankState RIGHT_TURN = new TankState("right_turn");

    static {
        STATES.put("idle", IDLE);
        STATES.put("forward", FORWARD);
        STATES.put("backward", BACKWARD);
        STATES.put("left_turn", LEFT_TURN);
        STATES.put("right_turn", RIGHT_TURN);
    }
    public static TankState getInitialState(TankEntity entity) {
        return STATES.get("idle");
    }

    String animname;

    private TankState(String animname) {
        this.animname = animname;
        STATES.put(animname, this);
    }

     @Override
    public void enter(MechBase tank) {
        tank.triggerAnim(CONTROLLER, animname);
    }
        private String a(String s) { return ANIM_BASE + s; }

     @Override
    public void exit(MechBase tank) {
        //nothing
    }


    @Override
    public TankState update(StateEvent event) {
        if(STATES.containsKey(event.eventName)) {
            return STATES.get(event.eventName);
        }

        if(event.secondaryEvent != null)  {
            if(STATES.containsKey(event.secondaryEvent)) {
                return STATES.get(event.secondaryEvent);
            }
        }

        return this;
    }


    @Override
    public TankState update(StateEvent event, MechBase entity)
    {
        TankState returnState = update(event);
        if( eitherMatch(event, TankEntity.FIRE_PRIMARY_EVENT ) )
            ((TankEntity) entity).setFirePrimary( true );
        else if( eitherMatch(event, TankEntity.FIRE_SECONDARY_EVENT ) ) {
            ((TankEntity) entity).setFireSecondary( true );
        }

        return returnState;
    }

    private static boolean eitherMatch(StateEvent event, String matchKey) {
        return event.eventName.equals(matchKey) ||
         (event.secondaryEvent != null && event.secondaryEvent.equals(matchKey));
    }

}
