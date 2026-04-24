package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DebugTreeOfYorishiroBlock extends TreeOfYorishiroBlock {

    public DebugTreeOfYorishiroBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {

        super.onPlaced(world, pos, state, placer, itemStack);

        if (!(world instanceof ServerWorld sw)) return;
        if (!(sw.getBlockEntity(pos) instanceof TreeOfYorishiroBlockEntity be)) return;

        // ★ デフォルトちび生成
        be.initDefaultChibisIfNeeded();

        // ★ 高ステ化
        be.debugSetAllChibisHighStats();

        // ★ 即召喚（これが重要）
        be.ensureChibishiros();

        be.markDirty();
        world.updateListeners(pos, state, state, 3);
    }
}