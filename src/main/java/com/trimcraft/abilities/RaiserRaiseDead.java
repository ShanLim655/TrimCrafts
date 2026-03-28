package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitTask;

public class RaiserRaiseDead extends TrimAbility {

    public RaiserRaiseDead(TrimCraftPlugin plugin) {
        super(plugin);
    }

    @Override public String getName() { return "Raise Dead"; }
    @Override public String getDescription() {
        return "Tear open the earth and summon undead warriors to fight at your side — an army from beyond the grave.";
    }
    @Override public String getCooldownKey() { return "raiser-raise-dead"; }
    @Override public int getDefaultCooldown() { return 45; }
    @Override public AbilityType getType() { return AbilityType.LEFT_CLICK; }

    @Override
    protected void execute(Player player) {
        int count = plugin.getTrimConfig().getInt("raiser.raise-dead.zombie-count", 3);
        int duration = plugin.getTrimConfig().getInt("raiser.raise-dead.zombie-duration-ticks", 400);

        Location loc = player.getLocation();
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i;
            Location spawnLoc = loc.clone().add(Math.cos(angle) * 2, 0, Math.sin(angle) * 2);
            Zombie zombie = (Zombie) player.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
            zombie.setCustomName(player.getName() + "'s Servant");
            zombie.setCustomNameVisible(true);

            ParticleUtils.spawnColoredDust(spawnLoc.add(0, 1, 0), 0, 80, 0, 1.5f, 20);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (zombie.isValid()) zombie.remove();
            }, duration);
        }

        player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_ZOMBIE_AMBIENT, 2f, 0.5f);
        MessageUtils.sendPrefixedMessage(player, "&2Raise Dead &7— the fallen answer your call!");
    }
}
