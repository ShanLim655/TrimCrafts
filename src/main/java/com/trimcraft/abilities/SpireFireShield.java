package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpireFireShield extends TrimAbility {

    public SpireFireShield(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Fire Shield"; }
    @Override public String getDescription() {
        return "Cloak yourself in a mantle of living fire — granting immunity to flames and scorching any who dare strike you.";
    }
    @Override public String getCooldownKey() { return "spire-fire-shield"; }
    @Override public int getDefaultCooldown() { return 40; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("spire.fire-shield.protection-duration", 100);

        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration / 2, 0, false, true));

        ParticleUtils.spawnAuraRing(player, 255, 120, 0);
        ParticleUtils.spawnFireParticles(player.getLocation().add(0, 1, 0));
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_BLAZE_AMBIENT, 1f, 0.7f);
        MessageUtils.sendPrefixedMessage(player, "&6Fire Shield &7— flames cannot touch you now!");
    }
}
