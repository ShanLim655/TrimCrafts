package com.trimcraft.listeners;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.TrimUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorChangeListener implements Listener {

    private final TrimCraftPlugin plugin;
    private final Map<UUID, String> lastTrim = new HashMap<>();

    public ArmorChangeListener(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) return;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) checkTrimEquip(player);
        }, 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) checkTrimEquip(player);
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastTrim.remove(event.getPlayer().getUniqueId());
        plugin.getTrimHUDManager().hideHUD(event.getPlayer());
    }

    private void checkTrimEquip(Player player) {
        String trim = TrimUtils.getFullSetTrim(player);
        String last = lastTrim.get(player.getUniqueId());

        if (trim != null && !trim.equals(last)) {
            lastTrim.put(player.getUniqueId(), trim);
            if (plugin.getTrimConfig().isShowEquipMessages()) sendEquipMessage(player, trim);
            plugin.getTrimHUDManager().showHUD(player, trim);
        } else if (trim == null && last != null) {
            lastTrim.remove(player.getUniqueId());
            plugin.getTrimHUDManager().hideHUD(player);
        }
    }

    private void sendEquipMessage(Player player, String trim) {
        String name = TrimUtils.formatTrimName(trim);
        MessageUtils.sendPrefixedMessage(player,
                "&6" + name + " Trim &7equipped! Sneak+Click to unleash your abilities.");

        for (var ability : plugin.getAbilityManager().getAbilities(trim)) {
            String keybind = ability.getType() == com.trimcraft.abilities.TrimAbility.AbilityType.LEFT_CLICK
                    ? "&eSneak+LMB" : "&eSneak+RMB";
            MessageUtils.sendMessage(player,
                    "  " + keybind + " &7» &b" + ability.getName() + " &8- &7" + ability.getDescription());
        }

        if (plugin.getTrimConfig().isEquipTitleEnabled()) {
            player.sendTitle(
                    TrimUtils.colorize("&6&l" + name + " Trim"),
                    TrimUtils.colorize("&7Sneak+Click to unleash your power"),
                    plugin.getTrimConfig().getEquipTitleFadeIn(),
                    plugin.getTrimConfig().getEquipTitleStay(),
                    plugin.getTrimConfig().getEquipTitleFadeOut()
            );
        }
    }
}
