package com.holybuckets.admiralsmechs.entity;

import com.holybuckets.admiralsmechs.LoggerProject;
import com.holybuckets.admiralsmechs.entity.state.State;
import com.holybuckets.admiralsmechs.entity.state.StateEvent;
import com.holybuckets.admiralsmechs.entity.state.TankState;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ClientInputEvent;
import com.holybuckets.foundation.networking.ClientInputMessage;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class MechMountable extends MechBase {

    protected State currentState;
    protected State prevState;
    protected int lastClientInput;
    protected float speed;
    protected float flySpeed;
    protected float strafeSpeed;
    protected float maxUpStep;
    protected Vec3 orientation;

    public MechMountable(EntityType<? extends MechMountable> entityType, Level level) {
        super(entityType, level);
        this.speed = 0.5f;
        this.flySpeed = 0.5f;
        this.strafeSpeed = 1f;
        this.maxUpStep = 1.6F;
        this.orientation = new Vec3(0, 0, 0);
    }

    //** FROM SUPER
    @Override
    public LivingEntity getControllingPassenger() {
        Entity e = super.getFirstPassenger();
        if(e instanceof Player) {
            return (Player) e;
        }
        return null;
    }

    @Override
    public float maxUpStep() {
        return this.maxUpStep;
    }

    @Override
    protected Vec3 getRiddenInput(Player $$0, Vec3 $$1) {
        return Vec3.ZERO;
    }

    @Override
    protected float getRiddenSpeed(Player $$0) {
        return super.getRiddenSpeed($$0);
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    /**
     * Standard travel method that orients synchronously with the player.
     * YRot - conventional roll - tilts player
     * XRot - conventional pitch - tilts forward/backward, dampened for land animals
     * @param pos
     */
    @Override
    public void travel(Vec3 pos) {
        if (this.isAlive() && this.isVehicle()) {
            Player p = this.getDriver();
            if( p == null ) return;
            //ServerPlayer pilot = (ServerPlayer) p;

            // ROTATE Vehicle
            this.setYRot(p.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(p.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.setYBodyRot(this.getYRot());
            this.setYHeadRot(this.getYRot());

            float f = p.xxa * 0.5F;
            float f1 = p.zza * 0.5F;

            // BOOST
            /*
            if (isServerSide()) {
                if (this.actionController.isBoost()) {
                    this.setSpeed(1.5F);
                } else {
                    this.setSpeed(0.5F);
                }
            }
            */

            super.travel(new Vec3(f, pos.y, f1));
            this.hasImpulse = true;
        } else {
            super.travel(pos);
        }
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
                return "left_turn";
            case InputConstants.KEY_D:
                return "right_turn";
            case InputConstants.MOUSE_BUTTON_LEFT:
                return "fire_primary";
            case InputConstants.MOUSE_BUTTON_RIGHT:
                return "fire_secondary";
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

    public void updateOrientation(Vec3 delta) {
        this.orientation = this.orientation.add(delta);

        //check Y
        // 0 - south, spawn angle, 180 north
        if (this.orientation.y >180 ) {
            this.orientation = this.orientation.add(new Vec3(0, -360, 0));
        } else if (this.orientation.y <= -180) {
            this.orientation = this.orientation.add(new Vec3(0, 360, 0));
        }

        this.setYRot((float)orientation.y);
        this.yRotO = this.getYRot();
        this.setXRot((float)this.orientation.x);
        this.setYBodyRot(this.getYRot());
        this.setYHeadRot(this.getYRot());
    }

    public void setOrientation(Vec3 newOrientation) {
        this.orientation = newOrientation;
    }

    public Vec3 getOrientation() {
        return this.orientation;
    }

    public Player getDriver() {
        Entity e = this.getControllingPassenger();
        if(e instanceof Player) {
            return (Player) e;
        }
        return null;
    }


    //** EVENT

    public static void updateFromClient(ClientInputEvent event)
    {
        Player p = event.getPlayer();
        Entity e =  p.getControlledVehicle();

        if(e == null || !(e instanceof MechMountable)) return;

        MechMountable mech = (MechMountable) e;
        ClientInputMessage message = event.getMessage();
        List<Integer> codes = event.getKeyCodes().stream().limit(2).toList();
        StateEvent stateEvent;
        if( codes.isEmpty() || codes.size() < 2) {
            stateEvent = new StateEvent(mech, mech.mapKeyToEvent(message.code));
        } else {
            String e1 = mech.mapKeyToEvent(codes.get(0));
            String e2 = mech.mapKeyToEvent(codes.get(1));
            stateEvent = new StateEvent(mech, e1, e2);
        }

        if( stateEvent.getEventName() == null ) {
            if(mech.lastClientInput == -1) return;
        }
        mech.lastClientInput = message.code;

        mech.updateState(stateEvent);
    }


}
