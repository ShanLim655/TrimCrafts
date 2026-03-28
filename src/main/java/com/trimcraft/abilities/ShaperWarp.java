package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ShaperWarp extends TrimAbility {

    public ShaperWarp(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Reality Warp"; }
    @Override public String getDescription() {
        return "Fold space itself, teleporting instantly in the direction you're looking — reality bends to your will.";
    }
    @Override public String getCooldownKey() { return "shaper-warp"; }
    @Override public int getDefaultCooldown() { return 15; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double maxDist = plugin.getTrimConfig().getDouble("shaper.warp.max-distance", 20.0);
        Location from = player.getLocation();
        Location to = player.getTargetBlockExact((int) maxDist) != null
                ? player.getTargetBlockExact((int) maxDist).getLocation().add(0, 1, 0)
                : player.getLocation().add(player.getLocation().getDirection().multiply(maxDist));

        to.setYaw(from.getYaw());
        to.setPitch(from.getPitch());

        ParticleUtils.spawnPortalVortex(from);
        player.teleport(to);
        ParticleUtils.spawnPortalVortex(to);

        player.getWorld().playSound(to, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        MessageUtils.sendPrefixedMessage(player, "&dReality Warp &7— space folds before you!");
    }
}
