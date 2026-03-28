package com.trimcraft.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class ParticleUtils {

    public static void spawnColoredDust(Location loc, int r, int g, int b, float size, int count) {
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(r, g, b), size);
        loc.getWorld().spawnParticle(Particle.DUST, loc, count, 0.5, 0.5, 0.5, 0, dust);
    }

    public static void spawnAuraRing(Player player, int r, int g, int b) {
        Location center = player.getLocation().add(0, 1, 0);
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 8) {
            double x = Math.cos(angle) * 1.2;
            double z = Math.sin(angle) * 1.2;
            spawnColoredDust(center.clone().add(x, 0, z), r, g, b, 1.2f, 1);
        }
    }

    public static void spawnSoulFlames(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 20, 0.5, 0.5, 0.5, 0.05);
    }

    public static void spawnDrainBeam(Player source, Player target) {
        Location from = source.getLocation().add(0, 1, 0);
        Location to = target.getLocation().add(0, 1, 0);
        double distance = from.distance(to);
        int steps = (int) (distance * 4);
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Location point = from.clone().add(
                    (to.getX() - from.getX()) * t,
                    (to.getY() - from.getY()) * t,
                    (to.getZ() - from.getZ()) * t
            );
            spawnColoredDust(point, 150, 0, 200, 1.0f, 1);
        }
    }

    public static void spawnExplosionRing(Location center, int r, int g, int b, double radius) {
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            spawnColoredDust(center.clone().add(x, 0.1, z), r, g, b, 1.5f, 1);
        }
    }

    public static void spawnLightningParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0);
    }

    public static void spawnEnchantmentParticles(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 30, 0.5, 1, 0.5, 1);
    }

    public static void spawnFireParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 20, 0.3, 0.3, 0.3, 0.05);
    }

    public static void spawnSplashWater(Location loc) {
        loc.getWorld().spawnParticle(Particle.SPLASH, loc, 50, 1, 0.5, 1, 0.1);
    }

    public static void spawnPortalVortex(Location loc) {
        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 50, 0.5, 1, 0.5, 0.5);
    }

    public static void spawnSandParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.FALLING_DUST,
                loc, 30, 1, 0.5, 1, 0.1,
                org.bukkit.Material.SAND.createBlockData());
    }

    public static void spawnGreenMagic(Location loc) {
        spawnColoredDust(loc, 0, 200, 50, 1.2f, 20);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc, 10, 0.5, 0.5, 0.5, 0);
    }

    public static void spawnDeathParticles(Location loc) {
        loc.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc, 30, 0.5, 1, 0.5, 0.2);
    }
}
