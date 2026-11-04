package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import com.trimcraft.utils.TargetUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class VexEtherealBlade extends TrimAbility {

    public VexEtherealBlade(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Ethereal Blade"; }
    @Override public String getDescription() {
        return "Conjure a blade of vex energy and strike the living target directly in front of you.";
    }
    @Override public String getCooldownKey() { return "vex-ethereal-blade"; }
    @Override public int getDefaultCooldown() { return 35; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        double damage = plugin.getTrimConfig().getDouble("vex.ethereal-blade.damage", 8.0);
        double range = plugin.getTrimConfig().getDouble("vex.ethereal-blade.range", 6.0);

        LivingEntity target = TargetUtils.getLookTargetLivingEntity(player, range);
        if (target == null) {
            MessageUtils.sendPrefixedMessage(player, "&cAim at a living target to use Ethereal Blade!");
            cancelCooldownForActivation(player);
            return;
        }

        target.damage(damage, player);
        ParticleUtils.spawnColoredDust(target.getLocation().add(0, 1, 0), 150, 150, 255, 1.5f, 20);
        if (target instanceof Player hit) {
            MessageUtils.sendPrefixedMessage(hit, "&fEthereal Blade &7cuts through the air toward you!");
        }

        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VEX_CHARGE, 1f, 1f);
        MessageUtils.sendPrefixedMessage(player, "&fEthereal Blade &7— armor is no barrier!");
    }

}
