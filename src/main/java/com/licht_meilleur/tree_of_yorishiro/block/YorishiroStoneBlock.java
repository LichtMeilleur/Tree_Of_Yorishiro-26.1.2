package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class YorishiroStoneBlock extends Block {

    public YorishiroStoneBlock() {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, TreeofYorishiroMod.id("yorishiro_stone")))
                .mapColor(MapColor.STONE)
                .strength(0.6f, 0.6f)
                .sound(SoundType.STONE)
                .noOcclusion());
    }
}