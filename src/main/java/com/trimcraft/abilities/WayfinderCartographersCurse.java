package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WayfinderCartographersCurse extends TrimAbility {

    public WayfinderCartographersCurse(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Cartographer's Curse"; }
    @Override public String getDescription() {
        return "Curse a target with dimensional disorientation — their directions are scrambled, their world upturned and nauseating.";
    }
    @Override public String getCooldownKey() { return "wayfinder-cartographers-curse"; }
    @Override public int getDefaultCooldown() { return 50; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double range = plugin.getTrimConfig().getDouble("wayfinder.cartographers-curse.range", 12.0);
        int duration = plugin.getTrimConfig().getInt("wayfinder.cartographers-curse.disorientation-duration", 200);

        Player target = getNearestPlayer(player, range);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cNo target in range!");
            cooldownManager.clearCooldown(player, getCooldownKey());
            return;
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration / 4, 0, false, true));

        ParticleUtils.spawnPortalVortex(target.getLocation().add(0, 1, 0));
        target.getWorld().playSound(target.getLocation(), org.bukkit.Sound.BLOCK_PORTAL_AMBIENT, 0.5f, 2f);
        MessageUtils.sendPrefixedMessage(player, "&dCartographer's Curse &7inflicted on &b" + target.getName() + "&7!");
        MessageUtils.sendPrefixedMessage(target, "&dYour sense of direction has been &5cursed&d!");
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
