package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SentryIronWall extends TrimAbility {

    public SentryIronWall(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Iron Wall"; }
    @Override public String getDescription() {
        return "Become an immovable fortress — gain resistance and dramatically reduce incoming damage for a focused defensive window.";
    }
    @Override public String getCooldownKey() { return "sentry-iron-wall"; }
    @Override public int getDefaultCooldown() { return 20; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("sentry.iron-wall.duration-ticks", 120);
        int amp = plugin.getTrimConfig().getInt("sentry.iron-wall.resistance-amplifier", 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, amp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, 0, false, false));

        ParticleUtils.spawnAuraRing(player, 150, 150, 150);
        ParticleUtils.spawnExplosionRing(player.getLocation(), 200, 200, 200, 1.5);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_LAND, 0.8f, 1f);
        MessageUtils.sendPrefixedMessage(player, "&7Iron Wall &7— you are the fortress!");
    }
}
