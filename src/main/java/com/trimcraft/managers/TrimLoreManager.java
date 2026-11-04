package com.trimcraft.managers;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.abilities.TrimAbility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TrimLoreManager {

    private final TrimCraftPlugin plugin;
    private final NamespacedKey loreKey;

    private static final Map<String, Material> TRIM_TEMPLATES = new LinkedHashMap<>();

    static {
        TRIM_TEMPLATES.put("silence", Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("wild", Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("coast", Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("dune", Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("eye", Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("host", Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("raiser", Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("rib", Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("sentry", Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("shaper", Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("snout", Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("spire", Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("tide", Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("vex", Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("ward", Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("wayfinder", Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("bolt", Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
        TRIM_TEMPLATES.put("flow", Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
    }

    private static final Map<Material, String> TEMPLATE_TO_TRIM = new EnumMap<>(Material.class);

    static {
        TRIM_TEMPLATES.forEach((trim, material) -> TEMPLATE_TO_TRIM.put(material, trim));
    }

    public TrimLoreManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        this.loreKey = new NamespacedKey(plugin, "trim-lore-applied");
    }

    public boolean isTemplate(Material material) {
        return TEMPLATE_TO_TRIM.containsKey(material);
    }

    public String getTrimKey(Material material) {
        return TEMPLATE_TO_TRIM.get(material);
    }

    public boolean applyLore(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        String trimKey = TEMPLATE_TO_TRIM.get(item.getType());
        if (trimKey == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (meta.getPersistentDataContainer().has(loreKey, PersistentDataType.BYTE)) return false;

        List<String> lore = buildLore(trimKey);
        List<String> existing = meta.getLore();
        if (existing != null && !existing.isEmpty()) lore.addAll(existing);

        meta.setLore(lore);
        meta.getPersistentDataContainer().set(loreKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return true;
    }

    private List<String> buildLore(String trimKey) {
        TrimAbility[] abilities = plugin.getAbilityManager().getAbilities(trimKey);
        String name = capitalize(trimKey);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "⬡ " + ChatColor.GOLD + ChatColor.BOLD + "TrimCraft" + ChatColor.DARK_GRAY + " — " + ChatColor.YELLOW + name + " Trim");
        lore.add(ChatColor.DARK_GRAY + "Wear a full matching set to unlock:");
        lore.add("");

        for (int index = 0; index < abilities.length; index++) {
            TrimAbility ability = abilities[index];
            lore.add(ChatColor.YELLOW.toString() + (index + 1) + ". " + ChatColor.WHITE + ability.getName());
            String keybind = ability.getType() == TrimAbility.AbilityType.LEFT_CLICK
                    ? "Sneak + Left Click" : "Sneak + Right Click";
            lore.add(ChatColor.GRAY + "Keybind: " + ChatColor.AQUA + keybind);
            wrapDescription(ability.getDescription()).forEach(line -> lore.add(ChatColor.DARK_GRAY + line));
            lore.add("");
        }

        lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + "Full matching trim set required unless configured otherwise.");
        return lore;
    }

    private List<String> wrapDescription(String description) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : description.split("\\s+")) {
            if (line.length() > 0 && line.length() + word.length() + 1 > 38) {
                lines.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) line.append(' ');
            line.append(word);
        }
        if (!line.isEmpty()) lines.add(line.toString());
        return lines;
    }

    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}
