package com.trimcraft.listeners;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.abilities.TrimAbility;
import com.trimcraft.utils.TrimUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInputListener implements Listener {

    private final TrimCraftPlugin plugin;

    public PlayerInputListener(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        if (!player.isSneaking() || !player.hasPermission("trimcraft.use")) return;

        Action action = event.getAction();
        boolean leftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        boolean rightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        if (!leftClick && !rightClick) return;

        String fullSetTrim = TrimUtils.getFullSetTrim(player);
        String trim = fullSetTrim != null ? fullSetTrim : TrimUtils.getAnyTrim(player);
        if (trim == null || !plugin.getAbilityManager().hasTrim(trim)) return;
        if (!plugin.getTrimConfig().isTrimEnabled(trim)) return;
        if (!player.hasPermission("trimcraft.trim." + trim)) return;

        TrimAbility.AbilityType type = leftClick
                ? TrimAbility.AbilityType.LEFT_CLICK
                : TrimAbility.AbilityType.RIGHT_CLICK;

        for (TrimAbility ability : plugin.getAbilityManager().getAbilities(trim)) {
            if (ability.getType() != type) continue;
            if (fullSetTrim == null && plugin.getTrimConfig().isFullSetRequired(ability)) return;

            event.setCancelled(true);
            plugin.getServer().getScheduler().runTask(plugin, () -> ability.activate(player));
            return;
        }
    }
}
