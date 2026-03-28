package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HostSpectralAnchor extends TrimAbility {

    public HostSpectralAnchor(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Spectral Anchor"; }
    @Override public String getDescription() {
        return "Reach through the spirit realm and yank a nearby player toward you with ghostly chains.";
    }
    @Override public String getCooldownKey() { return "host-spectral-anchor"; }
    @Override public int getDefaultCooldown() { return 35; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double pullDist = plugin.getTrimConfig().getDouble("host.spectral-anchor.pull-distance", 15.0);
        Player target = getNearestPlayer(player, pullDist);

        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cNo target in spectral range!");
            cooldownManager.clearCooldown(player, getCooldownKey());
            return;
        }

        Location from = target.getLocation();
        Location to = player.getLocation().add(0, 1, 0);
        Vector dir = to.toVector().subtract(from.toVector()).normalize().multiply(1.5);
        dir.setY(0.3);
        target.setVelocity(dir);

        // Draw spectral chain
        double dist = from.distance(to);
        int steps = (int)(dist * 3);
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Location p = from.clone().add(
                (to.getX()-from.getX())*t,
                (to.getY()-from.getY())*t,
                (to.getZ()-from.getZ())*t);
            ParticleUtils.spawnColoredDust(p, 180, 180, 255, 1f, 1);
        }

        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GUARDIAN_ATTACK, 1f, 0.5f);
        MessageUtils.sendPrefixedMessage(player, "&7Spectral Anchor &5yanked &b" + target.getName() + " &5to you!");
        MessageUtils.sendPrefixedMessage(target, "&5Spectral chains &7drag you toward &b" + player.getName() + "&7!");
    }

    private Player getNearestPlayer(Player source, double range) {
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
