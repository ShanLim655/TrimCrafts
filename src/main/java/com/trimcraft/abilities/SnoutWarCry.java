package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.CombatUtils;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SnoutWarCry extends TrimAbility {

    public SnoutWarCry(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "War Cry"; }
    @Override public String getDescription() {
        return "Rally nearby allies, granting them strength and haste for 5 seconds.";
    }
    @Override public String getCooldownKey() { return "snout-war-cry"; }
    @Override public int getDefaultCooldown() { return 45; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = Math.max(0.0, plugin.getTrimConfig().getDouble("snout.war-cry.radius", 10.0));
        int duration = Math.max(1, plugin.getTrimConfig().getInt("snout.war-cry.strength-duration", 100));
        int buffed = 0;

        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (!(entity instanceof Player ally) || !CombatUtils.areAllies(player, ally)) continue;

            ally.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, 1, false, true));
            ally.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, 0, false, true));
            if (ally != player) {
                MessageUtils.sendPrefixedMessage(ally, "&c" + player.getName() + "'s War Cry &7fills you with might!");
            }
            buffed++;
        }

        ParticleUtils.spawnExplosionRing(player.getLocation(), 220, 50, 0, 3.0);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_RAVAGER_ROAR, 2f, 0.6f);
        MessageUtils.sendPrefixedMessage(player, "&cWar Cry &7— " + buffed + " allies empowered!");
    }
}
