package com.licht_meilleur.tree_of_yorishiro.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

public class YorisyokuninRecipeDef {

    private final List<YorisyokuninRequirement> inputs;
    private final ItemStack output;

    public YorisyokuninRecipeDef(List<YorisyokuninRequirement> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public List<YorisyokuninRequirement> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public boolean matches(List<ItemStack> stacks) {
        int nonEmptyCount = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                nonEmptyCount++;
            }
        }

        if (nonEmptyCount != inputs.size()) return false;

        boolean[] used = new boolean[stacks.size()];

        for (YorisyokuninRequirement requirement : inputs) {
            boolean matched = false;

            for (int i = 0; i < stacks.size(); i++) {
                if (used[i]) continue;

                ItemStack stack = stacks.get(i);
                if (stack.isEmpty()) continue;

                if (requirement.matches(stack)) {
                    used[i] = true;
                    matched = true;
                    break;
                }
            }

            if (!matched) return false;
        }

        return true;
    }
}