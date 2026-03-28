package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CoastSeaShield extends TrimAbility {

    public CoastSeaShield(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Sea Shield"; }
    @Override public String getDescription() {
        return "Encase yourself in a shell of ocean energy, gaining absorption and water breathing from the deep.";
    }
    @Override public String getCooldownKey() { return "coast-sea-shield"; }
    @Override public int getDefaultCooldown() { return 40; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("coast.sea-shield.duration-ticks", 100);
        int absorptionHearts = plugin.getTrimConfig().getInt("coast.sea-shield.absorption-hearts", 4);
        int waterBreath = plugin.getTrimConfig().getInt("coast.sea-shield.water-breathing-duration", 200);

        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, absorptionHearts - 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, waterBreath, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, 0, false, true));

        ParticleUtils.spawnAuraRing(player, 0, 150, 220);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.AMBIENT_UNDERWATER_LOOP, 1f, 1f);
        MessageUtils.sendPrefixedMessage(player, "&bSea Shield &7wraps you in ocean's embrace!");
    }
}
