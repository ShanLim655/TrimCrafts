package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SpireInfernoStrike extends TrimAbility {

    public SpireInfernoStrike(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Inferno Strike"; }
    @Override public String getDescription() {
        return "Unleash a lance of nether flame — setting nearby enemies ablaze and scorching them with direct damage.";
    }
    @Override public String getCooldownKey() { return "spire-inferno-strike"; }
    @Override public int getDefaultCooldown() { return 25; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        int fireTicks = plugin.getTrimConfig().getInt("spire.inferno-strike.fire-ticks", 80);
        double damage = plugin.getTrimConfig().getDouble("spire.inferno-strike.damage", 3.0);
        double range = plugin.getTrimConfig().getDouble("spire.inferno-strike.range", 5.0);

        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), range, range, range)) {
            if (entity == player || !(entity instanceof LivingEntity living)) continue;
            living.setFireTicks(fireTicks);
            living.damage(damage, player);
            ParticleUtils.spawnFireParticles(living.getLocation().add(0, 1, 0));
            if (entity instanceof Player target) {
                MessageUtils.sendPrefixedMessage(target, "&cInferno Strike &7— you burn in nether flames!");
            }
        }

        ParticleUtils.spawnExplosionRing(player.getLocation(), 255, 100, 0, range / 2);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_BLAZE_SHOOT, 1f, 0.8f);
        MessageUtils.sendPrefixedMessage(player, "&cInferno Strike &7— nether fire erupts!");
    }
}
