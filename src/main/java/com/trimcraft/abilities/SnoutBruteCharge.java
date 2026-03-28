package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SnoutBruteCharge extends TrimAbility {

    public SnoutBruteCharge(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Brute Charge"; }
    @Override public String getDescription() {
        return "Channel the piglin's brute force — charge forward with explosive power, smashing through enemies in your path.";
    }
    @Override public String getCooldownKey() { return "snout-brute-charge"; }
    @Override public int getDefaultCooldown() { return 20; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double power = plugin.getTrimConfig().getDouble("snout.brute-charge.dash-power", 1.8);
        double damage = plugin.getTrimConfig().getDouble("snout.brute-charge.damage", 5.0);

        Vector dir = player.getLocation().getDirection().normalize().multiply(power);
        dir.setY(0.3);
        player.setVelocity(dir);

        ParticleUtils.spawnExplosionRing(player.getLocation(), 220, 100, 20, 1.5);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_HOGLIN_ANGRY, 1f, 0.8f);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Entity entity : player.getNearbyEntities(1.5, 1.5, 1.5)) {
                if (entity == player || !(entity instanceof LivingEntity living)) continue;
                living.damage(damage, player);
                if (entity instanceof Player target) {
                    MessageUtils.sendPrefixedMessage(target, "&cBrute Charge &7— you were run over by &b" + player.getName() + "&c!");
                }
            }
        }, 5L);

        MessageUtils.sendPrefixedMessage(player, "&cBrute Charge &7— unstoppable force unleashed!");
    }
}
