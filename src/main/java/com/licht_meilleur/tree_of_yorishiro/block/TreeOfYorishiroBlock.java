package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TreeOfYorishiroBlock extends BaseEntityBlock {

    public static final MapCodec<TreeOfYorishiroBlock> CODEC =
            simpleCodec(TreeOfYorishiroBlock::new);

    public static final EnumProperty<Direction> FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape BASE_SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public TreeOfYorishiroBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TreeOfYorishiroBlockEntity(pos, state);
    }

    // 🔥 ENTITYBLOCK_ANIMATEDは存在しない
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(serverLevel.getBlockEntity(pos) instanceof TreeOfYorishiroBlockEntity be)) return;

        be.initDefaultChibisIfNeeded();
        be.startGrowAnimation();
        be.setChanged();

        clearNearbyCollisionBlocks(level, pos);
        placeCollisionBlocks(level, pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity raw = level.getBlockEntity(pos);
            if (raw instanceof MenuProvider provider) {
                player.openMenu(provider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) return null;

        return type == ModBlockEntities.TREE_OF_YORISHIRO
                ? (lvl, p, s, be) -> TreeOfYorishiroBlockEntity.tick(lvl, p, s, (TreeOfYorishiroBlockEntity) be)
                : null;
    }

    // 🔥 戻り値ありに修正
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockEntity raw = level.getBlockEntity(pos);

            if (raw instanceof TreeOfYorishiroBlockEntity treeBe) {
                // 一旦コメントアウト（未実装）
                // treeBe.removeAllChibishiro();
            }

            removeCollisionBlocks(level, pos, state);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE_SHAPE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE_SHAPE;
    }

    private BlockPos rotateOffset(BlockPos origin, int offX, int offY, int offZ, Direction facing) {
        return switch (facing) {
            case SOUTH -> origin.offset(offX, offY, offZ);
            case WEST -> origin.offset(-offZ, offY, offX);
            case NORTH -> origin.offset(-offX, offY, -offZ);
            case EAST -> origin.offset(offZ, offY, -offX);
            default -> origin.offset(offX, offY, offZ);
        };
    }

    private void placeCollisionBlocks(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);

        for (int[] offset : collisionOffsets()) {
            BlockPos target = rotateOffset(pos, offset[0], offset[1], offset[2], facing);

            if (level.getBlockState(target).isAir()) {
                level.setBlock(target, ModBlocks.YORISHIRO_TRUNK_COLLISION.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private void removeCollisionBlocks(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);

        for (int[] offset : collisionOffsets()) {
            BlockPos target = rotateOffset(pos, offset[0], offset[1], offset[2], facing);

            if (level.getBlockState(target).is(ModBlocks.YORISHIRO_TRUNK_COLLISION)) {
                level.removeBlock(target, false);
            }
        }
    }

    private void clearNearbyCollisionBlocks(Level level, BlockPos pos) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = 0; dy <= 8; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos target = pos.offset(dx, dy, dz);

                    if (level.getBlockState(target).is(ModBlocks.YORISHIRO_TRUNK_COLLISION)) {
                        level.removeBlock(target, false);
                    }
                }
            }
        }
    }

    private static int[][] collisionOffsets() {
        return new int[][]{
                {0, 1, 0}, {0, 2, 0}, {0, 3, 0}, {0, 4, 0}, {0, 5, 0}, {0, 6, 0},
                {-1, 3, 0}, {-2, 3, 0}, {-2, 5, 0}, {-3, 6, 0},
                {-1, 5, 0}, {1, 5, 0}, {2, 5, 0}, {2, 6, 0},
                {1, 3, 0}, {2, 3, 0},
                {-3, 7, -3}, {-3, 7, -1}, {0, 7, -3}, {0, 7, -1}
        };
    }
}