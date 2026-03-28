package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RibBoneCage extends TrimAbility {

    public RibBoneCage(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Bone Cage"; }
    @Override public String getDescription() {
        return "Conjure phantom rib-bone bars around nearby enemies — trapping them in place with crushing slowness and total sensory deprivation.";
    }
    @Override public String getCooldownKey() { return "rib-bone-cage"; }
    @Override public int getDefaultCooldown() { return 28; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double range = plugin.getTrimConfig().getDouble("rib.bone-cage.range", 6.0);
        int slowDuration = plugin.getTrimConfig().getInt("rib.bone-cage.slow-duration-ticks", 80);
        int blindDuration = plugin.getTrimConfig().getInt("rib.bone-cage.blind-duration-ticks", 60);
        int fatigueDuration = plugin.getTrimConfig().getInt("rib.bone-cage.fatigue-duration-ticks", 100);

        int trapped = 0;
        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), range, range, range)) {
            if (entity == player || !(entity instanceof LivingEntity living)) continue;
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDuration, 3, false, true));
            living.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDuration, 0, false, true));
            living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, fatigueDuration, 2, false, true));
            ParticleUtils.spawnAuraRing(living instanceof Player p ? p : player, 210, 210, 210);
            if (entity instanceof Player target) {
                MessageUtils.sendPrefixedMessage(target, "&fBone Cage &7— rib bones close around you!");
            }
            trapped++;
        }

        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_SKELETON_HURT, 1.2f, 0.5f);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLOCK_BONE_BLOCK_PLACE, 1f, 0.8f);
        MessageUtils.sendPrefixedMessage(player, "&fBone Cage &7— trapped " + trapped + " enemy" + (trapped == 1 ? "" : "s") + " in phantom ribs!");
    }
}
