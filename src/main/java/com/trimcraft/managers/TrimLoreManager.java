package com.trimcraft.managers;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.abilities.TrimAbility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TrimLoreManager {

    private final TrimCraftPlugin plugin;

    private static final Map<String, Material> TRIM_TEMPLATES = new HashMap<>();

    static {
        TRIM_TEMPLATES.put("silence",   Material.valueOf("SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("wild",      Material.valueOf("WILD_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("coast",     Material.valueOf("COAST_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("dune",      Material.valueOf("DUNE_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("eye",       Material.valueOf("EYE_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("host",      Material.valueOf("HOST_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("raiser",    Material.valueOf("RAISER_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("rib",       Material.valueOf("RIB_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("sentry",    Material.valueOf("SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("shaper",    Material.valueOf("SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("snout",     Material.valueOf("SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("spire",     Material.valueOf("SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("tide",      Material.valueOf("TIDE_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("vex",       Material.valueOf("VEX_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("ward",      Material.valueOf("WARD_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("wayfinder", Material.valueOf("WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("bolt",      Material.valueOf("BOLT_ARMOR_TRIM_SMITHING_TEMPLATE"));
        TRIM_TEMPLATES.put("flow",      Material.valueOf("FLOW_ARMOR_TRIM_SMITHING_TEMPLATE"));
    }

    private static final Map<Material, String> TEMPLATE_TO_TRIM = new HashMap<>();

    static {
        TRIM_TEMPLATES.forEach((trim, mat) -> TEMPLATE_TO_TRIM.put(mat, trim));
    }

    public TrimLoreManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isTemplate(Material material) {
        return TEMPLATE_TO_TRIM.containsKey(material);
    }

    public String getTrimKey(Material material) {
        return TEMPLATE_TO_TRIM.get(material);
    }

    public boolean applyLore(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        String trimKey = TEMPLATE_TO_TRIM.get(item.getType());
        if (trimKey == null) return false;

        List<String> lore = buildLore(trimKey);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        List<String> existing = meta.getLore();
        if (existing != null && !existing.isEmpty() && existing.get(0).contains("TrimCraft")) return false;

        meta.setLore(lore);
        item.setItemMeta(meta);
        return true;
    }

    private List<String> buildLore(String trimKey) {
        TrimAbility[] abilities = plugin.getAbilityManager().getAbilities(trimKey);
        String name = capitalize(trimKey);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "⬡ " + ChatColor.GOLD + ChatColor.BOLD + "TrimCraft" + ChatColor.DARK_GRAY + " — " + ChatColor.YELLOW + name + " Trim");
        lore.add(ChatColor.DARK_GRAY + "Equip a full matching set to unlock:");
        lore.add("");

        if (abilities.length > 0) {
            TrimAbility a1 = abilities[0];
            lore.add(ChatColor.YELLOW + "❶ " + ChatColor.WHITE + a1.getName());
            lore.add(ChatColor.GRAY + "  Keybind: " + ChatColor.AQUA + "Sneak + Left Click");
            wrapDesc(a1.getDescription()).forEach(line -> lore.add(ChatColor.DARK_GRAY + "  " + line));
        }

        lore.add("");

        if (abilities.length > 1) {
            TrimAbility a2 = abilities[1];
            lore.add(ChatColor.YELLOW + "❷ " + ChatColor.WHITE + a2.getName());
            lore.add(ChatColor.GRAY + "  Keybind: " + ChatColor.AQUA + "Sneak + Right Click");
            wrapDesc(a2.getDescription()).forEach(line -> lore.add(ChatColor.DARK_GRAY + "  " + line));
        }

        lore.add("");
        lore.add("" + ChatColor.DARK_GRAY + ChatColor.ITALIC + "Full armor set of the same trim required.");
        return lore;
    }

    private List<String> wrapDesc(String desc) {
        List<String> lines = new ArrayList<>();
        String[] words = desc.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (line.length() + word.length() > 38) {
                lines.add(line.toString().trim());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        if (!line.toString().isBlank()) lines.add(line.toString().trim());
        return lines;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
