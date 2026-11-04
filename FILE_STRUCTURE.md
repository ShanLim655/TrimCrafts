# TrimCraft Plugin - File Structure

## mkdir & touch commands to recreate the structure

```bash
# Root directory
mkdir -p trimcraft-plugin/src/main/java/com/trimcraft/{abilities,commands,config,listeners,managers,utils}
mkdir -p trimcraft-plugin/src/main/resources

# Resources
touch trimcraft-plugin/pom.xml
touch trimcraft-plugin/src/main/resources/plugin.yml
touch trimcraft-plugin/src/main/resources/config.yml

# Core plugin
touch trimcraft-plugin/src/main/java/com/trimcraft/TrimCraftPlugin.java

# Config
touch trimcraft-plugin/src/main/java/com/trimcraft/config/TrimCraftConfig.java

# Managers
touch trimcraft-plugin/src/main/java/com/trimcraft/managers/AbilityManager.java
touch trimcraft-plugin/src/main/java/com/trimcraft/managers/CooldownManager.java
touch trimcraft-plugin/src/main/java/com/trimcraft/managers/ActiveEffectsManager.java

# Utils
touch trimcraft-plugin/src/main/java/com/trimcraft/utils/TrimUtils.java
touch trimcraft-plugin/src/main/java/com/trimcraft/utils/ParticleUtils.java
touch trimcraft-plugin/src/main/java/com/trimcraft/utils/MessageUtils.java

# Listeners
touch trimcraft-plugin/src/main/java/com/trimcraft/listeners/PlayerInputListener.java
touch trimcraft-plugin/src/main/java/com/trimcraft/listeners/ArmorChangeListener.java

# Commands
touch trimcraft-plugin/src/main/java/com/trimcraft/commands/TrimCraftCommand.java
touch trimcraft-plugin/src/main/java/com/trimcraft/commands/TrimInfoCommand.java

# Abstract base
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/TrimAbility.java

# SILENCE TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SilenceLifeForceSuction.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SilenceAbility.java

# WILD TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/WildNatureBond.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/WildBeastRage.java

# COAST TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/CoastTidalWave.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/CoastSeaShield.java

# DUNE TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/DuneSandstorm.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/DuneMirages.java

# EYE TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/EyeDivineSight.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/EyeCurseOfSight.java

# HOST TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/HostSpectralAnchor.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/HostUndyingWill.java

# RAISER TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/RaiserRaiseDead.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/RaiserDeathMark.java

# SENTRY TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SentryIronWall.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SentryExecute.java

# SHAPER TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/ShaperWarp.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/ShaperGravityCrush.java

# SNOUT TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SnoutBruteCharge.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SnoutWarCry.java

# SPIRE TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SpireInfernoStrike.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/SpireFireShield.java

# TIDE TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/TideTsunami.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/TideAbyssalForm.java

# VEX TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/VexPhantomDash.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/VexEtherealBlade.java

# WARD TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/WardReflect.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/WardSanctuary.java

# WAYFINDER TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/WayfinderPhaseStep.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/WayfinderCartographersCurse.java

# BOLT TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/BoltLightningStrike.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/BoltStormArmor.java

# FLOW TRIM
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/FlowTimeWarp.java
touch trimcraft-plugin/src/main/java/com/trimcraft/abilities/FlowTemporalShield.java
```

## Compile

```bash
cd trimcraft-plugin
mvn -B clean verify
Output: target/TrimCraft-3.1.0.jar
```
