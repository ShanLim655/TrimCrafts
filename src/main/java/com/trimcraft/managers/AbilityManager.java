package com.trimcraft.managers;

import com.trimcraft.TrimCraftPlugin;
import com.trimcraft.abilities.*;
import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private final TrimCraftPlugin plugin;
    private final Map<String, TrimAbility[]> trimAbilities = new HashMap<>();

    public AbilityManager(TrimCraftPlugin plugin) {
        this.plugin = plugin;
        registerAll();
    }

    private void registerAll() {
        trimAbilities.put("silence", new TrimAbility[]{new SilenceLifeForceSuction(plugin), new SilenceAbility(plugin)});
        trimAbilities.put("wild", new TrimAbility[]{new WildNatureBond(plugin), new WildBeastRage(plugin)});
        trimAbilities.put("coast", new TrimAbility[]{new CoastTidalWave(plugin), new CoastSeaShield(plugin)});
        trimAbilities.put("dune", new TrimAbility[]{new DuneSandstorm(plugin), new DuneMirages(plugin)});
        trimAbilities.put("eye", new TrimAbility[]{new EyeDivineSight(plugin), new EyeCurseOfSight(plugin)});
        trimAbilities.put("host", new TrimAbility[]{new HostSpectralAnchor(plugin), new HostUndyingWill(plugin)});
        trimAbilities.put("raiser", new TrimAbility[]{new RaiserRaiseDead(plugin), new RaiserDeathMark(plugin)});
        trimAbilities.put("sentry", new TrimAbility[]{new SentryIronWall(plugin), new SentryExecute(plugin)});
        trimAbilities.put("shaper", new TrimAbility[]{new ShaperWarp(plugin), new ShaperGravityCrush(plugin)});
        trimAbilities.put("snout", new TrimAbility[]{new SnoutBruteCharge(plugin), new SnoutWarCry(plugin)});
        trimAbilities.put("spire", new TrimAbility[]{new SpireInfernoStrike(plugin), new SpireFireShield(plugin)});
        trimAbilities.put("tide", new TrimAbility[]{new TideTsunami(plugin), new TideAbyssalForm(plugin)});
        trimAbilities.put("vex", new TrimAbility[]{new VexPhantomDash(plugin), new VexEtherealBlade(plugin)});
        trimAbilities.put("ward", new TrimAbility[]{new WardReflect(plugin), new WardSanctuary(plugin)});
        trimAbilities.put("wayfinder", new TrimAbility[]{new WayfinderPhaseStep(plugin), new WayfinderCartographersCurse(plugin)});
        trimAbilities.put("bolt", new TrimAbility[]{new BoltLightningStrike(plugin), new BoltStormArmor(plugin)});
        trimAbilities.put("flow", new TrimAbility[]{new FlowTimeWarp(plugin), new FlowTemporalShield(plugin)});
        trimAbilities.put("rib", new TrimAbility[]{new RibBoneCage(plugin), new RibNetherFortitude(plugin)});
    }


    public TrimAbility[] getAbilities(String trim) {
        return trimAbilities.getOrDefault(trim, new TrimAbility[0]);
    }

    public TrimAbility getAbility(String trim, int index) {
        TrimAbility[] abilities = trimAbilities.get(trim);
        if (abilities == null || index >= abilities.length) return null;
        return abilities[index];
    }

    public boolean hasTrim(String trim) {
        return trimAbilities.containsKey(trim);
    }

    public Map<String, TrimAbility[]> getAllAbilities() {
        return trimAbilities;
    }
}
