package com.trimcraft.utils;

import com.trimcraft.config.TrimCraftConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.Locale;

public final class TrimUtils {

    private TrimUtils() {
    }

    public static String getFullSetTrim(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor == null || armor.length != 4) return null;

        String trim = null;
        for (ItemStack piece : armor) {
            if (piece == null || piece.getType() == Material.AIR) return null;
            if (!(piece.getItemMeta() instanceof ArmorMeta meta)) return null;
            ArmorTrim armorTrim = meta.getTrim();
            if (armorTrim == null) return null;
            String pieceTrim = armorTrim.getPattern().getKey().getKey();
            if (trim == null) {
                trim = pieceTrim;
            } else if (!trim.equals(pieceTrim)) {
                return null;
            }
        }
        return trim;
    }

    public static String getAnyTrim(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor == null) return null;
        for (ItemStack piece : armor) {
            if (piece != null && piece.getType() != Material.AIR
                    && piece.getItemMeta() instanceof ArmorMeta meta
                    && meta.getTrim() != null) {
                return meta.getTrim().getPattern().getKey().getKey();
            }
        }
        return null;
    }

    public static boolean hasFullSet(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor == null || armor.length != 4) return false;
        for (ItemStack piece : armor) {
            if (piece == null || piece.getType() == Material.AIR) return false;
        }
        return true;
    }

    public static double getArmorMultiplier(Player player, TrimCraftConfig config) {
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null) return 1.0;

        String material = boots.getType().name().toLowerCase(Locale.ROOT).replace("_boots", "");
        if (material.equals("golden")) material = "gold";
        return config.getDouble("silence.life-force-suction.multipliers." + material, switch (material) {
            case "netherite" -> 1.4;
            case "diamond" -> 1.0;
            case "iron" -> 0.8;
            case "gold" -> 0.75;
            case "chainmail" -> 0.7;
            default -> 0.6;
        });
    }

    public static String formatTrimName(String key) {
        if (key == null || key.isBlank()) return "None";
        String normalized = key.replace('_', ' ');
        return normalized.substring(0, 1).toUpperCase(Locale.ROOT) + normalized.substring(1);
    }

    public static String colorize(String value) {
        return value == null ? "" : value.replace("&", "\u00A7");
    }
}
