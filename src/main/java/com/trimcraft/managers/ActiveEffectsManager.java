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
        activeTasks.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>()).put(key, task);
    }

    public void unregisterTask(Player player, String key) {
        Map<String, BukkitTask> tasks = activeTasks.get(player.getUniqueId());
        if (tasks == null) return;
        tasks.remove(key);
        if (tasks.isEmpty()) activeTasks.remove(player.getUniqueId());
    }

    public void cancelTask(Player player, String key) {
        Map<String, BukkitTask> tasks = activeTasks.get(player.getUniqueId());
        if (tasks == null) return;
        BukkitTask task = tasks.remove(key);
        if (task != null) task.cancel();
        if (tasks.isEmpty()) activeTasks.remove(player.getUniqueId());
    }

    public void cancelAll(Player player) {
        Map<String, BukkitTask> tasks = activeTasks.remove(player.getUniqueId());
        if (tasks != null) tasks.values().forEach(BukkitTask::cancel);
    }

    public void cancelAll() {
        activeTasks.values().forEach(tasks -> tasks.values().forEach(BukkitTask::cancel));
        activeTasks.clear();
    }

    public boolean hasActiveEffect(Player player, String key) {
        Map<String, BukkitTask> tasks = activeTasks.get(player.getUniqueId());
        return tasks != null && tasks.containsKey(key);
    }
}
