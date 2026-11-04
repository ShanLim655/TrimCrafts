package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.CombatUtils;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DuneSandstorm extends TrimAbility {

    public DuneSandstorm(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Sandstorm"; }
    @Override public String getDescription() {
        return "Conjure a blinding desert storm around you, damaging and disorientating all nearby foes.";
    }
    @Override public String getCooldownKey() { return "dune-sandstorm"; }
    @Override public int getDefaultCooldown() { return 30; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = plugin.getTrimConfig().getDouble("dune.sandstorm.radius", 6.0);
        int blindDur = plugin.getTrimConfig().getInt("dune.sandstorm.blindness-duration", 60);
        int slowDur = plugin.getTrimConfig().getInt("dune.sandstorm.slowness-duration", 80);
        double damage = plugin.getTrimConfig().getDouble("dune.sandstorm.damage", 2.0);

        Location center = player.getLocation();
        ParticleUtils.spawnSandParticles(center);
        player.getWorld().playSound(center, org.bukkit.Sound.BLOCK_SAND_BREAK, 2f, 0.5f);

        for (Entity entity : player.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living) || !CombatUtils.canTarget(player, entity)) continue;
            living.damage(damage, player);
            if (entity instanceof Player target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDur, 0, false, true));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDur, 1, false, true));
                MessageUtils.sendPrefixedMessage(target, "&eA sandstorm &7blinds your sight!");
            }
        }
        MessageUtils.sendPrefixedMessage(player, "&eSandstorm &7erupts around you!");
    }
}
