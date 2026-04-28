package com.licht_meilleur.tree_of_yorishiro.world;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> YORISHIRO_STONE =
            ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    TreeofYorishiroMod.id("yorishiro_stone")
            );

    public static void register() {
    }
}