package com.holybuckets.admiralsmechs.entity.vehicle;

import com.holybuckets.admiralsmechs.entity.MechMountable;
import com.holybuckets.admiralsmechs.entity.ModEntities;
import com.holybuckets.admiralsmechs.entity.state.State;
import com.holybuckets.admiralsmechs.entity.state.StateEvent;
import com.holybuckets.admiralsmechs.entity.state.TankState;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Zombie;
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

import java.util.List;

public class TankEntity extends MechMountable {

    public static final float DEFAULT_SCALE = 0.5f;

    static final List<String> STATES = List.of("idle", "forward", "backward", "left-turn", "right-turn");


    public TankEntity(EntityType<? extends MechMountable> $$0, Level $$1) {
        super($$0, $$1);
        this.currentState = TankState.getInitialState(this);
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
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.getDriver()  == null) {
            if (isServerSide()) {
                player.setYRot(this.getYRot());
                player.setXRot(this.getXRot());
                player.startRiding(this);
            }

            this.updateState( new StateEvent(this, "idle") );
        }

        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void travel(Vec3 pos)
    {
        if (this.isAlive() && this.isVehicle()) {
            Player p = this.getDriver();
            if( p == null ) return;
            //ServerPlayer pilot = (ServerPlayer) p;

            // Update orientation based on current state
            if (this.currentState == TankState.LEFT_TURN) {
                this.updateOrientation(new Vec3(0, -1, 0));
            } else if (this.currentState == TankState.RIGHT_TURN) {
                this.updateOrientation(new Vec3(0, 1, 0));
            }

            // ROTATE Vehicle based on orientation
            this.setYRot((float)this.orientation.y);
            this.yRotO = this.getYRot();
            this.setXRot((float)this.orientation.x);
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
            .triggerableAnim("left-turn", RawAnimation.begin().thenPlay(a("left-turn")))
            .triggerableAnim("right-turn", RawAnimation.begin().thenPlay(a("right-turn")))
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
            State newState = this.currentState.update( event );
            if (newState != this.currentState) {
                this.currentState.exit(this);
                this.currentState = newState;
                this.currentState.enter(this);
            }
        }


}
