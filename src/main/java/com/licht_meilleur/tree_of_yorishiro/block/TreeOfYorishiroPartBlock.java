package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroPartBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TreeOfYorishiroPartBlock extends BaseEntityBlock {

    public enum Part {
        UNDER,
        MIDDLE,
        TOP
    }

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public static final MapCodec<TreeOfYorishiroPartBlock> CODEC =
            simpleCodec(properties -> new TreeOfYorishiroPartBlock(
                    "tree_of_yorishiro_part",
                    Part.UNDER,
                    properties
            ));

    private final String name;
    private final Part part;

    public TreeOfYorishiroPartBlock(String name, Part part) {
        this(name, part, BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, TreeofYorishiroMod.id(name)))
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .noOcclusion());
    }

    public TreeOfYorishiroPartBlock(String name, Part part, BlockBehaviour.Properties properties) {
        super(properties);
        this.name = name;
        this.part = part;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
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
        return new TreeOfYorishiroPartBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public Part getPart() {
        return this.part;
    }

    public String getPartName() {
        return this.name;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 16, 16);
    }

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
            {1, 3, 0},
            {2, 3, 0},

            {-3, 7, -3},
            {-3, 7, -1},
            {0, 7, -3},
            {0, 7, -1}
    };

    private static BlockPos rotateOffset(BlockPos origin, int offX, int offY, int offZ, Direction facing) {
        return switch (facing) {
            case SOUTH -> origin.offset(offX, offY, offZ);
            case WEST -> origin.offset(-offZ, offY, offX);
            case NORTH -> origin.offset(-offX, offY, -offZ);
            case EAST -> origin.offset(offZ, offY, -offX);
            default -> origin.offset(offX, offY, offZ);
        };
    }

    public static void placeCollisionBlocks(Level level, BlockPos base, Direction facing) {
        for (int[] offset : COLLISION_OFFSETS) {
            BlockPos target = rotateOffset(base, offset[0], offset[1], offset[2], facing);

            if (level.getBlockState(target).isAir()) {
                level.setBlock(
                        target,
                        ModBlocks.YORISHIRO_TRUNK_COLLISION.defaultBlockState(),
                        Block.UPDATE_ALL
                );
            }
        }
    }

    public static void removeCollisionBlocks(LevelAccessor level, BlockPos base, Direction facing) {
        for (int[] offset : COLLISION_OFFSETS) {
            BlockPos target = rotateOffset(base, offset[0], offset[1], offset[2], facing);

            if (level.getBlockState(target).is(ModBlocks.YORISHIRO_TRUNK_COLLISION)) {
                level.removeBlock(target, false);
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos base = switch (this.part) {
                case UNDER -> pos;
                case MIDDLE -> pos.below();
                case TOP -> pos.below(2);
            };

            Direction facing = state.getValue(FACING);

            BlockEntity be = level.getBlockEntity(base);
            if (be instanceof TreeOfYorishiroPartBlockEntity treeBe) {
                treeBe.removeAllChibishiros();
            }

            removeCollisionBlocks(level, base, facing);

            removeIfTreePartExcept(level, base, pos);
            removeIfTreePartExcept(level, base.above(), pos);
            removeIfTreePartExcept(level, base.above(2), pos);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    private static void removeIfTreePartExcept(LevelAccessor level, BlockPos targetPos, BlockPos breakingPos) {
        if (targetPos.equals(breakingPos)) {
            return;
        }

        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.getBlock() instanceof TreeOfYorishiroPartBlock) {
            level.removeBlock(targetPos, false);
        }
    }


    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (!level.isClientSide() && !player.getAbilities().instabuild) {
            popResource(level, pos, new ItemStack(ModItems.TREE_OF_YORISHIRO_ITEM));
        }
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }

        if (this.part != Part.UNDER) {
            return null;
        }

        return type == ModBlockEntities.TREE_PART
                ? (lvl, pos, blockState, blockEntity) ->
                TreeOfYorishiroPartBlockEntity.tick(
                        lvl,
                        pos,
                        blockState,
                        (TreeOfYorishiroPartBlockEntity) blockEntity
                )
                : null;
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player) {

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // UNDER以外は下に誘導
        BlockPos base = switch (this.part) {
            case UNDER -> pos;
            case MIDDLE -> pos.below();
            case TOP -> pos.below(2);
        };

        BlockEntity be = level.getBlockEntity(base);

        if (be instanceof TreeOfYorishiroPartBlockEntity treeBe) {
            player.openMenu(treeBe);
        }

        return InteractionResult.CONSUME;
    }
}