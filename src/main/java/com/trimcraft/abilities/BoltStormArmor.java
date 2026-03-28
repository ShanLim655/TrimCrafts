package com.trimcraft.abilities;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.utils.MessageUtils;
import com.trimcraft.utils.ParticleUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class BoltStormArmor extends TrimAbility implements Listener {

    private final Set<UUID> stormActive = new HashSet<>();
    private final Random random = new Random();

    public BoltStormArmor(TrimCraftPlugin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override public String getName() { return "Storm Armor"; }
    @Override public String getDescription() {
        return "Electrify your armor — there is a 25% chance any attacker will be shocked by a discharge of storm energy.";
    }
    @Override public String getCooldownKey() { return "bolt-storm-armor"; }
    @Override public int getDefaultCooldown() { return 45; }
    @Override public AbilityType getType() { return AbilityType.RIGHT_CLICK; }

    @Override
    protected void execute(Player player) {
        int duration = plugin.getTrimConfig().getInt("bolt.storm-armor.duration-ticks", 200);

        stormActive.add(player.getUniqueId());
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0, false, false));

        ParticleUtils.spawnAuraRing(player, 255, 255, 0);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 1.5f);

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            stormActive.remove(player.getUniqueId());
        }, duration);

        plugin.getActiveEffectsManager().registerTask(player, "storm-armor", task);
        MessageUtils.sendPrefixedMessage(player, "&eStorm Armor &7— your body crackles with electricity!");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!stormActive.contains(victim.getUniqueId())) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        int chance = plugin.getTrimConfig().getInt("bolt.storm-armor.shock-chance", 25);
        if (random.nextInt(100) >= chance) return;

        double shockDmg = plugin.getTrimConfig().getDouble("bolt.storm-armor.shock-damage", 2.0);
        attacker.damage(shockDmg);
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 2, false, true));
        victim.getWorld().strikeLightningEffect(attacker.getLocation());
        MessageUtils.sendPrefixedMessage(attacker, "&eStorm Armor &7— you were shocked!");
    }
}
