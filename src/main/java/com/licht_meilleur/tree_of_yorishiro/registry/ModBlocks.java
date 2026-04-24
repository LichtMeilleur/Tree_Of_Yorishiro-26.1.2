package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {

    public static final Block BUD_OF_YORISHIRO = register("bud_of_yorishiro",
            new BudOfYorishiroBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS)
                    .nonOpaque()
                    .ticksRandomly()));

    public static final Block TREE_OF_YORISHIRO = register("tree_of_yorishiro",
            new TreeOfYorishiroBlock(AbstractBlock.Settings.create()
                    .strength(2.0f)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()));

    public static final Block DEBUG_TREE_OF_YORISHIRO = register("debug_tree_of_yorishiro",
            new DebugTreeOfYorishiroBlock(AbstractBlock.Settings.create()
                    .strength(2.0f)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()));

    public static final Block YORISHIRO_STONE = register("yorishiro_stone",
            new Block(AbstractBlock.Settings.create()
                    .strength(0.6f)
                    .sounds(BlockSoundGroup.STONE)
                    .nonOpaque()));

    public static final Block YORISHIRO_TRUNK_COLLISION = registerBlockWithoutItem(
            "yorishiro_trunk_collision",
            new YorishiroTrunkCollisionBlock(FabricBlockSettings.create()
                    .strength(-1.0F, 3600000.0F)
                    .dropsNothing()
            )
    );

    public static final Block SYOKUNIN_DESK_COLLISION = registerBlockWithoutItem(
            "syokunin_desk_collision",
            new SyokuninDeskCollisionBlock(FabricBlockSettings.create()
                    .strength(0.1F)
                    .nonOpaque()
            )
    );

    public static final Block SYOKUNIN_DESK = register("syokunin_desk",
            new SyokuninDeskBlock(AbstractBlock.Settings.create()
                    .strength(2.0f)
                    .nonOpaque()));

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, TreeofYorishiroMod.id(name), block);
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, TreeofYorishiroMod.id(name), block);
    }

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering blocks");
    }
}