package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DebugTreeOfYorishiroBlock extends TreeOfYorishiroBlock {

    public DebugTreeOfYorishiroBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!(level instanceof ServerLevel serverLevel)) return;
        if (!(serverLevel.getBlockEntity(pos) instanceof TreeOfYorishiroBlockEntity be)) return;

        be.initDefaultChibisIfNeeded();
        // TODO: TreeOfYorishiroBlockEntity復元後に戻す
        // be.debugSetAllChibisHighStats();
        // be.ensureChibishiros();

        be.setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }
}