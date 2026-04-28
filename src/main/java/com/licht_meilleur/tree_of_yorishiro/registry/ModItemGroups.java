package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final CreativeModeTab TREE_OF_YORISHIRO_GROUP = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            TreeofYorishiroMod.id("tree_of_yorishiro_group"),
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.tree_of_yorishiro.group"))
                    .icon(() -> new ItemStack(ModItems.YORISHIRO_STONE))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.YORISHIRO_STONE);
                        output.accept(ModItems.RAINBOW_SEED);
                        output.accept(ModItems.TREE_OF_YORISHIRO_ITEM);

                        output.accept(ModItems.YORISYOKUNIN_SUMMON);

                        output.accept(ModItems.STUDY_BOOK);
                        output.accept(ModItems.STUDY_SET);
                        output.accept(ModItems.HARD_STUDY_SET);

                        output.accept(ModItems.HEADBAND);
                        output.accept(ModItems.PUNCHING_SET);
                        output.accept(ModItems.RUNNING_SET);

                        output.accept(ModItems.BALL);
                        output.accept(ModItems.BUBBLE_SET);
                        output.accept(ModItems.GAME);

                        output.accept(ModItems.GLASSES_AND_PEN);
                        output.accept(ModItems.PUNCHING_MACHINE);
                        output.accept(ModItems.RUNNING_MACHINE);
                        output.accept(ModItems.STUDY_DESK);

                        output.accept(ModItems.DEBUG_TREE_OF_YORISHIRO_ITEM);
                    })
                    .build()
    );

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering item groups");
    }
}