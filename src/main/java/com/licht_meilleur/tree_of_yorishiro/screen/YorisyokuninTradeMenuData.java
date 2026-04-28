package com.licht_meilleur.tree_of_yorishiro.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record YorisyokuninTradeMenuData(BlockPos pos) {

    public static final StreamCodec<RegistryFriendlyByteBuf, YorisyokuninTradeMenuData> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    YorisyokuninTradeMenuData::pos,
                    YorisyokuninTradeMenuData::new
            );
}