package com.holybuckets.admiralsmechs.entity.vehicle;

import com.holybuckets.admiralsmechs.entity.ModEntities;
import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Iterator;
import java.util.List;

public class TankEntity extends LivingEntity implements GeoEntity, GeoAnimatable {

    public static final float DEFAULT_SCALE = 0.5f;

    static final List<String> STATES = List.of("idle", "forward", "backward", "left-turn", "right-turn");
    int currentState = 0;

    public TankEntity(EntityType<? extends LivingEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
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
        controllers.add(
            new AnimationController<TankEntity>(this, "tank", 0, state -> {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold( a(STATES.get(this.currentState))));
            })
        );
    }
        private static String ANIM_NAME = "ironclad";
        private String a(String s) { return  ANIM_NAME + "." + s; }


    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.currentState = (this.currentState + 1) % STATES.size();
            LivingEntity entity = this.getControllingPassenger();
            if( entity == null || !entity.isAlive() || !(entity instanceof  Player) ) {
                this.currentState = 0; // reset to idle if no player
                return;
            }
            Player p = (Player) entity;
            //If player is presseing forward, then forward , etc

        }
    }

        /**
         * Update state on client side, updates server side if state is new
         * @param state
         */
        private void updateState(int state) {
            if( state == this.currentState ) return;
            this.currentState = state;
            if(this.level().isClientSide) {
                //send to server
            }

        }


}
