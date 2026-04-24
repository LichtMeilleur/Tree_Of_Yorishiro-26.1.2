package com.licht_meilleur.tree_of_yorishiro.world;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.world.feature.YorishiroStoneFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {

    public static final Feature<DefaultFeatureConfig> YORISHIRO_STONE_FEATURE =
            Registry.register(
                    Registries.FEATURE,
                    new Identifier(TreeofYorishiroMod.MOD_ID, "yorishiro_stone_feature"),
                    new YorishiroStoneFeature(DefaultFeatureConfig.CODEC)
            );

    public static void register() {
    }
}