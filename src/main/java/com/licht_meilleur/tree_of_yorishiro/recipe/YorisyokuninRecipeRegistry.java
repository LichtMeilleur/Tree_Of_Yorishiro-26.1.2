package com.licht_meilleur.tree_of_yorishiro.recipe;

import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

import java.util.ArrayList;
import java.util.List;

public class YorisyokuninRecipeRegistry {

    private static final List<YorisyokuninRecipeDef> RECIPES = new ArrayList<>();

    static {
        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofItem(Items.BOOK)
                ),
                new ItemStack(ModItems.STUDY_BOOK)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofTag(ItemTags.WOOL)
                ),
                new ItemStack(ModItems.HEADBAND)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofItem(Items.SLIME_BALL)
                ),
                new ItemStack(ModItems.BALL)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofItem(Items.SUGAR),
                        YorisyokuninRequirement.waterBottle(),
                        YorisyokuninRequirement.ofItem(Items.SWEET_BERRIES)
                ),
                new ItemStack(ModItems.BUBBLE_SET)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofItem(Items.IRON_INGOT),
                        YorisyokuninRequirement.ofItem(Items.COPPER_INGOT),
                        YorisyokuninRequirement.ofItem(Items.GOLD_INGOT)
                ),
                new ItemStack(ModItems.GAME)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofItem(Items.GLASS),
                        YorisyokuninRequirement.ofItem(Items.CHARCOAL)
                ),
                new ItemStack(ModItems.GLASSES_AND_PEN)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofItem(Items.IRON_INGOT),
                        YorisyokuninRequirement.ofTag(ItemTags.PLANKS)
                ),
                new ItemStack(ModItems.PUNCHING_MACHINE)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofItem(Items.IRON_INGOT),
                        YorisyokuninRequirement.ofItem(Items.COPPER_INGOT),
                        YorisyokuninRequirement.ofItem(Items.DRIED_KELP)
                ),
                new ItemStack(ModItems.RUNNING_MACHINE)
        ));

        RECIPES.add(new YorisyokuninRecipeDef(
                List.of(
                        YorisyokuninRequirement.ofTag(ItemTags.LOGS_THAT_BURN)
                ),
                new ItemStack(ModItems.STUDY_DESK)
        ));
    }

    public static List<YorisyokuninRecipeDef> getRecipes() {
        return RECIPES;
    }

    public static YorisyokuninRecipeDef findMatch(List<ItemStack> stacks) {
        List<ItemStack> filtered = new ArrayList<>();

        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                filtered.add(stack);
            }
        }

        for (YorisyokuninRecipeDef recipe : RECIPES) {
            if (recipe.matches(filtered)) {
                return recipe;
            }
        }

        return null;
    }
}