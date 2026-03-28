package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class WildNatureBond extends TrimAbility {

    public WildNatureBond(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Nature's Bond"; }
    @Override public String getDescription() {
        return "Channel the jungle's life force, rapidly regenerating health and gaining speed for 5 seconds.";
    }
    @Override public String getCooldownKey() { return "wild-nature-bond"; }
    @Override public int getDefaultCooldown() { return 20; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double healPerSec = plugin.getTrimConfig().getDouble("wild.nature-bond.healing-per-second", 1.5);
        int durationTicks = plugin.getTrimConfig().getInt("wild.nature-bond.duration-ticks", 100);
        int speedAmp = plugin.getTrimConfig().getInt("wild.nature-bond.speed-amplifier", 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, speedAmp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, durationTicks, 1, false, true));

        final int[] ticks = {0};
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0] >= durationTicks / 20 || !player.isOnline()) return;
            double maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.setHealth(Math.min(maxHp, player.getHealth() + healPerSec));
            ParticleUtils.spawnGreenMagic(player.getLocation().add(0, 1, 0));
            ticks[0]++;
        }, 0L, 20L);

        plugin.getActiveEffectsManager().registerTask(player, "nature-bond", task);
        MessageUtils.sendPrefixedMessage(player, "&2Nature's Bond &7activated! The wild heals you.");
    }
}
