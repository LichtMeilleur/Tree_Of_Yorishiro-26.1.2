package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SyokuninDeskBlock extends BaseEntityBlock {

    public static final MapCodec<SyokuninDeskBlock> CODEC = simpleCodec(SyokuninDeskBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final BlockPos[] NORTH_COLLISIONS = new BlockPos[]{
            new BlockPos(0, 0, -1),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 1, -1),
            new BlockPos(-1, 1, 0),
            new BlockPos(-1, 0, 1),
            new BlockPos(0, 0, 1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 1, 1),
            new BlockPos(1, 2, 1)
    };

    public SyokuninDeskBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SyokuninDeskBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide()) {
            placeCollisionBlocks(level, pos, state);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            removeCollisionBlocks(level, pos, state);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) return null;

        return type == ModBlockEntities.SYOKUNIN_DESK
                ? (lvl, p, s, be) -> SyokuninDeskBlockEntity.tick(lvl, p, s, (SyokuninDeskBlockEntity) be)
                : null;
    }

    private static BlockPos rotateOffset(BlockPos offset, Direction facing) {
        return switch (facing) {
            case NORTH -> offset;
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default -> offset;
        };
    }

    public static void placeCollisionBlocks(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);

        for (BlockPos baseOffset : NORTH_COLLISIONS) {
            BlockPos targetPos = pos.offset(rotateOffset(baseOffset, facing));

            if (level.getBlockState(targetPos).isAir()) {
                level.setBlock(targetPos, ModBlocks.SYOKUNIN_DESK_COLLISION.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static void removeCollisionBlocks(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);

        for (BlockPos baseOffset : NORTH_COLLISIONS) {
            BlockPos targetPos = pos.offset(rotateOffset(baseOffset, facing));

            if (level.getBlockState(targetPos).is(ModBlocks.SYOKUNIN_DESK_COLLISION)) {
                level.removeBlock(targetPos, false);
            }
        }
    }
}