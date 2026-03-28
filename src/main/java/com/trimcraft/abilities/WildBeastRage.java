package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WildBeastRage extends TrimAbility {

    public WildBeastRage(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Beast Rage"; }
    @Override public String getDescription() {
        return "Unleash primal fury, granting strength, speed and a savage damage boost for 6 seconds.";
    }
    @Override public String getCooldownKey() { return "wild-beast-rage"; }
    @Override public int getDefaultCooldown() { return 35; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("wild.beast-rage.duration-ticks", 120);
        int strAmp = plugin.getTrimConfig().getInt("wild.beast-rage.strength-amplifier", 2);
        int spdAmp = plugin.getTrimConfig().getInt("wild.beast-rage.speed-amplifier", 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, strAmp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, spdAmp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, 1, false, true));

        ParticleUtils.spawnExplosionRing(player.getLocation(), 180, 80, 0, 2.0);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_RAVAGER_ROAR, 1f, 0.8f);
        MessageUtils.sendPrefixedMessage(player, "&cBeast Rage &7— the beast within awakens!");
    }
}
