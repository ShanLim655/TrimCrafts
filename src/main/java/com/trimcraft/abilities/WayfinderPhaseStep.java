package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WayfinderPhaseStep extends TrimAbility {

    public WayfinderPhaseStep(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Phase Step"; }
    @Override public String getDescription() {
        return "Step between planes of existence — blink in the direction you face, passing through thin walls and obstacles.";
    }
    @Override public String getCooldownKey() { return "wayfinder-phase-step"; }
    @Override public int getDefaultCooldown() { return 12; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double distance = plugin.getTrimConfig().getDouble("wayfinder.phase-step.blink-distance", 15.0);
        Location from = player.getLocation();
        Location to = from.clone().add(from.getDirection().normalize().multiply(distance));
        to.setYaw(from.getYaw());
        to.setPitch(from.getPitch());

        ParticleUtils.spawnPortalVortex(from);

        // Find safe location
        for (int i = (int) distance; i >= 1; i--) {
            Location test = from.clone().add(from.getDirection().normalize().multiply(i));
            if (test.getBlock().isPassable() && test.clone().add(0, 1, 0).getBlock().isPassable()) {
                to = test;
                break;
            }
        }

        player.teleport(to);
        ParticleUtils.spawnPortalVortex(to);
        player.getWorld().playSound(to, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1.5f);
        MessageUtils.sendPrefixedMessage(player, "&dPhase Step &7— you slip between dimensions!");
    }
}
