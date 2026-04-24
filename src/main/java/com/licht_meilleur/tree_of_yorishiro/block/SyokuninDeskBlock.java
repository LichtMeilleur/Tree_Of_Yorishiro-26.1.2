package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SyokuninDeskBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public SyokuninDeskBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SyokuninDeskBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
        if (factory != null) {
            player.openHandledScreen(factory);
        }

        return ActionResult.CONSUME;
    }

    // 本体には判定を持たせない
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    /**
     * 北向き基準
     * 本体(0,0,0)には置かない
     *
     * 北:      (0,0,-1)
     * 北西:    (-1,0,-1)
     * 西上段:  (-1,1,-1), (-1,1,0)
     * 南西:    (-1,0,1)
     * 南:      (0,0,1)
     * 南東3段: (1,0,1), (1,1,1), (1,2,1)
     */
    private static final BlockPos[] NORTH_COLLISIONS = new BlockPos[] {
            new BlockPos( 0, 0, -1),// 北
            new BlockPos(-1, 0, -1),

            new BlockPos(-1, 1, -1),
            new BlockPos(-1, 1,  0),

            new BlockPos(-1, 0,  1),
            new BlockPos( 0, 0,  1),

            new BlockPos( 1, 0,  1),
            new BlockPos( 1, 1,  1),
            new BlockPos( 1, 2,  1)
    };

    private static BlockPos rotateOffset(BlockPos offset, Direction facing) {
        return switch (facing) {
            case NORTH -> offset;
            case EAST  -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST  -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default    -> offset;
        };
    }

    public static void placeCollisionBlocks(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof SyokuninDeskBlock)) return;

        Direction facing = state.get(FACING);

        for (BlockPos baseOffset : NORTH_COLLISIONS) {
            BlockPos rotated = rotateOffset(baseOffset, facing);
            BlockPos targetPos = pos.add(rotated);

            if (world.getBlockState(targetPos).isAir()) {
                world.setBlockState(targetPos, ModBlocks.SYOKUNIN_DESK_COLLISION.getDefaultState(), Block.NOTIFY_ALL);
            }
        }
    }

    public static void removeCollisionBlocks(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof SyokuninDeskBlock)) return;

        Direction facing = state.get(FACING);

        for (BlockPos baseOffset : NORTH_COLLISIONS) {
            BlockPos rotated = rotateOffset(baseOffset, facing);
            BlockPos targetPos = pos.add(rotated);

            if (world.getBlockState(targetPos).isOf(ModBlocks.SYOKUNIN_DESK_COLLISION)) {
                world.removeBlock(targetPos, false);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            placeCollisionBlocks(world, pos, state);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (!world.isClient && world.getBlockEntity(pos) instanceof SyokuninDeskBlockEntity be) {
                be.discardYorisyokunin();
            }

            removeCollisionBlocks(world, pos, state);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {

        }

        super.onBreak(world, pos, state, player);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return world.isClient ? null : checkType(
                type,
                ModBlockEntities.SYOKUNIN_DESK,
                SyokuninDeskBlockEntity::tick
        );
    }
}