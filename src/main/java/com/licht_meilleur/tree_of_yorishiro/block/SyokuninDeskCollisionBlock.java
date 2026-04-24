package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SyokuninDeskCollisionBlock extends Block {

    public SyokuninDeskCollisionBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE; // 確認中。あとで INVISIBLE に戻す 表示はMODEL
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0, 0, 0, 16, 16, 16);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockPos deskPos = findNearbyDesk(world, pos);
            if (deskPos != null) {
                BlockState deskState = world.getBlockState(deskPos);
                if (deskState.isOf(ModBlocks.SYOKUNIN_DESK)) {
                    Block.dropStack(world, deskPos, new ItemStack(ModItems.YORISYOKUNIN_SUMMON));
                    world.removeBlock(deskPos, false);
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }

    private BlockPos findNearbyDesk(World world, BlockPos pos) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos check = pos.add(dx, dy, dz);
                    if (world.getBlockState(check).isOf(ModBlocks.SYOKUNIN_DESK)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}