package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.item.TreeOfYorishiroItem;
import com.licht_meilleur.tree_of_yorishiro.item.YorisyokuninSummonItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;

public class ModItems {

    public static final Item RAINBOW_SEED = register("rainbow_seed",
            new BlockItem(ModBlocks.BUD_OF_YORISHIRO, props("rainbow_seed")));

    public static final Item YORISHIRO_STONE = register("yorishiro_stone_item",
            new BlockItem(ModBlocks.YORISHIRO_STONE, props("yorishiro_stone_item")));

    // べんきょう
    public static final Item STUDY_BOOK = register("study_book", new Item(props("study_book")));
    public static final Item STUDY_SET = register("study_set", new Item(props("study_set")));
    public static final Item HARD_STUDY_SET = register("hard_study_set", new Item(props("hard_study_set")));

    // うんどう
    public static final Item HEADBAND = register("headband", new Item(props("headband")));
    public static final Item PUNCHING_SET = register("punching_set", new Item(props("punching_set")));
    public static final Item RUNNING_SET = register("running_set", new Item(props("running_set")));

    // あそび
    public static final Item BALL = register("ball", new Item(props("ball")));
    public static final Item BUBBLE_SET = register("bubble_set", new Item(props("bubble_set")));
    public static final Item GAME = register("game", new Item(props("game")));

    // 中間素材
    public static final Item GLASSES_AND_PEN = register("glasses_and_pen", new Item(props("glasses_and_pen")));
    public static final Item PUNCHING_MACHINE = register("punching_machine", new Item(props("punching_machine")));
    public static final Item RUNNING_MACHINE = register("running_machine", new Item(props("running_machine")));
    public static final Item STUDY_DESK = register("study_desk", new Item(props("study_desk")));

    public static final Item YORISYOKUNIN_SUMMON = register("yorisyokunin_item",
            new YorisyokuninSummonItem(props("yorisyokunin_item").stacksTo(16)));

    public static final Item TREE_OF_YORISHIRO_ITEM = register("tree_of_yorishiro_item",
            new TreeOfYorishiroItem(props("tree_of_yorishiro_item").stacksTo(1)));


    public static final Item DEBUG_TREE_OF_YORISHIRO_ITEM = register("debug_tree_of_yorishiro_item",
            new BlockItem(ModBlocks.DEBUG_TREE_OF_YORISHIRO, props("debug_tree_of_yorishiro_item").stacksTo(1)));

    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, TreeofYorishiroMod.id(name)));
    }

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, TreeofYorishiroMod.id(name), item);
    }

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering items");
    }
}