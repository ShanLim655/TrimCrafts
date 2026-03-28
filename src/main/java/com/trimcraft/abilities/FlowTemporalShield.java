package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlowTemporalShield extends TrimAbility {

    private final Map<UUID, Double> savedHealth = new HashMap<>();
    private final Map<UUID, Location> savedLocation = new HashMap<>();

    public FlowTemporalShield(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Temporal Shield"; }
    @Override public String getDescription() {
        return "Freeze a snapshot of your current state — after 3 seconds, time rewinds and you return to that exact position and health.";
    }
    @Override public String getCooldownKey() { return "flow-temporal-shield"; }
    @Override public int getDefaultCooldown() { return 55; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int rewindSeconds = plugin.getTrimConfig().getInt("flow.temporal-shield.rewind-seconds", 3);
        int duration = plugin.getTrimConfig().getInt("flow.temporal-shield.duration-ticks", 100);

        savedHealth.put(player.getUniqueId(), player.getHealth());
        savedLocation.put(player.getUniqueId(), player.getLocation().clone());

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 2, false, true));
        ParticleUtils.spawnAuraRing(player, 0, 200, 255);
        ParticleUtils.spawnEnchantmentParticles(player);

        MessageUtils.sendPrefixedMessage(player, "&9Temporal Shield &7— your state is frozen in time!");

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            Double hp = savedHealth.remove(player.getUniqueId());
            Location loc = savedLocation.remove(player.getUniqueId());
            if (hp != null) {
                double maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                player.setHealth(Math.min(maxHp, hp));
            }
            if (loc != null) player.teleport(loc);
            ParticleUtils.spawnPortalVortex(player.getLocation());
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.7f);
            MessageUtils.sendPrefixedMessage(player, "&9Temporal Shield &7— time has been rewound!");
        }, rewindSeconds * 20L);

        plugin.getActiveEffectsManager().registerTask(player, "temporal-shield", task);
    }
}
