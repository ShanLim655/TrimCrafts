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
    private final Map<UUID, Scoreboard> previousBoards = new HashMap<>();

    public TrimHUDManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void showHUD(Player player, String trimKey) {
        if (!plugin.getTrimConfig().isShowHUD()) {
            hideHUD(player);
            return;
        }

        TrimAbility[] abilities = plugin.getAbilityManager().getAbilities(trimKey);
        if (abilities.length == 0) return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        UUID uuid = player.getUniqueId();
        previousBoards.putIfAbsent(uuid, player.getScoreboard());

        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective(
                "trimhud",
                Criteria.DUMMY,
                ChatColor.GOLD + "" + ChatColor.BOLD + capitalize(trimKey) + " Trim"
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = buildHUDLines(abilities);
        for (int i = 0; i < lines.size(); i++) {
            objective.getScore(padEntry(lines.get(i), i)).setScore(lines.size() - i);
        }

        playerBoards.put(uuid, board);
        player.setScoreboard(board);
    }

    public void hideHUD(Player player) {
        UUID uuid = player.getUniqueId();
        Scoreboard active = playerBoards.remove(uuid);
        Scoreboard previous = previousBoards.remove(uuid);
        if (previous != null && active != null && player.getScoreboard() == active) {
            player.setScoreboard(previous);
        }
    }

    public void hideAll() {
        for (UUID uuid : new HashSet<>(playerBoards.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) hideHUD(player);
        }
        playerBoards.clear();
        previousBoards.clear();
    }

    private List<String> buildHUDLines(TrimAbility[] abilities) {
        boolean showNames = plugin.getTrimConfig().isHudShowAbilityNames();
        boolean showKeybinds = plugin.getTrimConfig().isHudShowKeybinds();
        boolean showDescriptions = plugin.getTrimConfig().isHudShowDescriptions();
        boolean showSeparator = plugin.getTrimConfig().isHudShowSeparator();

        List<String> lines = new ArrayList<>();
        if (showSeparator) lines.add(ChatColor.DARK_GRAY + "────────────");

        for (int index = 0; index < abilities.length; index++) {
            TrimAbility ability = abilities[index];
            if (index > 0) lines.add("");
            if (showNames) lines.add(ChatColor.WHITE + ability.getName());
            if (showKeybinds) {
                String keybind = ability.getType() == TrimAbility.AbilityType.LEFT_CLICK
                        ? "Sneak + L-Click" : "Sneak + R-Click";
                lines.add(ChatColor.AQUA + keybind);
            }
            if (showDescriptions) {
                String description = ability.getDescription();
                lines.add(ChatColor.GRAY + truncate(description, 30));
            }
        }

        if (showSeparator) lines.add(ChatColor.DARK_GRAY + "────────────");
        return lines;
    }

    private String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 1) + "…";
    }

    private String capitalize(String value) {
        return value == null || value.isEmpty()
                ? value
                : value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }

    private String padEntry(String base, int index) {
        return base + ChatColor.values()[index % ChatColor.values().length];
    }
}
