package com.holybuckets.admiralsmechs.entity.weapon;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public abstract class WeaponBase {

    Level level;
    WeaponAnim launchAnim;
    boolean isInstant;

    @Nullable
    Player owner;

    public WeaponBase(Level level, @Nullable Player owner, WeaponAnim anims, boolean isInstant) {
        this.level = level;
        this.owner = owner;
        this.launchAnim = anims;
        this.isInstant = isInstant;
    }

    @Nullable
    Entity fire(Vec3 muzzlePos, Vec3 direction) {
        return null;
    }

    void onImpact(Vec3 pos) {

    }

    public static class WeaponAnim {
        public String launchAnim;
        public String impactAnim;
        public String launchSound;
        public String impactSound;

    }

}
