package com.trimcraft;

import com.trimcraft.commands.TrimCraftCommand;
import com.trimcraft.commands.TrimInfoCommand;
import com.trimcraft.config.TrimCraftConfig;
import com.trimcraft.listeners.ArmorChangeListener;
import com.trimcraft.listeners.PlayerInputListener;
import com.trimcraft.listeners.TrimItemListener;
import com.trimcraft.managers.AbilityManager;
import com.trimcraft.managers.ActiveEffectsManager;
import com.trimcraft.managers.CooldownManager;
import com.trimcraft.managers.DatabaseManager;
import com.trimcraft.managers.TrimHUDManager;
import com.trimcraft.managers.TrimLoreManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TrimCraftPlugin extends JavaPlugin {

    private static TrimCraftPlugin instance;
    private TrimCraftConfig trimConfig;
    private CooldownManager cooldownManager;
    private AbilityManager abilityManager;
    private ActiveEffectsManager activeEffectsManager;
    private DatabaseManager databaseManager;
    private TrimHUDManager trimHUDManager;
    private TrimLoreManager trimLoreManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        trimConfig = new TrimCraftConfig(this);

        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        cooldownManager = new CooldownManager(this);
        activeEffectsManager = new ActiveEffectsManager(this);
        abilityManager = new AbilityManager(this);
        trimHUDManager = new TrimHUDManager(this);
        trimLoreManager = new TrimLoreManager(this);

        getServer().getPluginManager().registerEvents(new ArmorChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInputListener(this), this);
        getServer().getPluginManager().registerEvents(new TrimItemListener(this), this);

        PluginCommand trimCraftCommand = getCommand("trimcraft");
        PluginCommand trimInfoCommand = getCommand("triminfo");
        if (trimCraftCommand == null || trimInfoCommand == null) {
            getLogger().severe("Required commands are missing from plugin.yml. Disabling TrimCraft.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        trimCraftCommand.setExecutor(new TrimCraftCommand(this));
        trimInfoCommand.setExecutor(new TrimInfoCommand(this));
        getLogger().info("TrimCraft v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (activeEffectsManager != null) activeEffectsManager.cancelAll();
        if (trimHUDManager != null) trimHUDManager.hideAll();
        if (cooldownManager != null) cooldownManager.saveCooldowns();
        if (databaseManager != null) databaseManager.disconnect();
        instance = null;
        getLogger().info("TrimCraft disabled.");
    }

    public static TrimCraftPlugin getInstance() {
        return instance;
    }

    public TrimCraftConfig getTrimConfig() {
        return trimConfig;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public ActiveEffectsManager getActiveEffectsManager() {
        return activeEffectsManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public TrimHUDManager getTrimHUDManager() {
        return trimHUDManager;
    }

    public TrimLoreManager getTrimLoreManager() {
        return trimLoreManager;
    }

    public void reload() {
        reloadConfig();
        trimConfig = new TrimCraftConfig(this);
        getLogger().info("TrimCraft configuration reloaded.");
    }
}
