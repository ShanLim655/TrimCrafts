package com.trimcraft.listeners;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.managers.TrimLoreManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TrimItemListener implements Listener {

    private final TrimCraftPlugin plugin;
    private final TrimLoreManager loreManager;

    public TrimItemListener(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        this.loreManager = plugin.getTrimLoreManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;

        Item item = event.getItem();
        new BukkitRunnable() {
            @Override
            public void run() {
                loreManager.applyLore(item.getItemStack());
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cursor != null && cursor.getType() != Material.AIR) {
                    loreManager.applyLore(cursor);
                }
                if (current != null && current.getType() != Material.AIR) {
                    loreManager.applyLore(current);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;

        if (result.getType() == Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE) {
            if (plugin.getTrimConfig().isSilenceAntiDupe()) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                if (event.getViewers().size() > 0 && event.getViewers().get(0) instanceof Player player) {
                    com.trimcraft.utils.MessageUtils.sendActionBar(player,
                            "&cSilence Trim cannot be duplicated on this server.");
                }
            }
        }
    }
}
