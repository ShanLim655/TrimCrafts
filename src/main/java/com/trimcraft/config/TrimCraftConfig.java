package com.trimcraft.config;

import com.trimcraft.TrimCraftPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class TrimCraftConfig {

    private final TrimCraftPlugin plugin;
    private FileConfiguration config;

    public TrimCraftConfig(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public String getPrefix() {
        return colorize(config.getString("general.prefix", "&8[&6TrimCraft&8] &r"));
    }

    public boolean isShowCooldownBar() {
        return config.getBoolean("general.show-cooldown-bar", true);
    }

    public boolean isShowEquipMessages() {
        return config.getBoolean("general.show-equip-messages", true);
    }

    public boolean isShowHUD() {
        return config.getBoolean("general.show-hud", true);
    }

    public boolean isApplyTrimLore() {
        return config.getBoolean("general.apply-trim-lore", true);
    }

    public boolean isSilenceAntiDupe() {
        return config.getBoolean("silence.anti-dupe", true);
    }

    public boolean isHudShowAbilityNames() {
        return config.getBoolean("hud.show-ability-names", true);
    }

    public boolean isHudShowKeybinds() {
        return config.getBoolean("hud.show-keybinds", true);
    }

    public boolean isHudShowDescriptions() {
        return config.getBoolean("hud.show-descriptions", true);
    }

    public boolean isHudShowSeparator() {
        return config.getBoolean("hud.show-separator", true);
    }

    public boolean isEquipTitleEnabled() {
        return config.getBoolean("equip-title.enabled", true);
    }

    public int getEquipTitleFadeIn() {
        return config.getInt("equip-title.fade-in", 10);
    }

    public int getEquipTitleStay() {
        return config.getInt("equip-title.stay", 50);
    }

    public int getEquipTitleFadeOut() {
        return config.getInt("equip-title.fade-out", 15);
    }

    public boolean isTrimEnabled(String trim) {
        return config.getBoolean(trim + ".enabled", true);
    }

    public int getCooldown(String key) {
        return config.getInt("cooldowns." + key, 30);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public boolean getBool(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public String getString(String path, String def) {
        return colorize(config.getString(path, def));
    }

    private String colorize(String s) {
        if (s == null) return "";
        return s.replace("&", "\u00A7");
    }
}
