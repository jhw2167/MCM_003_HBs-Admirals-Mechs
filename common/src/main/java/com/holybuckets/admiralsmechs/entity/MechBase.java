package com.holybuckets.admiralsmechs.entity;

import com.holybuckets.foundation.event.EventRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public abstract class MechBase extends LivingEntity implements GeoEntity, GeoAnimatable {

    /**
     * Register all mech events from subclasses
     * @param reg
     */
    public static void init(EventRegistrar reg) {
        reg.registerOnClientInput(MechMountable::updateFromClient);
    }

    public void originalTravel(Vec3 $$0) {
        if (this.isControlledByLocalInstance()) {
            double $$1 = 0.08;
            boolean $$2 = this.getDeltaMovement().y <= 0.0;
            if ($$2 && this.hasEffect(MobEffects.SLOW_FALLING)) {
                $$1 = 0.01;
            }

            FluidState $$3 = this.level().getFluidState(this.blockPosition());
            float $$5;
            double $$4;
            if (this.isInWater() && this.isAffectedByFluids() && !this.canStandOnFluid($$3)) {
                $$4 = this.getY();
                $$5 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                float $$6 = 0.02F;
                float $$7 = (float) EnchantmentHelper.getDepthStrider(this);
                if ($$7 > 3.0F) {
                    $$7 = 3.0F;
                }

                if (!this.onGround()) {
                    $$7 *= 0.5F;
                }

                if ($$7 > 0.0F) {
                    $$5 += (0.54600006F - $$5) * $$7 / 3.0F;
                    $$6 += (this.getSpeed() - $$6) * $$7 / 3.0F;
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    $$5 = 0.96F;
                }

                this.moveRelative($$6, $$0);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 $$8 = this.getDeltaMovement();
                if (this.horizontalCollision && this.onClimbable()) {
                    $$8 = new Vec3($$8.x, 0.2, $$8.z);
                }

                this.setDeltaMovement($$8.multiply((double)$$5, 0.800000011920929, (double)$$5));
                Vec3 $$9 = this.getFluidFallingAdjustedMovement($$1, $$2, this.getDeltaMovement());
                this.setDeltaMovement($$9);
                if (this.horizontalCollision && this.isFree($$9.x, $$9.y + 0.6000000238418579 - this.getY() + $$4, $$9.z)) {
                    this.setDeltaMovement($$9.x, 0.30000001192092896, $$9.z);
                }
            } else if (this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid($$3)) {
                $$4 = this.getY();
                this.moveRelative(0.02F, $$0);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 $$12;
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.800000011920929, 0.5));
                    $$12 = this.getFluidFallingAdjustedMovement($$1, $$2, this.getDeltaMovement());
                    this.setDeltaMovement($$12);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
                }

                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -$$1 / 4.0, 0.0));
                }

                $$12 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree($$12.x, $$12.y + 0.6000000238418579 - this.getY() + $$4, $$12.z)) {
                    this.setDeltaMovement($$12.x, 0.30000001192092896, $$12.z);
                }
            } else if (this.isFallFlying()) {
                this.checkSlowFallDistance();
                Vec3 $$13 = this.getDeltaMovement();
                Vec3 $$14 = this.getLookAngle();
                $$5 = this.getXRot() * 0.017453292F;
                double $$16 = Math.sqrt($$14.x * $$14.x + $$14.z * $$14.z);
                double $$17 = $$13.horizontalDistance();
                double $$18 = $$14.length();
                double $$19 = Math.cos((double)$$5);
                $$19 = $$19 * $$19 * Math.min(1.0, $$18 / 0.4);
                $$13 = this.getDeltaMovement().add(0.0, $$1 * (-1.0 + $$19 * 0.75), 0.0);
                double $$22;
                if ($$13.y < 0.0 && $$16 > 0.0) {
                    $$22 = $$13.y * -0.1 * $$19;
                    $$13 = $$13.add($$14.x * $$22 / $$16, $$22, $$14.z * $$22 / $$16);
                }

                if ($$5 < 0.0F && $$16 > 0.0) {
                    $$22 = $$17 * (double)(-Mth.sin($$5)) * 0.04;
                    $$13 = $$13.add(-$$14.x * $$22 / $$16, $$22 * 3.2, -$$14.z * $$22 / $$16);
                }

                if ($$16 > 0.0) {
                    $$13 = $$13.add(($$14.x / $$16 * $$17 - $$13.x) * 0.1, 0.0, ($$14.z / $$16 * $$17 - $$13.z) * 0.1);
                }

                this.setDeltaMovement($$13.multiply(0.9900000095367432, 0.9800000190734863, 0.9900000095367432));
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.horizontalCollision && !this.level().isClientSide) {
                    $$22 = this.getDeltaMovement().horizontalDistance();
                    double $$23 = $$17 - $$22;
                    float $$24 = (float)($$23 * 10.0 - 3.0);
                    if ($$24 > 0.0F) {
                        //this.playSound(this.getFallDamageSound((int)$$24), 1.0F, 1.0F);
                        this.hurt(this.damageSources().flyIntoWall(), $$24);
                    }
                }

                if (this.onGround() && !this.level().isClientSide) {
                    this.setSharedFlag(7, false);
                }
            } else {
                BlockPos $$25 = this.getBlockPosBelowThatAffectsMyMovement();
                float $$26 = this.level().getBlockState($$25).getBlock().getFriction();
                $$5 = this.onGround() ? $$26 * 0.91F : 0.91F;
                Vec3 $$28 = this.handleRelativeFrictionAndCalculateMovement($$0, $$26);
                double $$29 = $$28.y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    $$29 += (0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - $$28.y) * 0.2;
                } else if (this.level().isClientSide && !this.level().hasChunkAt($$25)) {
                    if (this.getY() > (double)this.level().getMinBuildHeight()) {
                        $$29 = -0.1;
                    } else {
                        $$29 = 0.0;
                    }
                } else if (!this.isNoGravity()) {
                    $$29 -= $$1;
                }

                if (this.shouldDiscardFriction()) {
                    this.setDeltaMovement($$28.x, $$29, $$28.z);
                } else {
                    this.setDeltaMovement($$28.x * (double)$$5, $$29 * 0.9800000190734863, $$28.z * (double)$$5);
                }
            }
        }

        this.calculateEntityAnimation(this instanceof FlyingAnimal);
    }

    public MechBase(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

}
