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
        return "Burst forward three times in quick succession, leaving a trail of vex energy.";
    }
    @Override public String getCooldownKey() { return "vex-phantom-dash"; }
    @Override public int getDefaultCooldown() { return 10; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        int dashes = Math.max(1, Math.min(5, plugin.getTrimConfig().getInt("vex.phantom-dash.dashes", 3)));
        int speedDuration = Math.max(1, plugin.getTrimConfig().getInt("vex.phantom-dash.invincibility-ticks", 10));
        int speedAmp = Math.max(0, plugin.getTrimConfig().getInt("vex.phantom-dash.speed-amplifier", 3));
        int[] dashCount = {0};
        BukkitTask[] taskHolder = new BukkitTask[1];

        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || dashCount[0] >= dashes) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "phantom-dash");
                return;
            }

            Vector direction = player.getLocation().getDirection().normalize().multiply(1.5);
            player.setVelocity(direction);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedDuration, speedAmp, false, false));
            ParticleUtils.spawnColoredDust(player.getLocation().add(0, 1, 0), 200, 200, 255, 1f, 10);
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VEX_HURT, 0.5f, 1.5f);
            dashCount[0]++;

            if (dashCount[0] >= dashes) {
                taskHolder[0].cancel();
                plugin.getActiveEffectsManager().unregisterTask(player, "phantom-dash");
            }
        }, 1L, 5L);
        taskHolder[0] = task;

        plugin.getActiveEffectsManager().registerTask(player, "phantom-dash", task);
        MessageUtils.sendPrefixedMessage(player, "&fPhantom Dash &7— you dart like a vex!");
    }
}
