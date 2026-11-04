package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import com.trimcraft.utils.TargetUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SilenceAbility extends TrimAbility {

    public SilenceAbility(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Silence"; }
    @Override public String getDescription() {
        return "Silence a nearby player — strip their senses with blindness, nausea, weakness and chains of slowness.";
    }
    @Override public String getCooldownKey() { return "silence-silence-ability"; }
    @Override public int getDefaultCooldown() { return 45; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double range = plugin.getTrimConfig().getDouble("silence.silence-ability.range", 10.0);
        Player target = TargetUtils.getLookTargetPlayer(player, range);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cAim at a player to use Silence!");
            cancelCooldownForActivation(player);
            return;
        }

        int blindnessDur = plugin.getTrimConfig().getInt("silence.silence-ability.blindness-duration", 300);
        int slownessDur = plugin.getTrimConfig().getInt("silence.silence-ability.slowness-duration", 200);
        int slownessAmp = plugin.getTrimConfig().getInt("silence.silence-ability.slowness-amplifier", 2);
        int nauseaDur = plugin.getTrimConfig().getInt("silence.silence-ability.nausea-duration", 100);
        int weaknessDur = plugin.getTrimConfig().getInt("silence.silence-ability.weakness-duration", 200);

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessDur, 4, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slownessDur, slownessAmp, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, nauseaDur, 0, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weaknessDur, 0, false, true));

        Location loc = target.getLocation();
        ParticleUtils.spawnAuraRing(target, 100, 0, 100);
        loc.getWorld().spawnParticle(org.bukkit.Particle.SQUID_INK, loc.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.02);

        MessageUtils.sendPrefixedMessage(player, "&5Silence &7cast on &b" + target.getName() + "&7!");
        MessageUtils.sendPrefixedMessage(target, "&5Silence &7has consumed your senses...");
    }

}
