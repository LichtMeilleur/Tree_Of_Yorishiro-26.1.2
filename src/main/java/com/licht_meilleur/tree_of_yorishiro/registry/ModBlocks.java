package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final Block BUD_OF_YORISHIRO = registerBlockWithoutItem("bud_of_yorishiro",
            new BudOfYorishiroBlock());



    public static final Block GROWING_TREE_OF_YORISHIRO = register("growing_tree_of_yorishiro",
            new GrowingTreeOfYorishiroBlock());

    public static final Block TREE_OF_YORISHIRO_UNDER = register("tree_of_yorishiro_under",
            new TreeOfYorishiroPartBlock("tree_of_yorishiro_under", TreeOfYorishiroPartBlock.Part.UNDER));

    public static final Block TREE_OF_YORISHIRO_MIDDLE = register("tree_of_yorishiro_middle",
            new TreeOfYorishiroPartBlock("tree_of_yorishiro_middle", TreeOfYorishiroPartBlock.Part.MIDDLE));

    public static final Block TREE_OF_YORISHIRO_TOP = register("tree_of_yorishiro_top",
            new TreeOfYorishiroPartBlock("tree_of_yorishiro_top", TreeOfYorishiroPartBlock.Part.TOP));

    public static final Block DEBUG_TREE_OF_YORISHIRO = register("debug_tree_of_yorishiro",
            new DebugTreeOfYorishiroBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, TreeofYorishiroMod.id("debug_tree_of_yorishiro")))
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final Block YORISHIRO_STONE = register("yorishiro_stone",
            new YorishiroStoneBlock());

    public static final Block YORISHIRO_TRUNK_COLLISION = registerBlockWithoutItem(
            "yorishiro_trunk_collision",
            new YorishiroTrunkCollisionBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, TreeofYorishiroMod.id("yorishiro_trunk_collision")))
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
            )
    );

    public static final Block SYOKUNIN_DESK_COLLISION = registerBlockWithoutItem(
            "syokunin_desk_collision",
            new SyokuninDeskCollisionBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, TreeofYorishiroMod.id("syokunin_desk_collision")))
                    .strength(0.1F)
                    .noOcclusion()
            )
    );

    public static final Block SYOKUNIN_DESK = register("syokunin_desk",
            new SyokuninDeskBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, TreeofYorishiroMod.id("syokunin_desk")))
                    .strength(2.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    private static Block register(String name, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, TreeofYorishiroMod.id(name), block);

        Registry.register(
                BuiltInRegistries.ITEM,
                TreeofYorishiroMod.id(name),
                new BlockItem(
                        block,
                        new Item.Properties()
                                .setId(ResourceKey.create(Registries.ITEM, TreeofYorishiroMod.id(name)))
                )
        );

        return block;
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, TreeofYorishiroMod.id(name), block);
    }

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering blocks");
    }
}