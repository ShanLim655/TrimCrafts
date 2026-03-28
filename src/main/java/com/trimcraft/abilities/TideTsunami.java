package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.Sound;

public class TideTsunami extends TrimAbility {

    public TideTsunami(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Tsunami"; }
    @Override public String getDescription() {
        return "Call a devastating tsunami wave that obliterates all enemies in a wide radius with crushing water pressure.";
    }
    @Override public String getCooldownKey() { return "tide-tsunami"; }
    @Override public int getDefaultCooldown() { return 30; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = plugin.getTrimConfig().getDouble("tide.tsunami.wave-radius", 10.0);
        double knockback = plugin.getTrimConfig().getDouble("tide.tsunami.knockback-power", 3.0);

        Location center = player.getLocation();
        ParticleUtils.spawnSplashWater(center);
        ParticleUtils.spawnExplosionRing(center, 0, 100, 200, radius / 2);
        player.getWorld().playSound(center, org.bukkit.Sound.WEATHER_RAIN_ABOVE, 2f, 0.5f);

        for (Entity entity : player.getWorld().getNearbyEntities(center, radius, radius / 2, radius)) {
            if (entity == player || !(entity instanceof LivingEntity)) continue;
            Vector dir = entity.getLocation().toVector().subtract(center.toVector()).normalize();
            dir.setY(0.6);
            entity.setVelocity(dir.multiply(knockback));
            if (entity instanceof Player target) {
                MessageUtils.sendPrefixedMessage(target, "&bTsunami &7— you are consumed by the wave!");
            }
        }
        MessageUtils.sendPrefixedMessage(player, "&bTsunami &7— the ocean's wrath is unleashed!");
    }
}
