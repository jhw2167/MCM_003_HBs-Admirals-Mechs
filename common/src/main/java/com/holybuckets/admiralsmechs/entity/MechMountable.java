package com.holybuckets.admiralsmechs.entity;

import com.holybuckets.admiralsmechs.LoggerProject;
import com.holybuckets.admiralsmechs.entity.state.StateEvent;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ClientInputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class MechMountable extends MechBase {

    public MechMountable(EntityType<? extends MechMountable> entityType, Level level) {
        super(entityType, level);
    }

    //** CORE
    protected abstract void updateState(StateEvent event);

    @Nullable
    protected String mapKeyToEvent(int keyPressed) {
        switch (keyPressed) {
            case -1:
                return "idle";
            case InputConstants.KEY_W:
                return "forward";
            case InputConstants.KEY_S:
                return "backward";
            case InputConstants.KEY_A:
                return "left-turn";
            case InputConstants.KEY_D:
                return "right-turn";
            case InputConstants.MOUSE_BUTTON_LEFT:
                return "fire-primary";
            case InputConstants.MOUSE_BUTTON_RIGHT:
                return "fire-secondary";
            case InputConstants.KEY_SPACE:
                return "jump";
        }
        return null;
    }

    //** UTILITY
    protected boolean isServerSide() {
        return !isClientSide();
    }

    protected boolean isClientSide() {
        return this.level().isClientSide();
    }



    //** EVENT

    public static void updateFromClient(ClientInputEvent event)
    {
        Player p = event.getPlayer();
        Entity e =  p.getControlledVehicle();

        if(e == null || !(e instanceof MechMountable)) return;
        if(!(p instanceof ServerPlayer)) return;

        MechMountable mech = (MechMountable) e;
        String eventString = mech.mapKeyToEvent(event.getMessage().code);
        if( eventString == null ) return;

        LoggerProject.logDebug("015001", "Mech event: " + eventString);
        StateEvent stateEvent = new StateEvent(mech, eventString);
        mech.updateState(stateEvent);
    }


}
