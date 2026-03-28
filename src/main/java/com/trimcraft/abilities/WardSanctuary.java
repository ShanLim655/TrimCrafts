package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
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
        return "Consecrate the ground beneath you — a zone of healing energy that restores the health of all allies within.";
    }
    @Override public String getCooldownKey() { return "ward-sanctuary"; }
    @Override public int getDefaultCooldown() { return 55; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = plugin.getTrimConfig().getDouble("ward.sanctuary.radius", 8.0);
        int duration = plugin.getTrimConfig().getInt("ward.sanctuary.duration-ticks", 200);
        double healPerSec = plugin.getTrimConfig().getDouble("ward.sanctuary.healing-per-second", 1.0);

        Location anchor = player.getLocation().clone();
        MessageUtils.sendPrefixedMessage(player, "&aSanctuary &7— a zone of healing is consecrated!");

        final int[] ticks = {0};
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (ticks[0] >= duration / 20) return;
            for (Entity entity : anchor.getWorld().getNearbyEntities(anchor, radius, radius, radius)) {
                if (!(entity instanceof Player ally)) continue;
                double maxHp = ally.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                ally.setHealth(Math.min(maxHp, ally.getHealth() + healPerSec));
                ParticleUtils.spawnColoredDust(ally.getLocation().add(0, 1, 0), 100, 255, 100, 1f, 5);
            }
            ParticleUtils.spawnExplosionRing(anchor, 100, 220, 100, radius);
            ticks[0]++;
        }, 0L, 20L);

        plugin.getActiveEffectsManager().registerTask(player, "sanctuary", task);
    }
}
