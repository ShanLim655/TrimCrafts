package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.managers.CooldownManager;
import com.trimcraft.utils.MessageUtils;
import org.bukkit.entity.Player;

public abstract class TrimAbility {

    protected final TrimCraftPlugin plugin;
    protected final CooldownManager cooldownManager;

    public TrimAbility(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        this.cooldownManager = plugin.getCooldownManager();
    }

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getCooldownKey();
    public abstract int getDefaultCooldown();
    public abstract AbilityType getType();  // LEFT_CLICK or RIGHT_CLICK

    public int getCooldown() {
        return plugin.getTrimConfig().getCooldown(getCooldownKey());
    }

    public void activate(Player player) {
        if (cooldownManager.isOnCooldown(player, getCooldownKey())) {
            long remaining = cooldownManager.getRemainingSeconds(player, getCooldownKey());
            MessageUtils.sendActionBar(player,
                    "&c" + getName() + " on cooldown: &e" + remaining + "s");
            return;
        }
        execute(player);
        cooldownManager.setCooldown(player, getCooldownKey(), getCooldown());
        if (plugin.getTrimConfig().isShowCooldownBar()) {
            startCooldownDisplay(player);
        }
    }

    protected abstract void execute(Player player);

    private void startCooldownDisplay(Player player) {
        int totalSeconds = getCooldown();
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            int remaining = totalSeconds;

            @Override
            public void run() {
                if (remaining <= 0 || !player.isOnline()) return;
                double percent = (double) remaining / totalSeconds;
                String bar = buildBar(percent);
                MessageUtils.sendActionBar(player,
                        "&7" + getName() + " &8» " + bar + " &e" + remaining + "s");
                remaining--;
            }
        }, 0L, 20L);
    }

    private String buildBar(double percent) {
        int filled = (int) (percent * 20);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if (i < filled) sb.append("&a█");
            else sb.append("&8░");
        }
        return sb.toString();
    }

    public enum AbilityType {
        LEFT_CLICK, RIGHT_CLICK
    }
}
