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

public class BoltLightningStrike extends TrimAbility {

    public BoltLightningStrike(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Lightning Strike"; }
    @Override public String getDescription() {
        return "Call down a bolt of pure storm energy — a targeted lightning strike that stuns and damages all enemies in the impact zone.";
    }
    @Override public String getCooldownKey() { return "bolt-lightning-strike"; }
    @Override public int getDefaultCooldown() { return 15; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        double damage = plugin.getTrimConfig().getDouble("bolt.lightning-strike.damage", 6.0);
        double radius = plugin.getTrimConfig().getDouble("bolt.lightning-strike.radius", 3.0);
        int stunDur = plugin.getTrimConfig().getInt("bolt.lightning-strike.stun-duration", 40);

        Location target = player.getTargetBlock(null, 30).getLocation().add(0, 1, 0);
        player.getWorld().strikeLightningEffect(target);

        for (Entity entity : player.getWorld().getNearbyEntities(target, radius, radius, radius)) {
            if (entity == player || !(entity instanceof LivingEntity living)) continue;
            living.damage(damage, player);
            if (entity instanceof Player hit) {
                hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, stunDur, 5, false, true));
                MessageUtils.sendPrefixedMessage(hit, "&eLightning &7strikes you — stunned!");
            }
            ParticleUtils.spawnLightningParticles(entity.getLocation());
        }

        player.getWorld().playSound(target, org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
        MessageUtils.sendPrefixedMessage(player, "&eLightning Strike &7— storm calls on command!");
    }
}
