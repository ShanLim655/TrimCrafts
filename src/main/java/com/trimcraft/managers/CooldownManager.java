package com.trimcraft.managers;

import com.trimcraft.TrimCraftPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager implements Listener {

    private final TrimCraftPlugin plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public CooldownManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Map<String, Long> loaded = plugin.getDatabaseManager().loadCooldowns(uuid);
        cooldowns.put(uuid, loaded);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Map<String, Long> map = cooldowns.remove(uuid);
        if (map != null && !map.isEmpty()) {
            plugin.getDatabaseManager().saveAllCooldownsAsync(Map.of(uuid, new HashMap<>(map)));
        }
    }

    public boolean isOnCooldown(Player player, String ability) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return false;
        Long expiry = map.get(ability);
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    public long getRemainingSeconds(Player player, String ability) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return 0;
        Long expiry = map.get(ability);
        if (expiry == null) return 0;
        long remaining = expiry - System.currentTimeMillis();
        return remaining > 0 ? (long) Math.ceil(remaining / 1000.0) : 0;
    }

    public void setCooldown(Player player, String ability, int seconds) {
        if (seconds <= 0) return;
        long expiry = System.currentTimeMillis() + seconds * 1000L;
        cooldowns.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>()).put(ability, expiry);
        plugin.getDatabaseManager().saveCooldownAsync(player.getUniqueId(), ability, expiry);
    }

    public void clearCooldown(Player player, String ability) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map != null) {
            map.remove(ability);
            if (map.isEmpty()) cooldowns.remove(player.getUniqueId());
        }
        plugin.getDatabaseManager().deleteCooldown(player.getUniqueId(), ability);
    }

    public void clearAll() {
        cooldowns.clear();
    }

    public Map<String, Long> getPlayerCooldowns(Player player) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<>(map));
    }

    public Map<UUID, Map<String, Long>> getAllCooldowns() {
        Map<UUID, Map<String, Long>> copy = new HashMap<>();
        cooldowns.forEach((uuid, map) -> copy.put(uuid, new HashMap<>(map)));
        return copy;
    }

    public void saveCooldowns() {
        plugin.getDatabaseManager().saveAllCooldowns(getAllCooldowns());
    }
}
