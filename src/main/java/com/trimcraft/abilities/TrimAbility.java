package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.managers.CooldownManager;
import com.trimcraft.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public abstract class TrimAbility {

    protected final TrimCraftPlugin plugin;
    protected final CooldownManager cooldownManager;
    private final Set<UUID> cooldownExemptions = new HashSet<>();

    public TrimAbility(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        this.cooldownManager = plugin.getCooldownManager();
    }

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getCooldownKey();
    public abstract int getDefaultCooldown();
    public abstract AbilityType getType();

    public int getCooldown() {
        return plugin.getTrimConfig().getCooldown(getCooldownKey(), getDefaultCooldown());
    }

    public final void activate(Player player) {
        if (player == null || !player.isOnline() || !player.isValid() || player.isDead()) return;

        if (cooldownManager.isOnCooldown(player, getCooldownKey())) {
            long remaining = cooldownManager.getRemainingSeconds(player, getCooldownKey());
            MessageUtils.sendActionBar(player, "&c" + getName() + " on cooldown: &e" + remaining + "s");
            return;
        }

        UUID playerId = player.getUniqueId();
        cooldownExemptions.remove(playerId);

        try {
            execute(player);
            if (cooldownExemptions.remove(playerId)) return;

            int cooldown = getCooldown();
            if (cooldown <= 0) return;

            cooldownManager.setCooldown(player, getCooldownKey(), cooldown);
            if (plugin.getTrimConfig().isShowCooldownBar()) startCooldownDisplay(player, cooldown);
        } catch (RuntimeException exception) {
            cooldownExemptions.remove(playerId);
            plugin.getLogger().log(Level.SEVERE, "Ability " + getCooldownKey() + " failed for " + player.getName(), exception);
            MessageUtils.sendPrefixedMessage(player, "&cThat ability could not be activated. Check the server log.");
        }
    }

    protected final void cancelCooldownForActivation(Player player) {
        cooldownExemptions.add(player.getUniqueId());
    }

    protected abstract void execute(Player player);

    private void startCooldownDisplay(Player player, int totalSeconds) {
        BukkitTask[] task = new BukkitTask[1];
        task[0] = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            private int remaining = totalSeconds;

            @Override
            public void run() {
                if (remaining <= 0 || !player.isOnline() || !player.isValid()) {
                    task[0].cancel();
                    return;
                }

                double percent = (double) remaining / totalSeconds;
                MessageUtils.sendActionBar(player,
                        "&7" + getName() + " &8» " + buildBar(percent) + " &e" + remaining + "s");
                remaining--;
            }
        }, 1L, 20L);
    }

    private String buildBar(double percent) {
        int filled = (int) (Math.max(0.0, Math.min(1.0, percent)) * 20);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            bar.append(i < filled ? "&a█" : "&8░");
        }
        return bar.toString();
    }

    public enum AbilityType {
        LEFT_CLICK, RIGHT_CLICK
    }
}
