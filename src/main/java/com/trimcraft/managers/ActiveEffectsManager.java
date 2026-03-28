package com.trimcraft.managers;

import com.trimcraft.TrimCraftPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActiveEffectsManager {

    private final TrimCraftPlugin plugin;
    private final Map<UUID, Map<String, BukkitTask>> activeTasks = new HashMap<>();

    public ActiveEffectsManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerTask(Player player, String key, BukkitTask task) {
        cancelTask(player, key);
        activeTasks.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(key, task);
    }

    public void cancelTask(Player player, String key) {
        Map<String, BukkitTask> map = activeTasks.get(player.getUniqueId());
        if (map != null) {
            BukkitTask t = map.remove(key);
            if (t != null) t.cancel();
        }
    }

    public void cancelAll(Player player) {
        Map<String, BukkitTask> map = activeTasks.remove(player.getUniqueId());
        if (map != null) map.values().forEach(BukkitTask::cancel);
    }

    public boolean hasActiveEffect(Player player, String key) {
        Map<String, BukkitTask> map = activeTasks.get(player.getUniqueId());
        return map != null && map.containsKey(key);
    }
}
