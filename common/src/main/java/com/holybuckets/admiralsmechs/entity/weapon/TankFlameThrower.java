package com.holybuckets.admiralsmechs.entity.weapon;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TankFlameThrower extends WeaponBase {

    public static final String LAUNCH_ANIM = "tank_flame_launch";
    public static final String IMPACT_ANIM = "tank_flame_impact";
    public static final String LAUNCH_SOUND = "tank_flame_launch";
    public static final String IMPACT_SOUND = "tank_flame_impact";
    public static final boolean IS_INSTANT = false;

    private static WeaponAnim ANIMS = new WeaponAnim();
    static {
        ANIMS.launchAnim = LAUNCH_ANIM;
        ANIMS.impactAnim = IMPACT_ANIM;
        ANIMS.launchSound = LAUNCH_SOUND;
        ANIMS.impactSound = IMPACT_SOUND;
    }

    private static final float PROJECTILE_SPEED = 1.5f;

    public TankFlameThrower(Level level, Player owner) {
        super(level, owner, ANIMS, false);
    }

    @Override
    @Nullable
    public SmallFireball fire(Vec3 muzzlePos, Vec3 direction) {
        if(this.level.isClientSide()) {
            this.level.playLocalSound(muzzlePos.x, muzzlePos.y, muzzlePos.z, 
                SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 
                1.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, 
                false);
            return null;
        }

        // Convert angles to radians
        float yawRad = (float) Math.toRadians(direction.y);
        float pitchRad = (float) Math.toRadians(direction.x);
        float clampedPitch = Mth.clamp((float) (-direction.x*0.5), -10.0F, 20.0F);

        // Calculate direction vector
        double x = -(Math.sin(yawRad) * Math.cos(pitchRad));
        double y = Math.sin(clampedPitch);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        SmallFireball fireball = new SmallFireball(
            level, 
            muzzlePos.x, muzzlePos.y, muzzlePos.z,
            x * PROJECTILE_SPEED,
            y * PROJECTILE_SPEED,
            z * PROJECTILE_SPEED
        );

        level.addFreshEntity(fireball);
        return fireball;
    }

    @Override
    public void onImpact(Vec3 pos) {
        // Fire charges handle their own impact
    }
}
