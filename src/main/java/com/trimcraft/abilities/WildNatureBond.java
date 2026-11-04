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
        return "Channel the jungle's life force, restoring health and granting speed for 5 seconds.";
    }
    @Override public String getCooldownKey() { return "wild-nature-bond"; }
    @Override public int getDefaultCooldown() { return 20; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double healPerSec = Math.max(0.0, plugin.getTrimConfig().getDouble("wild.nature-bond.healing-per-second", 1.5));
        int durationTicks = Math.max(1, plugin.getTrimConfig().getInt("wild.nature-bond.duration-ticks", 100));
        int speedAmp = Math.max(0, plugin.getTrimConfig().getInt("wild.nature-bond.speed-amplifier", 1));

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, speedAmp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, durationTicks, 1, false, true));

        int healingPulses = Math.max(1, (int) Math.ceil(durationTicks / 20.0));
        int[] pulses = {0};
        BukkitTask[] taskHolder = new BukkitTask[1];

        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || pulses[0] >= healingPulses) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "nature-bond");
                return;
            }

            if (healPerSec > 0.0 && player.getAttribute(Attribute.MAX_HEALTH) != null) {
                double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
                player.setHealth(Math.min(maxHealth, player.getHealth() + healPerSec));
            }

            ParticleUtils.spawnGreenMagic(player.getLocation().add(0, 1, 0));
            pulses[0]++;
            if (pulses[0] >= healingPulses) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "nature-bond");
            }
        }, 1L, 20L);
        taskHolder[0] = task;

        plugin.getActiveEffectsManager().registerTask(player, "nature-bond", task);
        MessageUtils.sendPrefixedMessage(player, "&2Nature's Bond &7activated. The wild restores you.");
    }
}
