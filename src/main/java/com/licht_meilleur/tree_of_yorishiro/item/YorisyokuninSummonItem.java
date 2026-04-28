package com.licht_meilleur.tree_of_yorishiro.item;

import com.licht_meilleur.tree_of_yorishiro.block.SyokuninDeskBlock;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class YorisyokuninSummonItem extends Item {

    public YorisyokuninSummonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos placePos = context.getClickedPos().above();
        Player player = context.getPlayer();

        if (!level.getBlockState(placePos).isAir()) {
            return InteractionResult.FAIL;
        }

        BlockState state = ModBlocks.SYOKUNIN_DESK.defaultBlockState();

        if (player != null) {
            state = state.setValue(
                    SyokuninDeskBlock.FACING,
                    player.getDirection().getOpposite()
            );
        }

        level.setBlock(placePos, state, 3);

        if (!level.isClientSide()) {
            SyokuninDeskBlock.placeCollisionBlocks(level, placePos, state);

            if (level.getBlockEntity(placePos) instanceof SyokuninDeskBlockEntity be) {
                // TODO: SyokuninDeskBlockEntity復元後に戻す
                // be.spawnYorisyokunin();
            }
        }

        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}