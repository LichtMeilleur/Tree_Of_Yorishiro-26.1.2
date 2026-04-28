package com.licht_meilleur.tree_of_yorishiro.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ModWorldGeneration {

    public static void generateWorldGen() {
        BiomeModifications.addFeature(
                BiomeSelectors.all(),
                GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
                ModPlacedFeatures.YORISHIRO_STONE
        );
    }
}