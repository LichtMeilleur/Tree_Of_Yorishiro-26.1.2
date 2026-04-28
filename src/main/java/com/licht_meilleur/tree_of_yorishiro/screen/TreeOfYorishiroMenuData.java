package com.licht_meilleur.tree_of_yorishiro.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TreeOfYorishiroMenuData(BlockPos pos) {

    public static final StreamCodec<RegistryFriendlyByteBuf, TreeOfYorishiroMenuData> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    TreeOfYorishiroMenuData::pos,
                    TreeOfYorishiroMenuData::new
            );
}