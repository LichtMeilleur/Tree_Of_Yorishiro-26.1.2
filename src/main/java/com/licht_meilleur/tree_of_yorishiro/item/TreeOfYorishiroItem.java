package com.licht_meilleur.tree_of_yorishiro.item;

import com.licht_meilleur.tree_of_yorishiro.block.GrowingTreeOfYorishiroBlock;
import com.licht_meilleur.tree_of_yorishiro.block.TreeOfYorishiroPartBlock;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class TreeOfYorishiroItem extends Item {

    public TreeOfYorishiroItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos base = context.getClickedPos().relative(context.getClickedFace());

        if (!level.getBlockState(base).canBeReplaced()
                || !level.getBlockState(base.above()).canBeReplaced()
                || !level.getBlockState(base.above(2)).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        Direction facing = context.getHorizontalDirection().getOpposite();

        level.setBlock(base,
                ModBlocks.GROWING_TREE_OF_YORISHIRO.defaultBlockState()
                        .setValue(GrowingTreeOfYorishiroBlock.FACING, facing),
                Block.UPDATE_ALL
        );

        return InteractionResult.SUCCESS;
    }
}