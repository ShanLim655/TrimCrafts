package com.trimcraft.managers;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.abilities.TrimAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TrimHUDManager {

    private final TrimCraftPlugin plugin;
    private final Map<UUID, Scoreboard> playerBoards = new HashMap<>();

    public TrimHUDManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public void showHUD(Player player, String trimKey) {
        if (!plugin.getTrimConfig().isShowHUD()) {
            hideHUD(player);
            return;
        }

        TrimAbility[] abilities = plugin.getAbilityManager().getAbilities(trimKey);
        if (abilities == null || abilities.length == 0) return;

        String trimName = capitalize(trimKey);

        ScoreboardManager sm = Bukkit.getScoreboardManager();
        Scoreboard board = sm.getNewScoreboard();

        Objective obj = board.registerNewObjective("trimhud", "dummy",
                ChatColor.GOLD + "" + ChatColor.BOLD + trimName + " Trim");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = buildHUDLines(trimName, abilities);

        for (int i = 0; i < lines.size(); i++) {
            String entry = lines.get(i);
            Score score = obj.getScore(padEntry(entry, i));
            score.setScore(lines.size() - i);
        }

        playerBoards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
    }

    public void hideHUD(Player player) {
        playerBoards.remove(player.getUniqueId());
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        if (sm != null) {
            player.setScoreboard(sm.getMainScoreboard());
        }
    }

    public void hideAll() {
        for (UUID uuid : new HashSet<>(playerBoards.keySet())) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) hideHUD(p);
        }
        playerBoards.clear();
    }

    private List<String> buildHUDLines(String trimName, TrimAbility[] abilities) {
        boolean showNames   = plugin.getTrimConfig().isHudShowAbilityNames();
        boolean showKeybind = plugin.getTrimConfig().isHudShowKeybinds();
        boolean showDesc    = plugin.getTrimConfig().isHudShowDescriptions();
        boolean showSep     = plugin.getTrimConfig().isHudShowSeparator();

        List<String> lines = new ArrayList<>();
        if (showSep) lines.add(ChatColor.DARK_GRAY + "▔▔▔▔▔▔▔▔▔▔▔▔▔");

        if (abilities.length > 0) {
            TrimAbility a1 = abilities[0];
            if (showNames)   lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + a1.getName());
            if (showKeybind) lines.add(ChatColor.DARK_AQUA + "  Sneak + L-Click");
            if (showDesc) {
                String short1 = truncate(a1.getDescription(), 22);
                lines.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "  " + short1);
            }
        }

        lines.add("");

        if (abilities.length > 1) {
            TrimAbility a2 = abilities[1];
            if (showNames)   lines.add(ChatColor.GRAY + "" + ChatColor.ITALIC + a2.getName());
            if (showKeybind) lines.add(ChatColor.DARK_AQUA + "  Sneak + R-Click");
            if (showDesc) {
                String short2 = truncate(a2.getDescription(), 22);
                lines.add(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "  " + short2);
            }
        }

        if (showSep) lines.add(ChatColor.DARK_GRAY + "▔▔▔▔▔▔▔▔▔▔▔▔▔");
        return lines;
    }

    private String truncate(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, max - 2) + "..";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String padEntry(String base, int index) {
        StringBuilder sb = new StringBuilder(base);
        for (int i = 0; i < index; i++) sb.append(ChatColor.RESET);
        return sb.toString();
    }
}
