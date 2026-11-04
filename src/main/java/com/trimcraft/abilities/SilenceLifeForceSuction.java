package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.CombatUtils;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import com.trimcraft.utils.TrimUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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
            cancelCooldownForActivation(player);
            return;
        }

        double multiplier = TrimUtils.getArmorMultiplier(player, plugin.getTrimConfig());
        double drainPerPulse = Math.max(0.0,
                plugin.getTrimConfig().getDouble("silence.life-force-suction.drain-per-tick", 0.5)) * multiplier;
        int durationTicks = Math.max(1,
                plugin.getTrimConfig().getInt("silence.life-force-suction.duration-ticks", 80));

        MessageUtils.sendPrefixedMessage(player, "&5Life Force Suction &7activated!");
        MessageUtils.sendPrefixedMessage(target, "&cYou are being drained by &5" + player.getName() + "&c!");

        int[] ticksLeft = {durationTicks};
        BukkitTask[] taskHolder = new BukkitTask[1];
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || !target.isOnline() || target.isDead()
                    || player.getWorld() != target.getWorld()
                    || player.getLocation().distanceSquared(target.getLocation()) > range * range
                    || ticksLeft[0] <= 0) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "life-force");
                return;
            }

            double availableHealth = target.getHealth() - 0.5;
            double drained = Math.min(drainPerPulse, availableHealth);
            if (drained <= 0.0) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "life-force");
                return;
            }

            target.setHealth(Math.max(0.5, target.getHealth() - drained));
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.setHealth(Math.min(maxHealth, player.getHealth() + drained));
            ParticleUtils.spawnDrainBeam(player, target);
            ParticleUtils.spawnAuraRing(player, 150, 0, 200);

            ticksLeft[0] -= 2;
            if (ticksLeft[0] <= 0) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "life-force");
            }
        }, 1L, 2L);
        taskHolder[0] = task;

        plugin.getActiveEffectsManager().registerTask(player, "life-force", task);
    }

    private Player getNearestPlayer(Player source, double range) {
        Player nearest = null;
        double nearestDistance = range * range;
        for (Player candidate : source.getWorld().getPlayers()) {
            if (!CombatUtils.canTarget(source, candidate)) continue;
            double distance = source.getLocation().distanceSquared(candidate.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }
}
