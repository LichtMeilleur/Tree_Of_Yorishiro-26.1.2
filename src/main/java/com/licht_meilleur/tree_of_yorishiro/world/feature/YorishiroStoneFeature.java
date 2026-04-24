package com.licht_meilleur.tree_of_yorishiro.world.feature;

import com.mojang.serialization.Codec;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class YorishiroStoneFeature extends Feature<DefaultFeatureConfig> {

    public YorishiroStoneFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();

        int x = origin.getX();
        int z = origin.getZ();

        int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos placePos = new BlockPos(x, y, z);
        BlockPos belowPos = placePos.down();

        BlockState below = world.getBlockState(belowPos);
        BlockState current = world.getBlockState(placePos);

        if (!current.isAir()) {
            return false;
        }

        if (!world.getFluidState(placePos).isEmpty()) {
            return false;
        }

        if (!(below.isOf(Blocks.GRASS_BLOCK)
                || below.isOf(Blocks.DIRT)
                || below.isOf(Blocks.COARSE_DIRT)
                || below.isOf(Blocks.PODZOL)
                || below.isOf(Blocks.STONE)
                || below.isIn(BlockTags.DIRT))) {
            return false;
        }

        world.setBlockState(placePos, ModBlocks.YORISHIRO_STONE.getDefaultState(), Block.NOTIFY_ALL);
        return true;
    }
}