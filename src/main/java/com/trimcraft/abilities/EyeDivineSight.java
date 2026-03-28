package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EyeDivineSight extends TrimAbility {

    public EyeDivineSight(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Divine Sight"; }
    @Override public String getDescription() {
        return "Open your all-seeing eye — reveal all nearby players through walls with glowing, and grant yourself night vision.";
    }
    @Override public String getCooldownKey() { return "eye-divine-sight"; }
    @Override public int getDefaultCooldown() { return 25; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = plugin.getTrimConfig().getDouble("eye.divine-sight.radius", 30.0);
        int glowDur = plugin.getTrimConfig().getInt("eye.divine-sight.glowing-duration", 200);
        int nvDur = plugin.getTrimConfig().getInt("eye.divine-sight.night-vision-duration", 200);

        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, nvDur, 0, false, true));

        int revealed = 0;
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (entity == player) continue;
            entity.setGlowing(true);
            if (entity instanceof Player target) {
                MessageUtils.sendPrefixedMessage(target, "&eA divine eye &7is watching you...");
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> entity.setGlowing(false), glowDur);
            revealed++;
        }

        ParticleUtils.spawnEnchantmentParticles(player);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.5f);
        MessageUtils.sendPrefixedMessage(player, "&eDivine Sight &7— " + revealed + " entities revealed!");
    }
}
