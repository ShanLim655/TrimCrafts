package com.trimcraft.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public final class CombatUtils {

    private CombatUtils() {
    }

    public static boolean canTarget(Player attacker, Entity target) {
        if (attacker == null || target == null || target == attacker || !target.isValid()) return false;
        if (attacker.getWorld() != target.getWorld()) return false;

        if (target instanceof Player player) {
            if (!player.isOnline() || player.isDead()) return false;
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return false;

            Team attackerTeam = attacker.getScoreboard().getEntryTeam(attacker.getName());
            Team targetTeam = attacker.getScoreboard().getEntryTeam(player.getName());
            if (attackerTeam != null && targetTeam != null
                    && attackerTeam.getName().equals(targetTeam.getName())) return false;
        }

        return true;
    }

    public static boolean areAllies(Player first, Player second) {
        if (first == second) return true;
        if (first == null || second == null || first.getWorld() != second.getWorld()) return false;

        Team firstTeam = first.getScoreboard().getEntryTeam(first.getName());
        Team secondTeam = first.getScoreboard().getEntryTeam(second.getName());
        return firstTeam != null && secondTeam != null
                && firstTeam.getName().equals(secondTeam.getName());
    }
}
