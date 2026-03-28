package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ShaperGravityCrush extends TrimAbility {

    public ShaperGravityCrush(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Gravity Crush"; }
    @Override public String getDescription() {
        return "Invert gravity in a zone around you, pulling enemies to the ground and crushing them with immense force.";
    }
    @Override public String getCooldownKey() { return "shaper-gravity-crush"; }
    @Override public int getDefaultCooldown() { return 40; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double radius = plugin.getTrimConfig().getDouble("shaper.gravity-crush.radius", 6.0);
        double damage = plugin.getTrimConfig().getDouble("shaper.gravity-crush.damage", 4.0);
        int slowDur = plugin.getTrimConfig().getInt("shaper.gravity-crush.slowness-duration", 60);

        Location center = player.getLocation();
        for (Entity entity : player.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity == player || !(entity instanceof LivingEntity living)) continue;
            Vector down = new Vector(0, -2.0, 0);
            entity.setVelocity(down);
            living.damage(damage, player);
            if (entity instanceof Player target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDur, 2, false, true));
                MessageUtils.sendPrefixedMessage(target, "&5Gravity Crush &7slams you down!");
            }
        }

        ParticleUtils.spawnExplosionRing(center, 120, 0, 200, radius / 2);
        player.getWorld().playSound(center, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 2f);
        MessageUtils.sendPrefixedMessage(player, "&5Gravity Crush &7— the world bends inward!");
    }
}
