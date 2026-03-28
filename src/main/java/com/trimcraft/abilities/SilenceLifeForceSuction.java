package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import com.trimcraft.utils.TrimUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class SilenceLifeForceSuction extends TrimAbility {

    public SilenceLifeForceSuction(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Life Force Suction"; }
    @Override public String getDescription() {
        return "Drain a nearby player's life force for 4 seconds, healing yourself based on the drained amount.";
    }
    @Override public String getCooldownKey() { return "silence-life-force-suction"; }
    @Override public int getDefaultCooldown() { return 30; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double range = 8.0;
        Player target = getNearestPlayer(player, range);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cNo target found within range!");
            cooldownManager.clearCooldown(player, getCooldownKey());
            return;
        }

        double multiplier = TrimUtils.getArmorMultiplier(player);
        double drainPerTick = plugin.getTrimConfig().getDouble(
                "silence.life-force-suction.drain-per-tick", 0.5) * multiplier;
        int durationTicks = plugin.getTrimConfig().getInt(
                "silence.life-force-suction.duration-ticks", 80);

        MessageUtils.sendPrefixedMessage(player, "&5Life Force Suction &7activated!");
        MessageUtils.sendPrefixedMessage(target, "&cYou are being drained by &5" + player.getName() + "&c!");

        final int[] ticksLeft = {durationTicks};
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticksLeft[0] <= 0 || !player.isOnline() || !target.isOnline() || target.isDead()) {
                return;
            }
            double targetHp = target.getHealth();
            double drain = Math.min(drainPerTick, targetHp - 0.5);
            if (drain <= 0) { ticksLeft[0] = 0; return; }

            target.setHealth(Math.max(0.5, targetHp - drain));
            double maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.setHealth(Math.min(maxHp, player.getHealth() + drain));

            ParticleUtils.spawnDrainBeam(player, target);
            ParticleUtils.spawnAuraRing(player, 150, 0, 200);
            ticksLeft[0]--;
        }, 0L, 2L);

        plugin.getActiveEffectsManager().registerTask(player, "life-force", task);
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
