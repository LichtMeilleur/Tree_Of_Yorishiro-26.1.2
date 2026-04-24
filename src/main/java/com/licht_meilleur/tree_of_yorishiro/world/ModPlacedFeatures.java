package com.licht_meilleur.tree_of_yorishiro.world;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModPlacedFeatures {

    public static final RegistryKey<PlacedFeature> YORISHIRO_STONE =
            RegistryKey.of(
                    RegistryKeys.PLACED_FEATURE,
                    TreeofYorishiroMod.id("yorishiro_stone")
            );

    public static void register() {
    }
}