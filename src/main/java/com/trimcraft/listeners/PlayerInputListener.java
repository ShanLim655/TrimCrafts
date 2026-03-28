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

public class PlayerInputListener implements Listener {

    private final TrimCraftPlugin plugin;

    public PlayerInputListener(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        String trim = TrimUtils.getFullSetTrim(player);
        if (trim == null) {
            // Try any trim if full set not required
            trim = TrimUtils.getAnyTrim(player);
            if (trim == null) return;
        }

        if (!plugin.getAbilityManager().hasTrim(trim)) return;
        if (!plugin.getTrimConfig().isTrimEnabled(trim)) return;
        if (!player.hasPermission("trimcraft.trim." + trim)) return;

        Action action = event.getAction();
        boolean isLeftClick = (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK);
        boolean isRightClick = (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);

        if (!isLeftClick && !isRightClick) return;

        TrimAbility.AbilityType type = isLeftClick
                ? TrimAbility.AbilityType.LEFT_CLICK
                : TrimAbility.AbilityType.RIGHT_CLICK;

        TrimAbility[] abilities = plugin.getAbilityManager().getAbilities(trim);
        for (TrimAbility ability : abilities) {
            if (ability.getType() == type) {
                event.setCancelled(true);
                plugin.getServer().getScheduler().runTask(plugin, () -> ability.activate(player));
                break;
            }
        }
    }
}
