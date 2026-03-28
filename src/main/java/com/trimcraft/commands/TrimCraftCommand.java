package com.trimcraft.commands;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.abilities.TrimAbility;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.TrimUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrimCraftCommand implements CommandExecutor {

    private final TrimCraftPlugin plugin;

    public TrimCraftCommand(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&6TrimCraft &8v" + plugin.getDescription().getVersion()));
            sender.sendMessage(MessageUtils.colorize("&7/trimcraft reload &8- Reload config"));
            sender.sendMessage(MessageUtils.colorize("&7/trimcraft list &8- List all trims"));
            sender.sendMessage(MessageUtils.colorize("&7/trimcraft info <trim> &8- Trim details"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("trimcraft.admin")) {
                    sender.sendMessage(MessageUtils.colorize("&cNo permission."));
                    return true;
                }
                plugin.reload();
                sender.sendMessage(MessageUtils.colorize(
                        plugin.getTrimConfig().getPrefix() + "&aPlugin reloaded!"));
            }
            case "list" -> {
                sender.sendMessage(MessageUtils.colorize("&6Available Trims:"));
                plugin.getAbilityManager().getAllAbilities().forEach((trim, abilities) -> {
                    sender.sendMessage(MessageUtils.colorize(
                            "  &e" + TrimUtils.formatTrimName(trim) + " &8- &7" + abilities.length + " abilities"));
                });
            }
            case "info" -> {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtils.colorize("&cUsage: /trimcraft info <trim>"));
                    return true;
                }
                String trim = args[1].toLowerCase();
                TrimAbility[] abilities = plugin.getAbilityManager().getAbilities(trim);
                if (abilities.length == 0) {
                    sender.sendMessage(MessageUtils.colorize("&cUnknown trim: " + trim));
                    return true;
                }
                sender.sendMessage(MessageUtils.colorize("&6=== " + TrimUtils.formatTrimName(trim) + " Trim ==="));
                for (TrimAbility ability : abilities) {
                    String keybind = ability.getType() == TrimAbility.AbilityType.LEFT_CLICK
                            ? "Sneak+LMB" : "Sneak+RMB";
                    sender.sendMessage(MessageUtils.colorize(
                            "  &e[" + keybind + "] &b" + ability.getName()));
                    sender.sendMessage(MessageUtils.colorize(
                            "      &7" + ability.getDescription()));
                    sender.sendMessage(MessageUtils.colorize(
                            "      &8Cooldown: &f" + ability.getCooldown() + "s"));
                }
            }
            default -> sender.sendMessage(MessageUtils.colorize("&cUnknown subcommand."));
        }
        return true;
    }
}
