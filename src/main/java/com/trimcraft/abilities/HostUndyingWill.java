package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HostUndyingWill extends TrimAbility {

    public HostUndyingWill(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Undying Will"; }
    @Override public String getDescription() {
        return "Call on the spirit world for a brief burst of regeneration, resistance, and fire resistance.";
    }
    @Override public String getCooldownKey() { return "host-undying-will"; }
    @Override public int getDefaultCooldown() { return 60; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int regenDur = plugin.getTrimConfig().getInt("host.undying-will.regeneration-duration", 60);
        int totemDur = plugin.getTrimConfig().getInt("host.undying-will.totem-effect-duration", 100);

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regenDur, 3, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, totemDur, 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, totemDur, 0, false, true));

        ParticleUtils.spawnDeathParticles(player.getLocation().add(0, 1, 0));
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ITEM_TOTEM_USE, 1f, 0.8f);
        MessageUtils.sendPrefixedMessage(player, "&fUndying Will &7— the spirits sustain your life!");
    }
}
