package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.item.YorisyokuninSummonItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static final Item RAINBOW_SEED = register("rainbow_seed",
            new AliasedBlockItem(ModBlocks.BUD_OF_YORISHIRO, new FabricItemSettings()));

    public static final Item TREE_OF_YORISHIRO_ITEM = register("tree_of_yorishiro",
            new BlockItem(ModBlocks.TREE_OF_YORISHIRO, new FabricItemSettings().maxCount(1)));

    public static final Item YORISHIRO_STONE = register("yorishiro_stone",
            new BlockItem(ModBlocks.YORISHIRO_STONE, new FabricItemSettings()));

    // べんきょう
    public static final Item STUDY_BOOK = register("study_book", new Item(new FabricItemSettings()));
    public static final Item STUDY_SET = register("study_set", new Item(new FabricItemSettings()));
    public static final Item HARD_STUDY_SET = register("hard_study_set", new Item(new FabricItemSettings()));

    // うんどう
    public static final Item HEADBAND = register("headband", new Item(new FabricItemSettings()));
    public static final Item PUNCHING_SET = register("punching_set", new Item(new FabricItemSettings()));
    public static final Item RUNNING_SET = register("running_set", new Item(new FabricItemSettings()));

    // あそび
    public static final Item BALL = register("ball", new Item(new FabricItemSettings()));
    public static final Item BUBBLE_SET = register("bubble_set", new Item(new FabricItemSettings()));
    public static final Item GAME = register("game", new Item(new FabricItemSettings()));

    // 中間素材
    public static final Item GLASSES_AND_PEN = register("glasses_and_pen", new Item(new FabricItemSettings()));
    public static final Item PUNCHING_MACHINE = register("punching_machine", new Item(new FabricItemSettings()));
    public static final Item RUNNING_MACHINE = register("running_machine", new Item(new FabricItemSettings()));
    public static final Item STUDY_DESK = register("study_desk", new Item(new FabricItemSettings()));

    public static final Item YORISYOKUNIN_SUMMON = register("yorisyokunin_item",
            new YorisyokuninSummonItem(new FabricItemSettings().maxCount(16)));

    public static final Item DEBUG_TREE_OF_YORISHIRO_ITEM = register("debug_tree_of_yorishiro",
            new BlockItem(ModBlocks.DEBUG_TREE_OF_YORISHIRO, new FabricItemSettings().maxCount(1)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, TreeofYorishiroMod.id(name), item);
    }

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering items");
    }
}