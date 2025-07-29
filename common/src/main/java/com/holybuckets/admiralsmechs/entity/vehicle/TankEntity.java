package com.holybuckets.admiralsmechs.entity.vehicle;

import com.holybuckets.admiralsmechs.entity.MechMountable;
import com.holybuckets.admiralsmechs.entity.ModEntities;
import com.holybuckets.admiralsmechs.entity.state.State;
import com.holybuckets.admiralsmechs.entity.state.StateEvent;
import com.holybuckets.admiralsmechs.entity.state.TankState;
import net.minecraft.core.NonNullList;
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
    TankState currentState;

    public TankEntity(EntityType<? extends MechMountable> $$0, Level $$1) {
        super($$0, $$1);
        this.currentState = TankState.getInitialState(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(Attributes.ATTACK_KNOCKBACK)
            .add(Attributes.MAX_HEALTH, ModEntities.MECH_HEALTH);
    }


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


    @Override
    public double getPassengersRidingOffset() {
        return 2.5F;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.getControllingPassenger()  == null) {
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
            TankState newState = this.currentState.update( event );
            if (newState != this.currentState) {
                this.currentState.exit(this);
                this.currentState = newState;
                this.currentState.enter(this);
            }
        }


}
