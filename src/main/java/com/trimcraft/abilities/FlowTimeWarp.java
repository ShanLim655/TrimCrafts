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

public class FlowTimeWarp extends TrimAbility {

    public FlowTimeWarp(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Time Warp"; }
    @Override public String getDescription() {
        return "Warp the flow of time — enemies around you are plunged into slow motion while you accelerate with haste.";
    }
    @Override public String getCooldownKey() { return "flow-time-warp"; }
    @Override public int getDefaultCooldown() { return 40; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = plugin.getTrimConfig().getDouble("flow.time-warp.radius", 8.0);
        int slowDur = plugin.getTrimConfig().getInt("flow.time-warp.slow-duration-ticks", 60);
        int hasteAmp = plugin.getTrimConfig().getInt("flow.time-warp.haste-amplifier", 2);

        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, slowDur, hasteAmp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, slowDur, 2, false, true));

        Location center = player.getLocation();
        for (Entity entity : player.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity == player || !(entity instanceof LivingEntity living)) continue;
            if (entity instanceof Player target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDur, 4, false, true));
                target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, slowDur, 2, false, true));
                MessageUtils.sendPrefixedMessage(target, "&9Time Warp &7— time slows around you!");
            }
        }

        ParticleUtils.spawnExplosionRing(center, 0, 150, 255, radius / 2);
        ParticleUtils.spawnEnchantmentParticles(player);
        player.getWorld().playSound(center, org.bukkit.Sound.BLOCK_BEACON_POWER_SELECT, 1f, 0.5f);
        MessageUtils.sendPrefixedMessage(player, "&9Time Warp &7— reality stutters around you!");
    }
}
