package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class CoastTidalWave extends TrimAbility {

    public CoastTidalWave(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Tidal Wave"; }
    @Override public String getDescription() {
        return "Summon a crashing tidal wave that hurls nearby enemies away and slows them with ocean currents.";
    }
    @Override public String getCooldownKey() { return "coast-tidal-wave"; }
    @Override public int getDefaultCooldown() { return 25; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = plugin.getTrimConfig().getDouble("coast.tidal-wave.radius", 8.0);
        double knockback = plugin.getTrimConfig().getDouble("coast.tidal-wave.knockback-power", 2.5);
        int slowDur = plugin.getTrimConfig().getInt("coast.tidal-wave.slowness-duration", 60);

        Location center = player.getLocation();
        ParticleUtils.spawnSplashWater(center);
        player.getWorld().playSound(center, org.bukkit.Sound.ENTITY_GENERIC_SPLASH, 2f, 0.5f);

        for (Entity entity : player.getWorld().getNearbyEntities(center, radius, radius / 2, radius)) {
            if (entity == player || !(entity instanceof LivingEntity living)) continue;
            Vector dir = entity.getLocation().toVector().subtract(center.toVector()).normalize();
            dir.setY(0.4);
            entity.setVelocity(dir.multiply(knockback));
            if (entity instanceof Player target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDur, 1, false, true));
                MessageUtils.sendPrefixedMessage(target, "&bA tidal wave &7swept you away!");
            }
        }

        ParticleUtils.spawnExplosionRing(center, 0, 100, 200, radius / 2);
        MessageUtils.sendPrefixedMessage(player, "&bTidal Wave &7unleashed!");
    }
}
