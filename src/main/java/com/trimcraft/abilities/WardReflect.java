package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WardReflect extends TrimAbility implements Listener {

    private final Set<UUID> reflecting = new HashSet<>();

    public WardReflect(TrimCraftPlugin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override public String getName() { return "Reflect"; }
    @Override public String getDescription() {
        return "Raise an ancient ward that mirrors 75% of incoming damage back to your attacker for a brief window.";
    }
    @Override public String getCooldownKey() { return "ward-reflect"; }
    @Override public int getDefaultCooldown() { return 20; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("ward.reflect.duration-ticks", 60);

        reflecting.add(player.getUniqueId());
        ParticleUtils.spawnAuraRing(player, 100, 200, 255);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration, 0, false, false));

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            reflecting.remove(player.getUniqueId());
        }, duration);

        plugin.getActiveEffectsManager().registerTask(player, "reflect", task);
        MessageUtils.sendPrefixedMessage(player, "&bReflect &7— your ward is raised!");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!reflecting.contains(victim.getUniqueId())) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        double reflectPercent = plugin.getTrimConfig().getDouble("ward.reflect.reflect-percent", 75.0) / 100.0;
        double reflectedDamage = event.getDamage() * reflectPercent;
        attacker.damage(reflectedDamage);

        ParticleUtils.spawnColoredDust(attacker.getLocation().add(0, 1, 0), 100, 200, 255, 1.5f, 15);
        MessageUtils.sendPrefixedMessage(attacker, "&bReflect &7— your blow was mirrored back!");
    }
}
