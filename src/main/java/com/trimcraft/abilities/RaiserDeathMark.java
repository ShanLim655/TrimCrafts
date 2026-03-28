package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RaiserDeathMark extends TrimAbility {

    public RaiserDeathMark(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Death Mark"; }
    @Override public String getDescription() {
        return "Brand a player with a death mark — they take amplified damage from all sources while the rune burns on their soul.";
    }
    @Override public String getCooldownKey() { return "raiser-death-mark"; }
    @Override public int getDefaultCooldown() { return 30; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double range = plugin.getTrimConfig().getDouble("raiser.death-mark.range", 10.0);
        int duration = plugin.getTrimConfig().getInt("raiser.death-mark.duration-ticks", 100);
        int amp = plugin.getTrimConfig().getInt("raiser.death-mark.vulnerability-amplifier", 1);

        Player target = getNearestPlayer(player, range);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cNo target in range!");
            cooldownManager.clearCooldown(player, getCooldownKey());
            return;
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, amp, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, duration, 0, false, true));

        ParticleUtils.spawnColoredDust(target.getLocation().add(0, 1.5, 0), 80, 0, 0, 2f, 30);
        target.getWorld().playSound(target.getLocation(), org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 1f, 0.5f);
        MessageUtils.sendPrefixedMessage(player, "&8Death Mark &7branded on &b" + target.getName() + "&7!");
        MessageUtils.sendPrefixedMessage(target, "&8A death rune &7burns into your soul...");
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
