package com.licht_meilleur.tree_of_yorishiro.item;

import com.licht_meilleur.tree_of_yorishiro.block.SyokuninDeskBlock;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class YorisyokuninSummonItem extends Item {

    public YorisyokuninSummonItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos placePos = context.getBlockPos().up();
        PlayerEntity player = context.getPlayer();

        if (!world.getBlockState(placePos).isAir()) {
            return ActionResult.FAIL;
        }

        BlockState state = ModBlocks.SYOKUNIN_DESK.getDefaultState();
        if (player != null) {
            state = state.with(SyokuninDeskBlock.FACING, player.getHorizontalFacing().getOpposite());
        }

        world.setBlockState(placePos, state, 3);

        // ここが重要
        if (!world.isClient) {
            SyokuninDeskBlock.placeCollisionBlocks(world, placePos, state);

            if (world.getBlockEntity(placePos) instanceof SyokuninDeskBlockEntity be) {
                be.spawnYorisyokunin();
            }
        }

        if (player != null && !player.getAbilities().creativeMode) {
            context.getStack().decrement(1);
        }


        return ActionResult.success(world.isClient);
    }

}