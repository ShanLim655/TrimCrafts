package com.trimcraft.utils;

import com.trimcraft.TrimCraftPlugin;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static void sendMessage(Player player, String message) {
        player.sendMessage(colorize(message));
    }

    public static void sendPrefixedMessage(Player player, String message) {
        String prefix = TrimCraftPlugin.getInstance().getTrimConfig().getPrefix();
        player.sendMessage(colorize(prefix + message));
    }

    @SuppressWarnings("deprecation")
    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(
            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
            net.md_5.bungee.api.chat.TextComponent.fromLegacyText(colorize(message))
        );
    }

    public static String colorize(String s) {
        return s.replace("&", "\u00A7");
    }
}
