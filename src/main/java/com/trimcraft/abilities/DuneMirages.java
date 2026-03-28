package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DuneMirages extends TrimAbility {

    public DuneMirages(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Desert Mirages"; }
    @Override public String getDescription() {
        return "Vanish into shimmering mirages, becoming invisible and leaving behind phantom sand pillars to confuse enemies.";
    }
    @Override public String getCooldownKey() { return "dune-mirages"; }
    @Override public int getDefaultCooldown() { return 50; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("dune.mirages.decoy-duration-ticks", 80);
        int decoys = plugin.getTrimConfig().getInt("dune.mirages.decoy-count", 3);

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1, false, false));

        Location loc = player.getLocation();
        for (int i = 0; i < decoys; i++) {
            double angle = (2 * Math.PI / decoys) * i;
            Location mirLoc = loc.clone().add(Math.cos(angle) * 3, 0, Math.sin(angle) * 3);
            ParticleUtils.spawnSandParticles(mirLoc);
            ParticleUtils.spawnColoredDust(mirLoc.add(0, 1, 0), 220, 180, 80, 1.5f, 15);
        }

        player.getWorld().playSound(loc, org.bukkit.Sound.BLOCK_SAND_PLACE, 1.5f, 1.5f);
        MessageUtils.sendPrefixedMessage(player, "&eDesert Mirages &7— vanish into the shimmer!");
    }
}
