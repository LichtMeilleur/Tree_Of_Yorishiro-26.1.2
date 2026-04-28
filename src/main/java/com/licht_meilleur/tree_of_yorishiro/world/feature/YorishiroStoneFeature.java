package com.licht_meilleur.tree_of_yorishiro.world.feature;

import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class YorishiroStoneFeature extends Feature<NoneFeatureConfiguration> {

    public YorishiroStoneFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        int x = origin.getX();
        int z = origin.getZ();

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos placePos = new BlockPos(x, y, z);
        BlockPos belowPos = placePos.below();

        BlockState below = level.getBlockState(belowPos);
        BlockState current = level.getBlockState(placePos);

        if (!current.isAir()) {
            return false;
        }

        if (!level.getFluidState(placePos).isEmpty()) {
            return false;
        }

        if (!(below.is(Blocks.GRASS_BLOCK)
                || below.is(Blocks.DIRT)
                || below.is(Blocks.COARSE_DIRT)
                || below.is(Blocks.PODZOL)
                || below.is(Blocks.STONE)
                || below.is(BlockTags.DIRT))) {
            return false;
        }

        level.setBlock(placePos, ModBlocks.YORISHIRO_STONE.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }
}