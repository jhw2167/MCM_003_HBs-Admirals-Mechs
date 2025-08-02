package com.holybuckets.admiralsmechs.entity.weapon;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class TankSmallShot extends WeaponBase {

    public static final String LAUNCH_ANIM = "tank_small_shot_launch";
    public static final String IMPACT_ANIM = "tank_small_shot_impact";
    public static final String LAUNCH_SOUND = "tank_small_shot_launch";
    public static final String IMPACT_SOUND = "tank_small_shot_impact";
    public static final boolean IS_INSTANT = false;

    private static WeaponAnim ANIMS = new WeaponAnim();
    static {
        ANIMS.launchAnim = LAUNCH_ANIM;
        ANIMS.impactAnim = IMPACT_ANIM;
        ANIMS.launchSound = LAUNCH_SOUND;
        ANIMS.impactSound = IMPACT_SOUND;
    }


    public TankSmallShot(Level level, Player owner) {
        super(level, owner, ANIMS, false);
    }

    private static int PLAYER_RADIUS = 20;
    private static final float PROJECTILE_SPEED = 2.0f; // Speed of the projectile
    @Override @Nullable
    public PrimedTnt fire(Vec3 muzzlePos, Vec3 direction) {
        //1. Play TNT generic explode sound
        //2. Spawn generic explosion effect
        //3. Launch live tnt from the muzzle position in the direction

        AABB playerAABB = new AABB(
            muzzlePos.x - PLAYER_RADIUS, muzzlePos.y - (PLAYER_RADIUS*0.5), muzzlePos.z - PLAYER_RADIUS,
            muzzlePos.x + PLAYER_RADIUS, muzzlePos.y + (PLAYER_RADIUS*0.5), muzzlePos.z + PLAYER_RADIUS
        );

        if(this.level.isClientSide())
        {
            this.level.playLocalSound(muzzlePos.x, muzzlePos.y, muzzlePos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);

        }
        else {

            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, muzzlePos.x, muzzlePos.y, muzzlePos.z, 1.0, 0.0, 0.0);

            PrimedTnt tnt = new PrimedTnt(
                level, muzzlePos.x, muzzlePos.y, muzzlePos.z,
                this.owner
            );

            // Set fuse time (default = 80 ticks = 4 seconds)
            tnt.setFuse(20);

            // Convert angles to radians
            float yawRad = (float) Math.toRadians(direction.y);
            float clampedPitch = Mth.clamp((float) (-direction.x*0.5), -10.0F, 20.0F);
            float pitchRad = (float) Math.toRadians(clampedPitch);

            // Build direction vector
            double x = -(Math.sin(yawRad) * Math.cos(pitchRad));
            double y = Math.sin(pitchRad);
            double z = Math.cos(yawRad) * Math.cos(pitchRad);

            tnt.setDeltaMovement(
                x * PROJECTILE_SPEED,
                y * PROJECTILE_SPEED,
                z * PROJECTILE_SPEED
            );

            level.addFreshEntity(tnt);
            return tnt;
        }

        return null;
    }

    @Override
    public void onImpact(Vec3 pos) {
        // Implement impact logic, such as applying damage to entities in the area
    }
}
