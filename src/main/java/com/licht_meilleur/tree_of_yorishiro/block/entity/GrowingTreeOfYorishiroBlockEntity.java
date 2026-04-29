package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.licht_meilleur.tree_of_yorishiro.block.GrowingTreeOfYorishiroBlock;
import com.licht_meilleur.tree_of_yorishiro.block.TreeOfYorishiroPartBlock;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GrowingTreeOfYorishiroBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final int GROW_TICKS_MAX = 30;

    private static final RawAnimation GROW =
            RawAnimation.begin().thenPlay("animation.model.grow");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int growTicks = GROW_TICKS_MAX;

    public GrowingTreeOfYorishiroBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWING_TREE_OF_YORISHIRO, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrowingTreeOfYorishiroBlockEntity be) {
        if (level.isClientSide()) return;

        be.growTicks--;

        if (be.growTicks <= 0) {
            replaceWithCompletedTree(level, pos, state);
        }
    }

    private static void replaceWithCompletedTree(Level level, BlockPos pos, BlockState growingState) {
        if (!level.getBlockState(pos.above()).canBeReplaced()) return;
        if (!level.getBlockState(pos.above(2)).canBeReplaced()) return;

        Direction facing = growingState.getValue(GrowingTreeOfYorishiroBlock.FACING);

        level.setBlock(pos,
                ModBlocks.TREE_OF_YORISHIRO_UNDER.defaultBlockState()
                        .setValue(TreeOfYorishiroPartBlock.FACING, facing),
                Block.UPDATE_ALL);

        level.setBlock(pos.above(),
                ModBlocks.TREE_OF_YORISHIRO_MIDDLE.defaultBlockState()
                        .setValue(TreeOfYorishiroPartBlock.FACING, facing),
                Block.UPDATE_ALL);

        level.setBlock(pos.above(2),
                ModBlocks.TREE_OF_YORISHIRO_TOP.defaultBlockState()
                        .setValue(TreeOfYorishiroPartBlock.FACING, facing),
                Block.UPDATE_ALL);

        TreeOfYorishiroPartBlock.placeCollisionBlocks(level, pos, facing);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                "main",
                0,
                state -> {
                    state.setAnimation(GROW);
                    return PlayState.CONTINUE;
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}