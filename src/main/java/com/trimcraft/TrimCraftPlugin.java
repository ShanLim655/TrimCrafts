package com.trimcraft;

import com.trimcraft.commands.TrimCraftCommand;
import com.trimcraft.commands.TrimInfoCommand;
import com.trimcraft.config.TrimCraftConfig;
import com.trimcraft.listeners.ArmorChangeListener;
import com.trimcraft.listeners.PlayerInputListener;
import com.trimcraft.listeners.TrimItemListener;
import com.trimcraft.managers.AbilityManager;
import com.trimcraft.managers.CooldownManager;
import com.trimcraft.managers.ActiveEffectsManager;
import com.trimcraft.managers.DatabaseManager;
import com.trimcraft.managers.TrimHUDManager;
import com.trimcraft.managers.TrimLoreManager;
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

        getCommand("trimcraft").setExecutor(new TrimCraftCommand(this));
        getCommand("triminfo").setExecutor(new TrimInfoCommand(this));

        getLogger().info("TrimCraft v" + getDescription().getVersion() + " enabled! Trim the darkness.");
    }

    @Override
    public void onDisable() {
        if (cooldownManager != null) cooldownManager.saveCooldowns();
        if (trimHUDManager != null) trimHUDManager.hideAll();
        if (databaseManager != null) databaseManager.disconnect();
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
        cooldownManager.clearAll();
        abilityManager.reload();
        getLogger().info("TrimCraft reloaded.");
    }
}
