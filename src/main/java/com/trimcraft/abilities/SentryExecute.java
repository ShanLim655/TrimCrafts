package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SentryExecute extends TrimAbility {

    public SentryExecute(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Execute"; }
    @Override public String getDescription() {
        return "Deliver a heavy finishing strike to nearby enemies already weakened in battle.";
    }
    @Override public String getCooldownKey() { return "sentry-execute"; }
    @Override public int getDefaultCooldown() { return 60; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double threshold = plugin.getTrimConfig().getDouble("sentry.execute.threshold-percent", 20.0);
        double bonusDamage = plugin.getTrimConfig().getDouble("sentry.execute.bonus-damage", 10.0);

        int executed = 0;
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)) {
            if (entity == player || !(entity instanceof LivingEntity living)) continue;
            double maxHp = living.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
            double hpPercent = (living.getHealth() / maxHp) * 100;
            if (hpPercent <= threshold) {
                living.damage(bonusDamage + living.getHealth(), player);
                ParticleUtils.spawnExplosionRing(living.getLocation(), 255, 50, 0, 1.5);
                player.getWorld().playSound(living.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 0.5f);
                executed++;
            }
        }

        if (executed == 0) {
            MessageUtils.sendPrefixedMessage(player, "&cNo enemies below " + (int)threshold + "% HP in range!");
            cooldownManager.clearCooldown(player, getCooldownKey());
        } else {
            MessageUtils.sendPrefixedMessage(player, "&cExecute &7— &e" + executed + " &7sentenced to death!");
        }
    }
}
