package com.licht_meilleur.tree_of_yorishiro.client.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRequirement;
import com.licht_meilleur.tree_of_yorishiro.screen.YorisyokuninTradeScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class YorisyokuninTradeScreen extends AbstractContainerScreen<YorisyokuninTradeScreenHandler> {

    private static final Identifier BG = TreeofYorishiroMod.id("textures/gui/yorisyokunin_menu.png");
    private static final Identifier SLOT = TreeofYorishiroMod.id("textures/gui/yorisyokunin_slot.png");

    private int tickCounter = 0;
    private int recipeScroll = 0;

    public YorisyokuninTradeScreen(YorisyokuninTradeScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 256, 256);
        this.inventoryLabelY = 10000;
        this.titleLabelX = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        tickCounter++;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        int x = this.leftPos;
        int y = this.topPos;



        graphics.blit(RenderPipelines.GUI_TEXTURED, BG, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        drawRecipePanel(graphics, mouseX, mouseY);
        drawInputArea(graphics);
        drawOutputArea(graphics);
        drawCraftButton(graphics, mouseX, mouseY);
        drawProgress(graphics);
    }

    private void drawRecipePanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        List<YorisyokuninRecipeDef> recipes = this.menu.getRecipes();
        if (recipes.isEmpty()) return;

        int panelX = this.leftPos + 28;
        int panelY = this.topPos + 22;
        int entryWidth = 92;
        int entryHeight = 20;
        int visibleCount = 6;

        for (int i = 0; i < visibleCount; i++) {
            int recipeIndex = recipeScroll + i;
            if (recipeIndex >= recipes.size()) break;

            YorisyokuninRecipeDef recipe = recipes.get(recipeIndex);

            int ry = panelY + i * entryHeight;
            boolean selected = recipeIndex == this.menu.getSelectedRecipe();
            boolean hovered = isInside(mouseX, mouseY, panelX, ry, entryWidth, 18);

            int fillColor = selected ? 0x90FFF2B2 : (hovered ? 0x70666666 : 0x50444444);
            graphics.fill(panelX, ry, panelX + entryWidth, ry + 18, fillColor);

            graphics.item(recipe.getOutput(), panelX + 2, ry + 1);

            String name = recipe.getOutput().getHoverName().getString();
            if (name.length() > 12) {
                name = name.substring(0, 12);
            }

            graphics.text(this.font, name, panelX + 22, ry + 5, 0xFFFFFF, false);
        }
    }

    private void drawInputArea(GuiGraphicsExtractor graphics) {
        int[] slotXs = {132, 152, 172};
        int slotY = 34;

        for (int i = 0; i < 3; i++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT, this.leftPos + slotXs[i], this.topPos + slotY,
                    0.0F, 0.0F, 18, 18, 18, 18);
        }

        YorisyokuninRecipeDef recipe = this.menu.getSelectedRecipeDef();
        if (recipe == null || this.menu.getBlockEntity() == null) return;

        List<YorisyokuninRequirement> inputs = recipe.getInputs();

        for (int i = 0; i < 3; i++) {
            if (i >= inputs.size()) break;

            ItemStack current = this.menu.getBlockEntity().getInventory().getItem(i);
            if (current.isEmpty()) {
                ItemStack ghost = inputs.get(i).getRotatingDisplayStack(tickCounter);
                if (!ghost.isEmpty()) {
                    int gx = this.leftPos + slotXs[i];
                    int gy = this.topPos + slotY;

                    graphics.item(ghost, gx, gy);
                    graphics.fill(gx, gy, gx + 16, gy + 16, 0x88FFFFFF);
                }
            }
        }
    }

    private void drawOutputArea(GuiGraphicsExtractor graphics) {
        int outputX = 162;
        int outputY = 78;

        graphics.blit(RenderPipelines.GUI_TEXTURED, SLOT, this.leftPos + outputX, this.topPos + outputY,
                0.0F, 0.0F, 18, 18, 18, 18);

        YorisyokuninRecipeDef recipe = this.menu.getSelectedRecipeDef();
        if (recipe == null) return;

        graphics.item(recipe.getOutput(), this.leftPos + outputX, this.topPos + outputY);
    }

    private void drawCraftButton(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int bx = this.leftPos + 150;
        int by = this.topPos + 108;
        int bw = 56;
        int bh = 20;

        boolean hovered = isInside(mouseX, mouseY, bx, by, bw, bh);
        boolean canCraft = this.menu.canCraftSelectedRecipe() && !this.menu.isWorking();

        int fillColor = canCraft
                ? (hovered ? 0xFF7FB3FF : 0xFF5E8FDB)
                : 0xFF888888;
        int borderColor = canCraft ? 0xFF2E5D87 : 0xFF555555;

        graphics.fill(bx, by, bx + bw, by + bh, fillColor);
        graphics.fill(bx, by, bx + bw, by + 1, borderColor);
        graphics.fill(bx, by + bh - 1, bx + bw, by + bh, borderColor);
        graphics.fill(bx, by, bx + 1, by + bh, borderColor);
        graphics.fill(bx + bw - 1, by, bx + bw, by + bh, borderColor);

        Component text = Component.literal("つくる");
        int tx = bx + (bw - this.font.width(text)) / 2;
        graphics.text(this.font, text, tx, by + 6, 0xFFFFFFFF, false);
    }

    private void drawProgress(GuiGraphicsExtractor graphics) {
        if (!this.menu.isWorking()) return;

        int progress = this.menu.getWorkTicks();
        int width = progress * 40 / 120;

        int px = this.leftPos + 150;
        int py = this.topPos + 134;

        graphics.fill(px, py, px + 40, py + 4, 0xFF444444);
        graphics.fill(px, py, px + width, py + 4, 0xFF7FD3FF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();

        List<YorisyokuninRecipeDef> recipes = this.menu.getRecipes();

        int panelX = this.leftPos + 28;
        int panelY = this.topPos + 22;
        int entryWidth = 92;
        int entryHeight = 20;
        int visibleCount = 6;

        for (int i = 0; i < visibleCount; i++) {
            int recipeIndex = recipeScroll + i;
            if (recipeIndex >= recipes.size()) break;

            int ry = panelY + i * entryHeight;

            if (isInside(mouseX, mouseY, panelX, ry, entryWidth, 18)) {
                if (this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(
                            this.menu.containerId,
                            YorisyokuninTradeScreenHandler.getRecipeButtonId(recipeIndex)
                    );
                }
                return true;
            }
        }

        int bx = this.leftPos + 150;
        int by = this.topPos + 108;
        int bw = 56;
        int bh = 20;

        if (isInside(mouseX, mouseY, bx, by, bw, bh)) {
            if (this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(
                        this.menu.containerId,
                        YorisyokuninTradeScreenHandler.getCraftButtonId()
                );
            }
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int visibleCount = 6;
        int maxScroll = Math.max(0, this.menu.getRecipeCount() - visibleCount);

        if (verticalAmount < 0) {
            recipeScroll = Math.min(recipeScroll + 1, maxScroll);
            return true;
        }

        if (verticalAmount > 0) {
            recipeScroll = Math.max(recipeScroll - 1, 0);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}