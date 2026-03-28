package com.trimcraft.commands;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.abilities.TrimAbility;
import com.trimcraft.managers.CooldownManager;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.TrimUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrimInfoCommand implements CommandExecutor {

    private final TrimCraftPlugin plugin;

    public TrimInfoCommand(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String trim = TrimUtils.getFullSetTrim(player);
        if (trim == null) {
            MessageUtils.sendPrefixedMessage(player, "&cYou don't have a full matching trim set equipped.");
            return true;
        }

        CooldownManager cdm = plugin.getCooldownManager();
        TrimAbility[] abilities = plugin.getAbilityManager().getAbilities(trim);

        MessageUtils.sendMessage(player, MessageUtils.colorize(
                "&6=== &e" + TrimUtils.formatTrimName(trim) + " Trim &6==="));

        for (TrimAbility ability : abilities) {
            String keybind = ability.getType() == TrimAbility.AbilityType.LEFT_CLICK ? "Sneak+LMB" : "Sneak+RMB";
            long remaining = cdm.getRemainingSeconds(player, ability.getCooldownKey());
            String cdStr = remaining > 0 ? "&c" + remaining + "s" : "&aReady";
            MessageUtils.sendMessage(player, MessageUtils.colorize(
                    "  &e[" + keybind + "] &b" + ability.getName() + " &8| " + cdStr));
            MessageUtils.sendMessage(player, MessageUtils.colorize(
                    "      &7" + ability.getDescription()));
        }
        return true;
    }
}
