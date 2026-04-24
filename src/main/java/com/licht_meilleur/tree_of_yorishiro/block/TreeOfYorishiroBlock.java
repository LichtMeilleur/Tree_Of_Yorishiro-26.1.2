package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TreeOfYorishiroBlock extends Block implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    private static final int[][] COLLISION_OFFSETS = {
            {0, 1, 0},
            {0, 2, 0},
            {0, 3, 0},
            {0, 4, 0},
            {0, 5, 0},
            {0, 6, 0},

            {-1, 3, 0},
            {-2, 3, 0},
            {-2, 5, 0},
            {-3, 6, 0},
            {-1, 5, 0},
            {1, 5, 0},
            {2, 5, 0},
            {2, 6, 0},
            {0, 3, 0},
            {1, 3, 0},
            {2, 3, 0},

            {-3, 7, -3},
            {-3, 7, -1},
            {0, 7, -3},
            {0, 7, -1}
    };

    public TreeOfYorishiroBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TreeOfYorishiroBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!(world instanceof ServerWorld sw)) return;
        if (!(sw.getBlockEntity(pos) instanceof TreeOfYorishiroBlockEntity be)) return;

        be.initDefaultChibisIfNeeded();
        be.startGrowAnimation();
        be.markDirty();

        clearNearbyCollisionBlocks(world, pos);
        placeCollisionBlocks(world, pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (world.getBlockEntity(pos) instanceof TreeOfYorishiroBlockEntity be) {
                player.openHandledScreen(be);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type) {

        if (world.isClient) {
            return null;
        }

        return new BlockEntityTicker<T>() {
            @Override
            public void tick(World world1, BlockPos pos1, BlockState state1, T be) {
                if (be instanceof TreeOfYorishiroBlockEntity yorishiro) {
                    TreeOfYorishiroBlockEntity.tick(world1, pos1, state1, yorishiro);
                }
            }
        };
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            return;
        }

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TreeOfYorishiroBlockEntity treeBe) {
                treeBe.removeAllChibishiro();
            }

            removeCollisionBlocks(world, pos, state);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            removeCollisionBlocks(world, pos, state);

            dropStack(world, pos, new ItemStack(ModItems.TREE_OF_YORISHIRO_ITEM));
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    private BlockPos rotateOffset(BlockPos origin, int offX, int offY, int offZ, Direction facing) {
        return switch (facing) {
            case SOUTH -> origin.add(offX, offY, offZ);
            case WEST  -> origin.add(-offZ, offY, offX);
            case NORTH -> origin.add(-offX, offY, -offZ);
            case EAST  -> origin.add(offZ, offY, -offX);
            default    -> origin.add(offX, offY, offZ);
        };
    }

    private void placeCollisionBlocks(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(FACING);

        for (int[] offset : COLLISION_OFFSETS) {
            BlockPos target = rotateOffset(pos, offset[0], offset[1], offset[2], facing);

            if (world.getBlockState(target).isAir()) {
                world.setBlockState(target, ModBlocks.YORISHIRO_TRUNK_COLLISION.getDefaultState(), Block.NOTIFY_ALL);
            }
        }
    }

    private void removeCollisionBlocks(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(FACING);

        for (int[] offset : COLLISION_OFFSETS) {
            BlockPos target = rotateOffset(pos, offset[0], offset[1], offset[2], facing);

            if (world.getBlockState(target).isOf(ModBlocks.YORISHIRO_TRUNK_COLLISION)) {
                world.removeBlock(target, false);
            }
        }
    }

    private void clearNearbyCollisionBlocks(World world, BlockPos pos) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = 0; dy <= 8; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos target = pos.add(dx, dy, dz);
                    if (world.getBlockState(target).isOf(ModBlocks.YORISHIRO_TRUNK_COLLISION)) {
                        world.removeBlock(target, false);
                    }
                }
            }
        }
    }
}