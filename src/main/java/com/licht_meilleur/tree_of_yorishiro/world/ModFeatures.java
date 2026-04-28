package com.licht_meilleur.tree_of_yorishiro.world;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.world.feature.YorishiroStoneFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ModFeatures {

    public static final Feature<NoneFeatureConfiguration> YORISHIRO_STONE_FEATURE =
            Registry.register(
                    BuiltInRegistries.FEATURE,
                    TreeofYorishiroMod.id("yorishiro_stone_feature"),
                    new YorishiroStoneFeature(NoneFeatureConfiguration.CODEC)
            );

    public static void register() {
    }
}