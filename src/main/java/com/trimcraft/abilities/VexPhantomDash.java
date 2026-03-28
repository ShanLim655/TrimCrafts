package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class VexPhantomDash extends TrimAbility {

    public VexPhantomDash(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Phantom Dash"; }
    @Override public String getDescription() {
        return "Dart through the air like a vex — three rapid ethereal dashes with brief invincibility frames between each.";
    }
    @Override public String getCooldownKey() { return "vex-phantom-dash"; }
    @Override public int getDefaultCooldown() { return 10; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        int dashes = plugin.getTrimConfig().getInt("vex.phantom-dash.dashes", 3);
        int invTicks = plugin.getTrimConfig().getInt("vex.phantom-dash.invincibility-ticks", 10);
        int speedAmp = plugin.getTrimConfig().getInt("vex.phantom-dash.speed-amplifier", 3);

        final int[] dashCount = {0};
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (dashCount[0] >= dashes || !player.isOnline()) return;
            Vector dir = player.getLocation().getDirection().normalize().multiply(1.5);
            player.setVelocity(dir);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, invTicks, speedAmp, false, false));
            ParticleUtils.spawnColoredDust(player.getLocation().add(0, 1, 0), 200, 200, 255, 1f, 10);
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VEX_HURT, 0.5f, 1.5f);
            dashCount[0]++;
        }, 0L, 5L);

        plugin.getActiveEffectsManager().registerTask(player, "phantom-dash", task);
        MessageUtils.sendPrefixedMessage(player, "&fPhantom Dash &7— you dart like a vex!");
    }
}
