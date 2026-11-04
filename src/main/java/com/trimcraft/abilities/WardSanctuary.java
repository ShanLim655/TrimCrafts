package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.CombatUtils;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class WardSanctuary extends TrimAbility {

    public WardSanctuary(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Sanctuary"; }
    @Override public String getDescription() {
        return "Create a healing field that restores you and nearby teammates for 10 seconds.";
    }
    @Override public String getCooldownKey() { return "ward-sanctuary"; }
    @Override public int getDefaultCooldown() { return 55; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = Math.max(0.0, plugin.getTrimConfig().getDouble("ward.sanctuary.radius", 8.0));
        int duration = Math.max(1, plugin.getTrimConfig().getInt("ward.sanctuary.duration-ticks", 200));
        double healPerSec = Math.max(0.0, plugin.getTrimConfig().getDouble("ward.sanctuary.healing-per-second", 1.0));
        Location anchor = player.getLocation().clone();
        int totalPulses = Math.max(1, (int) Math.ceil(duration / 20.0));
        int[] pulses = {0};
        BukkitTask[] taskHolder = new BukkitTask[1];

        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (pulses[0] >= totalPulses || !anchor.getWorld().isChunkLoaded(anchor.getBlockX() >> 4, anchor.getBlockZ() >> 4)) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "sanctuary");
                return;
            }

            for (Entity entity : anchor.getWorld().getNearbyEntities(anchor, radius, radius, radius)) {
                if (!(entity instanceof Player ally) || !CombatUtils.areAllies(player, ally)) continue;
                if (ally.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null) continue;
                double maxHealth = ally.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                ally.setHealth(Math.min(maxHealth, ally.getHealth() + healPerSec));
                ParticleUtils.spawnColoredDust(ally.getLocation().add(0, 1, 0), 100, 255, 100, 1f, 5);
            }

            ParticleUtils.spawnExplosionRing(anchor, 100, 220, 100, radius);
            pulses[0]++;
            if (pulses[0] >= totalPulses) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "sanctuary");
            }
        }, 1L, 20L);
        taskHolder[0] = task;

        plugin.getActiveEffectsManager().registerTask(player, "sanctuary", task);
        MessageUtils.sendPrefixedMessage(player, "&aSanctuary &7— a healing field settles around you.");
    }
}
