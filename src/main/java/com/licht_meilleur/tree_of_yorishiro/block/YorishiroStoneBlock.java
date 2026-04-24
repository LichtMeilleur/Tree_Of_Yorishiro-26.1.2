package com.licht_meilleur.tree_of_yorishiro.block;

import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;

public class YorishiroStoneBlock extends Block {

    public YorishiroStoneBlock() {
        super(AbstractBlock.Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .strength(0.6f, 0.6f)
                .sounds(BlockSoundGroup.STONE)
                .nonOpaque());
    }
}