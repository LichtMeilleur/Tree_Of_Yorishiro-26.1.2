package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class YorisyokuninTradeScreenHandler extends AbstractContainerMenu {

    private static final int CRAFT_BUTTON_ID = 999;

    private final SyokuninDeskBlockEntity be;
    private final Container inventory;
    private final BlockPos pos;

    private int selectedRecipe = 0;

    public YorisyokuninTradeScreenHandler(int syncId, Inventory playerInventory, SyokuninDeskBlockEntity be, BlockPos pos) {
        super(ModScreenHandlers.YORISYOKUNIN_TRADE, syncId);

        this.be = be;
        this.pos = pos;
        this.inventory = be != null ? be.getInventory() : new SimpleContainer(3);

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return selectedRecipe;
            }

            @Override
            public void set(int value) {
                selectedRecipe = value;
            }
        });

        addSlots(playerInventory);
    }

    private void addSlots(Inventory playerInventory) {
        this.addSlot(new Slot(inventory, 0, 132, 34));
        this.addSlot(new Slot(inventory, 1, 152, 34));
        this.addSlot(new Slot(inventory, 2, 172, 34));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 48 + col * 18, 156 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 48 + col * 18, 214));
        }
    }

    public static int getRecipeButtonId(int recipeIndex) {
        return recipeIndex;
    }

    public static int getCraftButtonId() {
        return CRAFT_BUTTON_ID;
    }

    public List<YorisyokuninRecipeDef> getRecipes() {
        return YorisyokuninRecipeRegistry.getRecipes();
    }

    public int getRecipeCount() {
        return YorisyokuninRecipeRegistry.getRecipes().size();
    }

    public int getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(int selectedRecipe) {
        int max = Math.max(0, getRecipeCount() - 1);
        this.selectedRecipe = Math.max(0, Math.min(selectedRecipe, max));
    }

    public YorisyokuninRecipeDef getSelectedRecipeDef() {
        List<YorisyokuninRecipeDef> recipes = getRecipes();
        if (recipes.isEmpty()) return null;

        int index = getSelectedRecipe();
        if (index < 0 || index >= recipes.size()) {
            index = 0;
            setSelectedRecipe(0);
        }

        return recipes.get(index);
    }

    public boolean canCraftSelectedRecipe() {
        if (be == null) return false;

        YorisyokuninRecipeDef recipe = getSelectedRecipeDef();
        if (recipe == null) return false;

        List<ItemStack> inputs = List.of(
                inventory.getItem(0),
                inventory.getItem(1),
                inventory.getItem(2)
        );

        return recipe.matches(inputs);
    }

    public boolean startWork() {
        if (be == null) return false;

        YorisyokuninRecipeDef recipe = getSelectedRecipeDef();
        if (recipe == null) return false;
        if (!canCraftSelectedRecipe()) return false;
        if (be.isWorking()) return false;

        be.tryStartWork(recipe);
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == CRAFT_BUTTON_ID) {
            return startWork();
        }

        if (id >= 0 && id < getRecipeCount()) {
            setSelectedRecipe(id);
            broadcastChanges();
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    public boolean isWorking() {
        return be != null && be.isWorking();
    }

    public int getWorkTicks() {
        return be != null ? be.getWorkTicks() : 0;
    }

    public SyokuninDeskBlockEntity getBlockEntity() {
        return be;
    }

    @Override
    public boolean stillValid(Player player) {
        if (be == null) return false;

        return player.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }
}