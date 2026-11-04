package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import com.trimcraft.utils.TargetUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaiserDeathMark extends TrimAbility implements Listener {

    private final Map<UUID, Long> markedUntil = new HashMap<>();

    public RaiserDeathMark(TrimCraftPlugin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override public String getName() { return "Death Mark"; }
    @Override public String getDescription() {
        return "Mark a player so all incoming damage is increased while the rune remains active.";
    }
    @Override public String getCooldownKey() { return "raiser-death-mark"; }
    @Override public int getDefaultCooldown() { return 30; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double range = Math.max(0.1, plugin.getTrimConfig().getDouble("raiser.death-mark.range", 10.0));
        int duration = Math.max(1, plugin.getTrimConfig().getInt("raiser.death-mark.duration-ticks", 100));
        Player target = TargetUtils.getLookTargetPlayer(player, range);

        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cAim at an enemy player to place Death Mark.");
            cancelCooldownForActivation(player);
            return;
        }

        long expiresAt = System.currentTimeMillis() + duration * 50L;
        markedUntil.put(target.getUniqueId(), expiresAt);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            markedUntil.remove(target.getUniqueId(), expiresAt);
        }, duration);

        ParticleUtils.spawnColoredDust(target.getLocation().add(0, 1.5, 0), 80, 0, 0, 2f, 30);
        target.getWorld().playSound(target.getLocation(), org.bukkit.Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1f, 0.8f);
        MessageUtils.sendPrefixedMessage(player, "&8Death Mark &7branded on &b" + target.getName() + "&7!");
        MessageUtils.sendPrefixedMessage(target, "&8A death rune &7burns into your soul...");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMarkedDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;

        Long expiresAt = markedUntil.get(target.getUniqueId());
        if (expiresAt == null) return;
        if (expiresAt <= System.currentTimeMillis()) {
            markedUntil.remove(target.getUniqueId());
            return;
        }

        double multiplier = plugin.getTrimConfig().getDouble("raiser.death-mark.damage-multiplier", 1.35);
        event.setDamage(event.getDamage() * Math.max(1.0, Math.min(3.0, multiplier)));
    }
}
