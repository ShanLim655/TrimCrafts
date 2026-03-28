package com.trimcraft.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

public class TrimUtils {

    public static String getFullSetTrim(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor == null) return null;

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
            if (piece != null && piece.getItemMeta() instanceof ArmorMeta meta) {
                ArmorTrim armorTrim = meta.getTrim();
                if (armorTrim != null) return armorTrim.getPattern().getKey().getKey();
            }
        }
        return null;
    }

    public static boolean hasFullSet(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor == null) return false;
        for (ItemStack piece : armor) {
            if (piece == null || piece.getType() == Material.AIR) return false;
        }
        return true;
    }

    public static double getArmorMultiplier(Player player) {
        ItemStack boots = player.getInventory().getBoots();
        if (boots == null) return 1.0;
        return switch (boots.getType()) {
            case NETHERITE_BOOTS -> 1.4;
            case DIAMOND_BOOTS -> 1.0;
            case IRON_BOOTS -> 0.8;
            case GOLDEN_BOOTS -> 0.75;
            case CHAINMAIL_BOOTS -> 0.7;
            default -> 0.6;
        };
    }

    public static String formatTrimName(String key) {
        if (key == null) return "None";
        return key.substring(0, 1).toUpperCase() + key.substring(1).replace("_", " ");
    }

    public static String colorize(String s) {
        return s.replace("&", "\u00A7");
    }
}
