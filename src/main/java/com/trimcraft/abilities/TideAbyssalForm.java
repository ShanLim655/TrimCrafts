package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TideAbyssalForm extends TrimAbility {

    public TideAbyssalForm(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Abyssal Form"; }
    @Override public String getDescription() {
        return "Descend into the abyss — transform into a creature of the deep, gaining unmatched speed and power underwater.";
    }
    @Override public String getCooldownKey() { return "tide-abyssal-form"; }
    @Override public int getDefaultCooldown() { return 50; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("tide.abyssal-form.duration-ticks", 200);
        int waterBr = plugin.getTrimConfig().getInt("tide.abyssal-form.water-breathing-duration", 400);
        int speedAmp = plugin.getTrimConfig().getInt("tide.abyssal-form.speed-in-water-amplifier", 2);

        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, waterBr, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, speedAmp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 2, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, duration, 0, false, true));

        ParticleUtils.spawnAuraRing(player, 0, 50, 150);
        ParticleUtils.spawnSplashWater(player.getLocation());
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.AMBIENT_UNDERWATER_ENTER, 1f, 0.8f);
        MessageUtils.sendPrefixedMessage(player, "&1Abyssal Form &7— you become one with the deep!");
    }
}
