package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RibNetherFortitude extends TrimAbility {

    public RibNetherFortitude(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Nether Fortitude"; }
    @Override public String getDescription() {
        return "Draw on the indomitable spirit of the Nether Fortress — reinforcing your bones with resistance, fire immunity, and absorption hearts.";
    }
    @Override public String getCooldownKey() { return "rib-nether-fortitude"; }
    @Override public int getDefaultCooldown() { return 45; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("rib.nether-fortitude.duration-ticks", 120);
        int absorptionHearts = plugin.getTrimConfig().getInt("rib.nether-fortitude.absorption-hearts", 4);

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, 0, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, absorptionHearts - 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration / 4, 1, false, true));

        ParticleUtils.spawnAuraRing(player, 255, 80, 0);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NETHER_BRICKS_PLACE, 1f, 0.6f);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_WITHER_AMBIENT, 0.6f, 1.5f);
        MessageUtils.sendPrefixedMessage(player, "&6Nether Fortitude &7— the fortress strengthens your bones!");
    }
}
