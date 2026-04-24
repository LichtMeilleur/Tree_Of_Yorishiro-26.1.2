package com.licht_meilleur.tree_of_yorishiro.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

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
            case TAG -> stack.isIn(tag);
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
                for (var entry : Registries.ITEM.iterateEntries(tag)) {
                    result.add(new ItemStack(entry.value()));
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
        return PotionUtil.getPotion(stack) == Potions.WATER;
    }

    private static ItemStack makeWaterBottle() {
        return PotionUtil.setPotion(new ItemStack(net.minecraft.item.Items.POTION), Potions.WATER);
    }
}