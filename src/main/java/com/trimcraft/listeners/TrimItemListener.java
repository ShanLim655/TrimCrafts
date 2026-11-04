package com.trimcraft.listeners;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.managers.TrimLoreManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class TrimItemListener implements Listener {

    private final TrimCraftPlugin plugin;
    private final TrimLoreManager loreManager;

    public TrimItemListener(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        this.loreManager = plugin.getTrimLoreManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!plugin.getTrimConfig().isApplyTrimLore()) return;

        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        if (loreManager.applyLore(stack)) item.setItemStack(stack);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;

        if (result.getType() == Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE
                && plugin.getTrimConfig().isSilenceAntiDupe()) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            if (!event.getViewers().isEmpty() && event.getViewers().get(0) instanceof Player player) {
                com.trimcraft.utils.MessageUtils.sendActionBar(player,
                        "&cSilence Trim cannot be duplicated on this server.");
            }
            return;
        }

        if (plugin.getTrimConfig().isApplyTrimLore()) loreManager.applyLore(result);
    }
}
