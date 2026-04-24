package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ModItemGroups {

    public static final ItemGroup TREE_OF_YORISHIRO_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            TreeofYorishiroMod.id("tree_of_yorishiro_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.tree_of_yorishiro.group"))
                    .icon(() -> new ItemStack(ModItems.YORISHIRO_STONE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.YORISHIRO_STONE);
                        entries.add(ModItems.RAINBOW_SEED);
                        entries.add(ModItems.TREE_OF_YORISHIRO_ITEM);
                        entries.add(ModItems.YORISYOKUNIN_SUMMON);

                        entries.add(ModItems.STUDY_BOOK);
                        entries.add(ModItems.STUDY_SET);
                        entries.add(ModItems.HARD_STUDY_SET);

                        entries.add(ModItems.HEADBAND);
                        entries.add(ModItems.PUNCHING_SET);
                        entries.add(ModItems.RUNNING_SET);

                        entries.add(ModItems.BALL);
                        entries.add(ModItems.BUBBLE_SET);
                        entries.add(ModItems.GAME);

                        entries.add(ModItems.GLASSES_AND_PEN);
                        entries.add(ModItems.PUNCHING_MACHINE);
                        entries.add(ModItems.RUNNING_MACHINE);
                        entries.add(ModItems.STUDY_DESK);

                        entries.add(ModItems.DEBUG_TREE_OF_YORISHIRO_ITEM);
                    })
                    .build()
    );

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering item groups");
    }
}