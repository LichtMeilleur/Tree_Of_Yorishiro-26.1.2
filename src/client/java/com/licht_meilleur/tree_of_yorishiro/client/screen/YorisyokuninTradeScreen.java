package com.licht_meilleur.tree_of_yorishiro.client.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRequirement;
import com.licht_meilleur.tree_of_yorishiro.screen.YorisyokuninTradeScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class YorisyokuninTradeScreen extends HandledScreen<YorisyokuninTradeScreenHandler> {

    private static final Identifier BG = TreeofYorishiroMod.id("textures/gui/yorisyokunin_menu.png");
    private static final Identifier SLOT = TreeofYorishiroMod.id("textures/gui/yorisyokunin_slot.png");

    private int tickCounter = 0;
    private int recipeScroll = 0;

    public YorisyokuninTradeScreen(YorisyokuninTradeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;
        this.playerInventoryTitleY = 10000;
        this.titleX = 10000;
        this.titleY = 10000;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        tickCounter++;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        context.drawTexture(BG, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);

        drawRecipePanel(context, mouseX, mouseY);
        drawInputArea(context);
        drawOutputArea(context);
        drawCraftButton(context, mouseX, mouseY);
        drawProgress(context);
    }

    private void drawRecipePanel(DrawContext context, int mouseX, int mouseY) {
        List<YorisyokuninRecipeDef> recipes = handler.getRecipes();
        if (recipes.isEmpty()) return;

        int panelX = this.x + 28;
        int panelY = this.y + 22;
        int entryWidth = 92;
        int entryHeight = 20;
        int visibleCount = 6;

        for (int i = 0; i < visibleCount; i++) {
            int recipeIndex = recipeScroll + i;
            if (recipeIndex >= recipes.size()) break;

            YorisyokuninRecipeDef recipe = recipes.get(recipeIndex);

            int ry = panelY + i * entryHeight;
            boolean selected = recipeIndex == handler.getSelectedRecipe();
            boolean hovered = isPointWithinBounds(panelX, ry, entryWidth, 18, mouseX, mouseY);

            int fillColor = selected ? 0x90FFF2B2 : (hovered ? 0x70666666 : 0x50444444);
            context.fill(panelX, ry, panelX + entryWidth, ry + 18, fillColor);

            context.drawItem(recipe.getOutput(), panelX + 2, ry + 1);

            String name = recipe.getOutput().getName().getString();
            if (name.length() > 12) {
                name = name.substring(0, 12);
            }

            context.drawText(this.textRenderer, name, panelX + 22, ry + 5, 0xFFFFFF, false);
        }
    }

    private void drawInputArea(DrawContext context) {
        int[] slotXs = {132, 152, 172};
        int slotY = 34;

        for (int i = 0; i < 3; i++) {
            context.drawTexture(SLOT, this.x + slotXs[i], this.y + slotY, 0, 0, 18, 18, 18, 18);
        }

        YorisyokuninRecipeDef recipe = handler.getSelectedRecipeDef();
        if (recipe == null) return;

        List<YorisyokuninRequirement> inputs = recipe.getInputs();
        for (int i = 0; i < 3; i++) {
            if (i >= inputs.size()) break;

            ItemStack current = handler.getBlockEntity().getInventory().getStack(i);
            if (current.isEmpty()) {
                ItemStack ghost = inputs.get(i).getRotatingDisplayStack(tickCounter);
                if (!ghost.isEmpty()) {
                    int gx = this.x + slotXs[i];
                    int gy = this.y + slotY;

                    context.drawItem(ghost, gx, gy);
                    context.fill(gx, gy, gx + 16, gy + 16, 0x88FFFFFF);
                }
            }
        }
    }

    private void drawOutputArea(DrawContext context) {
        int outputX = 162;
        int outputY = 78;

        context.drawTexture(SLOT, this.x + outputX, this.y + outputY, 0, 0, 18, 18, 18, 18);

        YorisyokuninRecipeDef recipe = handler.getSelectedRecipeDef();
        if (recipe == null) return;

        context.drawItem(recipe.getOutput(), this.x + outputX, this.y + outputY);
    }

    private void drawCraftButton(DrawContext context, int mouseX, int mouseY) {
        int bx = this.x + 150;
        int by = this.y + 108;
        int bw = 56;
        int bh = 20;

        boolean hovered = mouseX >= bx && mouseX < bx + bw && mouseY >= by && mouseY < by + bh;
        boolean canCraft = handler.canCraftSelectedRecipe() && !handler.isWorking();

        int fillColor = canCraft
                ? (hovered ? 0xFF7FB3FF : 0xFF5E8FDB)
                : 0xFF888888;
        int borderColor = canCraft ? 0xFF2E5D87 : 0xFF555555;

        context.fill(bx, by, bx + bw, by + bh, fillColor);
        context.drawBorder(bx, by, bw, bh, borderColor);

        Text text = Text.literal("つくる");
        int tx = bx + (bw - this.textRenderer.getWidth(text)) / 2;
        context.drawText(this.textRenderer, text, tx, by + 6, 0xFFFFFFFF, false);
    }

    private void drawProgress(DrawContext context) {
        if (!handler.isWorking()) return;

        int progress = handler.getWorkTicks();
        int width = progress * 40 / 120;

        int px = this.x + 150;
        int py = this.y + 134;

        context.fill(px, py, px + 40, py + 4, 0xFF444444);
        context.fill(px, py, px + width, py + 4, 0xFF7FD3FF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<YorisyokuninRecipeDef> recipes = handler.getRecipes();

        // 描画と同じ判定にする
        int panelX = this.x + 28;
        int panelY = this.y + 22;
        int entryWidth = 92;
        int entryHeight = 20;
        int visibleCount = 6;

        for (int i = 0; i < visibleCount; i++) {
            int recipeIndex = recipeScroll + i;
            if (recipeIndex >= recipes.size()) break;

            int ry = panelY + i * entryHeight;
            if (mouseX >= panelX && mouseX < panelX + entryWidth && mouseY >= ry && mouseY < ry + 18) {
                if (this.client != null && this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(
                            this.handler.syncId,
                            YorisyokuninTradeScreenHandler.getRecipeButtonId(recipeIndex)
                    );
                }
                return true;
            }
        }

        int bx = this.x + 150;
        int by = this.y + 108;
        int bw = 56;
        int bh = 20;

        if (mouseX >= bx && mouseX < bx + bw && mouseY >= by && mouseY < by + bh) {
            if (this.client != null && this.client.interactionManager != null) {
                this.client.interactionManager.clickButton(
                        this.handler.syncId,
                        YorisyokuninTradeScreenHandler.getCraftButtonId()
                );
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int visibleCount = 6;
        int maxScroll = Math.max(0, handler.getRecipeCount() - visibleCount);

        if (amount < 0) {
            recipeScroll = Math.min(recipeScroll + 1, maxScroll);
            return true;
        }
        if (amount > 0) {
            recipeScroll = Math.max(recipeScroll - 1, 0);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        drawRecipeTooltip(context, mouseX, mouseY);
    }

    private void drawRecipeTooltip(DrawContext context, int mouseX, int mouseY) {
        List<YorisyokuninRecipeDef> recipes = handler.getRecipes();

        // ここも描画と同じ判定にする
        int panelX = this.x + 28;
        int panelY = this.y + 22;
        int entryWidth = 92;
        int entryHeight = 20;
        int visibleCount = 6;

        for (int i = 0; i < visibleCount; i++) {
            int recipeIndex = recipeScroll + i;
            if (recipeIndex >= recipes.size()) break;

            int ry = panelY + i * entryHeight;
            if (isPointWithinBounds(panelX, ry, entryWidth, 18, mouseX, mouseY)) {
                context.drawTooltip(this.textRenderer, recipes.get(recipeIndex).getOutput().getName(), mouseX, mouseY);
                return;
            }
        }
    }
}