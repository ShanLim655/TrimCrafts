package com.trimcraft.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public final class TargetUtils {

    private TargetUtils() {
    }

    public static Player getLookTargetPlayer(Player source, double range) {
        Location eye = source.getEyeLocation();
        RayTraceResult result = source.getWorld().rayTraceEntities(
                eye,
                eye.getDirection(),
                Math.max(0.1, range),
                0.65,
                entity -> entity instanceof Player && CombatUtils.canTarget(source, entity)
        );

        if (result == null || !(result.getHitEntity() instanceof Player target)) return null;
        return source.hasLineOfSight(target) ? target : null;
    }

    public static LivingEntity getLookTargetLivingEntity(Player source, double range) {
        Location eye = source.getEyeLocation();
        RayTraceResult result = source.getWorld().rayTraceEntities(
                eye,
                eye.getDirection(),
                Math.max(0.1, range),
                0.55,
                entity -> entity instanceof LivingEntity && CombatUtils.canTarget(source, entity)
        );

        if (result == null || !(result.getHitEntity() instanceof LivingEntity target)) return null;
        return source.hasLineOfSight(target) ? target : null;
    }
}
