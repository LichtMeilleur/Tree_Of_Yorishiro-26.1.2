package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SyokuninDeskCollisionBlock extends Block {

    public SyokuninDeskCollisionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }


    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos deskPos = findNearbyDesk(level, pos);

            if (deskPos != null && level.getBlockState(deskPos).is(ModBlocks.SYOKUNIN_DESK)) {
                Block.popResource(level, deskPos, new ItemStack(ModItems.YORISYOKUNIN_SUMMON));
                level.removeBlock(deskPos, false);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    private BlockPos findNearbyDesk(Level level, BlockPos pos) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);

                    if (level.getBlockState(check).is(ModBlocks.SYOKUNIN_DESK)) {
                        return check;
                    }
                }
            }
        }

        return null;
    }
}