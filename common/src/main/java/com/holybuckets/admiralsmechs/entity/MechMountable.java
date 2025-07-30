package com.holybuckets.admiralsmechs.entity;

import com.holybuckets.admiralsmechs.LoggerProject;
import com.holybuckets.admiralsmechs.entity.state.State;
import com.holybuckets.admiralsmechs.entity.state.StateEvent;
import com.holybuckets.admiralsmechs.entity.state.TankState;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ClientInputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

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

    public void updateOrientation(Vec3 delta) {
        this.orientation = this.orientation.add(delta);
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
        if(!(p instanceof ServerPlayer)) return;

        MechMountable mech = (MechMountable) e;
        String eventString = mech.mapKeyToEvent(event.getMessage().code);
        if( eventString == null ) return;

        LoggerProject.logDebug("015001", "Mech event: " + eventString);
        StateEvent stateEvent = new StateEvent(mech, eventString);
        mech.updateState(stateEvent);
    }


}
