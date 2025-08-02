package com.holybuckets.admiralsmechs.entity.vehicle;

import com.holybuckets.admiralsmechs.entity.MechMountable;
import com.holybuckets.admiralsmechs.entity.ModEntities;
import com.holybuckets.admiralsmechs.entity.state.State;
import com.holybuckets.admiralsmechs.entity.state.StateEvent;
import com.holybuckets.admiralsmechs.entity.state.TankState;
import com.holybuckets.admiralsmechs.entity.weapon.TankFlameThrower;
import com.holybuckets.admiralsmechs.entity.weapon.TankSmallShot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class TankEntity extends MechMountable {

    public static final float DEFAULT_SCALE = 0.5f;

    static final List<String> STATES = List.of("idle", "forward", "backward", "left_turn", "right_turn");

    protected boolean firePrimary = false;
    protected boolean fireSecondary = false;
    protected int primaryCooldown = 0;
    protected int secondaryCooldown = 0;
    protected TankSmallShot primaryWeapon;
    protected TankFlameThrower secondaryWeapon;

    private static final int PRIMARY_COOLDOWN_TICKS = 25;
    private static final int SECONDARY_COOLDOWN_TICKS = 0;

    public static final String FIRE_PRIMARY_EVENT = "fire_primary";
    public static final String FIRE_SECONDARY_EVENT = "fire_secondary";

    public TankEntity(EntityType<? extends MechMountable> $$0, Level $$1) {
        super($$0, $$1);
        this.currentState = TankState.getInitialState(this);
        this.primaryWeapon = new TankSmallShot($$1, null);
        this.secondaryWeapon = new TankFlameThrower($$1, null);
    }

    public void setFirePrimary(boolean firing) {
        this.firePrimary = firing && this.primaryCooldown <= 0;
    }

    public void setFireSecondary(boolean firing) {
        this.fireSecondary = firing && this.secondaryCooldown <= 0;
    }

    public boolean isFirePrimary() {
        return firePrimary;
    }

    public boolean isFireSecondary() {
        return fireSecondary;
    }


    public Vec3 getMuzzlePosition() {
        //Move 1 unit in x and z direction proportional to this.orientation
        double yRot = this.orientation.y;
        if(this.getDriver() != null)
            yRot = this.getDriver().getYRot();
        double yaw = Math.toRadians(yRot);
        double xOffset = -(Math.sin(yaw)*2);
        double zOffset = Math.cos(yaw)*2;
        return this.position().add(xOffset, 1.8, zOffset);
    }

    /**
     * x - pitch, y - yaw
     * @return
     */
    @Nullable
    public Vec3 getMuzzleAngles() {
        if(this.getDriver() == null) return null;
        Player p = this.getDriver();
        return new Vec3(p.getXRot(), p.getYRot(), 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(Attributes.ATTACK_KNOCKBACK)
            .add(Attributes.MAX_HEALTH, ModEntities.MECH_HEALTH);
    }

    //** BASIC FROM SUPER

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }


    //* RIDING

    @Override
    protected Vec3 getRiddenInput(Player $$0, Vec3 $$1) {
        if(this.getControllingPassenger() instanceof Player) {
            if(this.currentState == TankState.FORWARD) {
                return new Vec3(1, 0, 0);
            }
        }
        return Vec3.ZERO;
    }

    @Override
    protected float getRiddenSpeed(Player $$0) {
        return super.getRiddenSpeed($$0);
    }


    @Override
    public double getPassengersRidingOffset() {
        return 2.5F;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        if (this.getDriver()  == null) {
            if (isServerSide()) {
                player.setYRot(this.getYRot());
                player.setXRot(this.getXRot());
                player.startRiding(this);
            }

            this.updateState( new StateEvent(this, "idle") );
            this.primaryWeapon = new TankSmallShot(this.level(), player);
            this.secondaryWeapon = new TankFlameThrower(this.level(), player);
        }


        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    private void handleWeaponsOnTick() {
       if( this.firePrimary ) {
            this.primaryWeapon.fire( this.getMuzzlePosition(), getMuzzleAngles() );
            this.primaryCooldown = PRIMARY_COOLDOWN_TICKS;
            this.firePrimary = false;
       } else if( this.fireSecondary ) {
            this.secondaryWeapon.fire( this.getMuzzlePosition(), getMuzzleAngles() );
            this.secondaryCooldown = SECONDARY_COOLDOWN_TICKS;
            this.fireSecondary = false;
       } else {
            if(this.primaryCooldown > 0)
                this.primaryCooldown--;

            if(this.secondaryCooldown > 0)
                this.secondaryCooldown--;
        }

    }

    //** TICKING METHODS

    @Override
    public void tick() {
        this.handleWeaponsOnTick();
        super.tick();
    }

    private static int cCount = 0;
    private static Vec3 LEFT_TURN = new Vec3(0, -3F, 0);
    private static Vec3 RIGHT_TURN = new Vec3(0, 3F, 0);
    @Override
    public void travel(Vec3 pos) {
        if (this.isAlive() && this.isVehicle()) {
            Player p = this.getDriver();
            if (p == null) return;

            float forward = p.zza; // forward/backward throttle

            if(this.level().isClientSide()) {
                if(cCount % 80 == 0) {
                    int i = 0;
                }
            } else {
                if(cCount % 80 == 0) {
                    int i = 0;
                }
            }
            cCount++;


            // Handle tank rotation using A/D keys
            if (this.currentState == TankState.LEFT_TURN) {
                this.updateOrientation(LEFT_TURN);
                //this.originalTravel(Vec3.ZERO);
                //return;
            } else if (this.currentState == TankState.RIGHT_TURN) {
                this.updateOrientation(RIGHT_TURN);
                //this.originalTravel(Vec3.ZERO);
                //return;
            } else {
                this.updateOrientation(Vec3.ZERO);
            }

            // Convert forward throttle into world-relative movement

            float yawRad = (float) Math.toRadians(this.orientation.y);
            double dx = forward * Math.sin(yawRad);
            double dz =  forward * Math.cos(yawRad);

            float f = p.xxa * 0.5F;
            float f1 = p.zza * 0.5F;

            //Vec3 delta = new Vec3(dx, pos.y, dz);
            //Vec3 delta = new Vec3(f, pos.y, f1);
            Vec3 delta = new Vec3(0, pos.y, f1);
            this.originalTravel(delta);
            this.hasImpulse = true;

        } else {
            //originalTravel(pos);
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }


    //** ANIMATIONS
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add( new AnimationController<>(this, "tank", state -> PlayState.STOP)
            .triggerableAnim("forward", RawAnimation.begin().thenLoop(a("forward")))
            .triggerableAnim("backward", RawAnimation.begin().thenLoop(a("backward")))
            .triggerableAnim("left_turn", RawAnimation.begin().thenPlay(a("left_turn")))
            .triggerableAnim("right_turn", RawAnimation.begin().thenPlay(a("right_turn")))
            .triggerableAnim("idle", RawAnimation.begin().thenLoop(a("idle"))));
    }
        private String a(String s) { return "animation.ironclad." + s; }

    @Override
    protected String mapKeyToEvent(int key) {
        String parentState = super.mapKeyToEvent(key);
        //Specific entity abilities
        return parentState;
    }


        /**
         * Update state on client side, updates server side if state is new
         * @param event
         */
        protected void updateState(StateEvent event) {
            State newState = this.currentState.update( event, this );
            if (newState != this.currentState) {
                this.currentState.exit(this);
                this.currentState = newState;
                this.currentState.enter(this);
            }
        }


}
