package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import com.trimcraft.utils.TargetUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EyeCurseOfSight extends TrimAbility {

    public EyeCurseOfSight(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Curse of Sight"; }
    @Override public String getDescription() {
        return "Curse a player — they glow for all to see while suffering blinding visions. The hunter becomes the hunted.";
    }
    @Override public String getCooldownKey() { return "eye-curse-of-sight"; }
    @Override public int getDefaultCooldown() { return 40; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double range = plugin.getTrimConfig().getDouble("eye.curse-of-sight.range", 12.0);
        int blindDur = plugin.getTrimConfig().getInt("eye.curse-of-sight.blindness-duration", 100);
        int glowDur = plugin.getTrimConfig().getInt("eye.curse-of-sight.glowing-duration", 100);

        Player target = TargetUtils.getLookTargetPlayer(player, range);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cNo target in range!");
            cancelCooldownForActivation(player);
            return;
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDur, 2, false, true));
        target.setGlowing(true);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> target.setGlowing(false), glowDur);

        ParticleUtils.spawnAuraRing(target, 255, 200, 0);
        target.getWorld().playSound(target.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_STARE, 1f, 0.5f);
        MessageUtils.sendPrefixedMessage(player, "&eCurse of Sight &7placed on &b" + target.getName() + "&7!");
        MessageUtils.sendPrefixedMessage(target, "&eThe Eye &7sees all — you cannot hide.");
    }

}
