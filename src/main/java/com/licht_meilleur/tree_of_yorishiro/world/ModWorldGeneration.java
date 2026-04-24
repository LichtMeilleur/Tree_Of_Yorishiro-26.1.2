package com.licht_meilleur.tree_of_yorishiro.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class ModWorldGeneration {

    public static void generateWorldGen() {
        BiomeModifications.addFeature(
                BiomeSelectors.all(),
                GenerationStep.Feature.TOP_LAYER_MODIFICATION,
                ModPlacedFeatures.YORISHIRO_STONE
        );
    }
}