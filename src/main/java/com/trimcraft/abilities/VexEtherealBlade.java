package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class VexEtherealBlade extends TrimAbility {

    public VexEtherealBlade(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Ethereal Blade"; }
    @Override public String getDescription() {
        return "Conjure a blade of pure vex energy that phases through armor, dealing direct damage that ignores all protection.";
    }
    @Override public String getCooldownKey() { return "vex-ethereal-blade"; }
    @Override public int getDefaultCooldown() { return 35; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double damage = plugin.getTrimConfig().getDouble("vex.ethereal-blade.damage", 8.0);
        double range = plugin.getTrimConfig().getDouble("vex.ethereal-blade.range", 6.0);

        Player target = getTargetPlayer(player, range);
        if (target == null) {
            for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), range, range, range)) {
                if (entity == player || !(entity instanceof LivingEntity living)) continue;
                living.damage(damage);
                ParticleUtils.spawnColoredDust(living.getLocation().add(0, 1, 0), 150, 150, 255, 1.5f, 20);
                break;
            }
        } else {
            target.damage(damage);
            ParticleUtils.spawnColoredDust(target.getLocation().add(0, 1, 0), 150, 150, 255, 1.5f, 20);
            MessageUtils.sendPrefixedMessage(target, "&fEthereal Blade &7phases through your armor!");
        }

        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VEX_CHARGE, 1f, 1f);
        MessageUtils.sendPrefixedMessage(player, "&fEthereal Blade &7— armor is no barrier!");
    }

    private Player getTargetPlayer(Player source, double range) {
        Player nearest = null;
        double dist = range * range;
        for (Player p : source.getWorld().getPlayers()) {
            if (p == source) continue;
            double d = source.getLocation().distanceSquared(p.getLocation());
            if (d < dist) { dist = d; nearest = p; }
        }
        return nearest;
    }
}
