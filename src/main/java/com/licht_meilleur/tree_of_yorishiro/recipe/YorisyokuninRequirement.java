package com.licht_meilleur.tree_of_yorishiro.recipe;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.ArrayList;
import java.util.List;

public class YorisyokuninRequirement {

    public enum Type {
        ITEM,
        TAG,
        WATER_BOTTLE
    }

    private final Type type;
    private final List<Item> items;
    private final TagKey<Item> tag;

    private YorisyokuninRequirement(Type type, List<Item> items, TagKey<Item> tag) {
        this.type = type;
        this.items = items;
        this.tag = tag;
    }

    public static YorisyokuninRequirement ofItem(Item... items) {
        List<Item> list = new ArrayList<>();
        for (Item item : items) {
            list.add(item);
        }
        return new YorisyokuninRequirement(Type.ITEM, list, null);
    }

    public static YorisyokuninRequirement ofTag(TagKey<Item> tag) {
        return new YorisyokuninRequirement(Type.TAG, List.of(), tag);
    }

    public static YorisyokuninRequirement waterBottle() {
        return new YorisyokuninRequirement(Type.WATER_BOTTLE, List.of(), null);
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) return false;

        return switch (type) {
            case ITEM -> items.contains(stack.getItem());
            case TAG -> tag != null && stack.is(tag);
            case WATER_BOTTLE -> isWaterBottle(stack);
        };
    }

    public List<ItemStack> getDisplayStacks() {
        List<ItemStack> result = new ArrayList<>();

        switch (type) {
            case ITEM -> {
                for (Item item : items) {
                    result.add(new ItemStack(item));
                }
            }
            case TAG -> {
                if (tag != null) {
                    for (var entry : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                        result.add(new ItemStack(entry.value()));
                    }
                }
            }
            case WATER_BOTTLE -> result.add(makeWaterBottle());
        }

        return result;
    }

    public ItemStack getRotatingDisplayStack(int tick) {
        List<ItemStack> stacks = getDisplayStacks();
        if (stacks.isEmpty()) return ItemStack.EMPTY;
        return stacks.get((tick / 20) % stacks.size());
    }

    private static boolean isWaterBottle(ItemStack stack) {
        PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);
        return stack.is(Items.POTION)
                && potion != null
                && potion.is(Potions.WATER);
    }

    private static ItemStack makeWaterBottle() {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        return stack;
    }
}