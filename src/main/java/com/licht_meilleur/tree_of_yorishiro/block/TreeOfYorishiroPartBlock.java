package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TreeOfYorishiroPartBlock extends BaseEntityBlock {

    public static final MapCodec<TreeOfYorishiroPartBlock> CODEC =
            simpleCodec(properties -> new TreeOfYorishiroPartBlock("tree_of_yorishiro_under", Part.UNDER, properties));

    public enum Part {
        UNDER,
        MIDDLE,
        TOP
    }

    private final Part part;

    public TreeOfYorishiroPartBlock(String id, Part part) {
        this(id, part, BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, TreeofYorishiroMod.id(id)))
                .strength(2.0f)
                .sound(SoundType.WOOD)
                .noOcclusion());
    }

    public TreeOfYorishiroPartBlock(String id, Part part, Properties properties) {
        super(properties);
        this.part = part;
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
        return this.part == Part.UNDER ? new TreeOfYorishiroBlockEntity(pos, state) : null;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) return null;
        if (this.part != Part.UNDER) return null;

        return type == ModBlockEntities.TREE_OF_YORISHIRO
                ? (lvl, blockPos, blockState, blockEntity) ->
                TreeOfYorishiroBlockEntity.tick(
                        lvl,
                        blockPos,
                        blockState,
                        (TreeOfYorishiroBlockEntity) blockEntity
                )
                : null;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos underPos = switch (this.part) {
                case UNDER -> pos;
                case MIDDLE -> pos.below();
                case TOP -> pos.below(2);
            };

            // TODO: TreeOfYorishiroBlockEntity復元後に戻す
            // if (level.getBlockEntity(underPos) instanceof TreeOfYorishiroBlockEntity be) {
            //     be.discardAllChildren();
            // }

            removeTreeParts(level, underPos);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    private static void removeTreeParts(Level level, BlockPos underPos) {
        if (level.getBlockState(underPos).is(ModBlocks.TREE_OF_YORISHIRO_UNDER)) {
            level.setBlock(underPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        if (level.getBlockState(underPos.above()).is(ModBlocks.TREE_OF_YORISHIRO_MIDDLE)) {
            level.setBlock(underPos.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        if (level.getBlockState(underPos.above(2)).is(ModBlocks.TREE_OF_YORISHIRO_TOP)) {
            level.setBlock(underPos.above(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }
}