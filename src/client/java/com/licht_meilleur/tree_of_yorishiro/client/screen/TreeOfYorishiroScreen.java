package com.licht_meilleur.tree_of_yorishiro.client.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeChibishiroData;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.screen.TreeOfYorishiroScreenHandler;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TreeOfYorishiroScreen extends AbstractContainerScreen<TreeOfYorishiroScreenHandler> {

    private static final Identifier RED_UI = TreeofYorishiroMod.id("textures/gui/red_ui.png");
    private static final Identifier BLUE_UI = TreeofYorishiroMod.id("textures/gui/blue_ui.png");
    private static final Identifier YELLOW_UI = TreeofYorishiroMod.id("textures/gui/yellow_ui.png");
    private static final Identifier PURPLE_UI = TreeofYorishiroMod.id("textures/gui/purple_ui.png");
    private static final Identifier WHITE_UI = TreeofYorishiroMod.id("textures/gui/white_ui.png");
    private static final Identifier TREASURE_UI = TreeofYorishiroMod.id("textures/gui/treasure_ui.png");

    private static final Identifier TAB_UI = TreeofYorishiroMod.id("textures/gui/chibishiro_ui_tab.png");
    private static final Identifier BUTTON_UI = TreeofYorishiroMod.id("textures/gui/chibishiro_button.png");

    // 0=白, 1=赤, 2=青, 3=黄, 4=紫, 5=冒険
    private int selectedTab = 0;

    private ChibishiroEntity previewChibi;
    private int previewColorCache = -1;

    private enum DetailPage {
        MAIN,
        MEAL,
        STUDY,
        EXERCISE,
        PLAY,
        ADVENTURE
    }

    private DetailPage detailPage = DetailPage.MAIN;

    public TreeOfYorishiroScreen(TreeOfYorishiroScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 256, 256);
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = this.imageWidth - this.font.width(this.title) - 10;
        this.titleLabelY = this.imageHeight - 230;

        this.selectedTab = 0;
        setDetailPage(DetailPage.MAIN);
        sendSelectedTabColorToServer(this.selectedTab);
    }

    private Identifier getCurrentBackground() {
        return switch (selectedTab) {
            case 1 -> RED_UI;
            case 2 -> BLUE_UI;
            case 3 -> YELLOW_UI;
            case 4 -> PURPLE_UI;
            case 5 -> TREASURE_UI;
            default -> WHITE_UI;
        };
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        int x = this.leftPos;
        int y = this.topPos;

        g.blit(RenderPipelines.GUI_TEXTURED, getCurrentBackground(),
                x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        g.blit(RenderPipelines.GUI_TEXTURED, TAB_UI,
                x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        g.text(this.font, "W", x + 4, y + 5, 0xFFFFFF, true);
        g.text(this.font, "R", x + 28, y + 5, 0xFFFFFF, true);
        g.text(this.font, "B", x + 52, y + 5, 0xFFFFFF, true);
        g.text(this.font, "Y", x + 76, y + 5, 0xFFFFFF, true);
        g.text(this.font, "P", x + 100, y + 5, 0xFFFFFF, true);
        g.text(this.font, "ADV", x + 124, y + 5, 0xFFFFFF, true);

        drawSelectedTabHighlight(g, x, y);

        if (selectedTab != 5 && isTrainingDetailPage()) {
            drawTrainingSlots(g, x, y);
            drawPlayerInventorySlots(g, x, y);
        }

        if (selectedTab != 5) {
            if (detailPage == DetailPage.MAIN) {
                drawSingleChibi3D(g, x, y, mouseX, mouseY);
                drawStatusTexts(g, x, y);
                drawActionButtons(g, x, y);
            } else {
                drawTrainingDetailPage(g, x, y);
            }
        } else {
            drawAdventureArea(g, x, y, mouseX, mouseY);
        }

        if (isTrainingDetailPage()) {
            boolean canStart = this.menu.canStartTraining();

            int fillColor = canStart ? 0xFF6FA8DC : 0xFF888888;
            int borderColor = canStart ? 0xFF2E5D87 : 0xFF555555;

            g.fill(x + 150, y + 145, x + 210, y + 165, fillColor);
            drawBorder(g, x + 150, y + 145, 60, 20, borderColor);
            g.text(this.font, "start", x + 168, y + 151, 0xFFFFFFFF, false);
        }
    }

    private void drawSelectedTabHighlight(GuiGraphicsExtractor g, int x, int y) {
        int tabX;
        int tabWidth;

        switch (selectedTab) {
            case 0 -> {
                tabX = x;
                tabWidth = 24;
            }
            case 1 -> {
                tabX = x + 24;
                tabWidth = 24;
            }
            case 2 -> {
                tabX = x + 48;
                tabWidth = 24;
            }
            case 3 -> {
                tabX = x + 72;
                tabWidth = 24;
            }
            case 4 -> {
                tabX = x + 96;
                tabWidth = 24;
            }
            case 5 -> {
                tabX = x + 120;
                tabWidth = 68;
            }
            default -> {
                tabX = x;
                tabWidth = 24;
            }
        }

        g.fill(tabX, y + 2, tabX + tabWidth, y + 18, 0x80FFFFFF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();

        int x = this.leftPos;
        int y = this.topPos;

        if (isInside(mouseX, mouseY, x, y + 8, 24, 16)) {
            selectedTab = 0;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }

        if (isInside(mouseX, mouseY, x + 24, y + 8, 24, 16)) {
            selectedTab = 1;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }

        if (isInside(mouseX, mouseY, x + 48, y + 8, 24, 16)) {
            selectedTab = 2;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }

        if (isInside(mouseX, mouseY, x + 72, y + 8, 24, 16)) {
            selectedTab = 3;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }

        if (isInside(mouseX, mouseY, x + 96, y + 8, 24, 16)) {
            selectedTab = 4;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }

        if (isInside(mouseX, mouseY, x + 120, y + 8, 68, 16)) {
            selectedTab = 5;
            setDetailPage(DetailPage.ADVENTURE);
            return true;
        }

        int bx = x + 180;
        int by = y + 60;

        if (detailPage == DetailPage.MAIN && selectedTab != 5) {
            if (isInside(mouseX, mouseY, bx, by, 64, 16)) {
                setDetailPage(DetailPage.MEAL);
                return true;
            }
            if (isInside(mouseX, mouseY, bx, by + 24, 64, 16)) {
                setDetailPage(DetailPage.STUDY);
                return true;
            }
            if (isInside(mouseX, mouseY, bx, by + 48, 64, 16)) {
                setDetailPage(DetailPage.EXERCISE);
                return true;
            }
            if (isInside(mouseX, mouseY, bx, by + 72, 64, 16)) {
                setDetailPage(DetailPage.PLAY);
                return true;
            }
        }

        if (isTrainingDetailPage()) {
            if (isPointIn(mouseX, mouseY, x + 150, y + 145, 60, 20)) {
                if (this.menu.canStartTraining()
                        && this.minecraft != null
                        && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(
                            this.menu.getSyncIdForClient(),
                            TreeOfYorishiroScreenHandler.BUTTON_START_TRAINING
                    );
                }
                return true;
            }
        }

        if (detailPage != DetailPage.MAIN && detailPage != DetailPage.ADVENTURE) {
            if (isInside(mouseX, mouseY, x + 18, y + 28, 40, 12)) {
                setDetailPage(DetailPage.MAIN);
                return true;
            }
        }

        if (detailPage == DetailPage.ADVENTURE) {
            if (isPointIn(mouseX, mouseY, x + 150, y + 122, 70, 20)) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(
                            this.menu.getSyncIdForClient(),
                            TreeOfYorishiroScreenHandler.BUTTON_START_ADVENTURE
                    );
                }
                return true;
            }

            if (isPointIn(mouseX, mouseY, x + 180, y + 164, 60, 20)) {
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(
                            this.menu.getSyncIdForClient(),
                            TreeOfYorishiroScreenHandler.BUTTON_CLAIM_ADVENTURE
                    );
                }
                return true;
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    private boolean isPointIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width
                && mouseY >= y && mouseY < y + height;
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w
                && mouseY >= y && mouseY < y + h;
    }

    @Nullable
    private TreeOfYorishiroBlockEntity getTreeBlockEntity() {
        if (this.minecraft.level == null) {
            return null;
        }

        TreeOfYorishiroBlockEntity be = this.menu.getBlockEntity(this.minecraft.level);
        if (be != null) {
            be.initDefaultChibisIfNeeded();
        }

        return be;
    }

    @Nullable
    private TreeChibishiroData getSelectedChibiData() {
        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) {
            System.out.println("[YorishiroUI] BlockEntity is null");
            return null;
        }

        if (selectedTab == 5) {
            return null;
        }

        return be.getChibiDataByColor(getSelectedColor());
    }

    private void drawStatusTexts(GuiGraphicsExtractor g, int x, int y) {
        TreeChibishiroData data = getSelectedChibiData();
        if (data == null) {
            g.text(this.font, "NO DATA", x + 20, y + 90, 0xFF4444, true);
            return;
        }

        int sx = x + 20;
        int sy = y + 110;
        int line = 22;

        g.text(this.font,
                Component.translatable("screen.tree_of_yorishiro.genki").append(" : " + data.getGenki()),
                sx, sy, 0xFFFFFF, true);

        g.text(this.font,
                Component.translatable("screen.tree_of_yorishiro.kashikosa").append(" : " + data.getKashikosa()),
                sx, sy + line, 0xFFFFFF, true);

        g.text(this.font,
                Component.translatable("screen.tree_of_yorishiro.chikara").append(" : " + data.getChikara()),
                sx, sy + line * 2, 0xFFFFFF, true);

        g.text(this.font,
                Component.translatable("screen.tree_of_yorishiro.stress").append(" : " + data.getStress()),
                sx, sy + line * 3, 0xFFFFFF, true);
    }

    private void drawAdventureArea(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY) {
        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) {
            return;
        }

        g.text(this.font,
                Component.translatable("screen.tree_of_yorishiro.adventure_menu"),
                x + 78, y + 22, 0xFFFFFF, true);

        g.text(this.font,
                Component.translatable("screen.tree_of_yorishiro.send_adventure"),
                x + 42, y + 46, 0xFFFFFF, true);

        for (int i = 0; i < 5; i++) {
            int drawX = x + 28 + (i * 42);
            int drawY = y + 108;

            g.fill(drawX - 18, drawY - 38, drawX + 18, drawY + 6, 0x22000000);

            ChibishiroEntity entity = getAdventurePreviewEntity(i);
            if (entity != null) {
                drawAdventureChibi3D(g, drawX, drawY, mouseX, mouseY, entity);
            }
        }

        List<TreeChibishiroData> chibis = be.getChibis();

        boolean allAdventuring = !chibis.isEmpty();
        for (TreeChibishiroData data : chibis) {
            if (data == null || !data.isAdventuring()) {
                allAdventuring = false;
                break;
            }
        }

        if (allAdventuring) {
            Component text = Component.translatable("gui.tree_of_yorishiro.adventuring");
            int centerX = x + this.imageWidth / 2;
            int textWidth = this.font.width(text);

            g.fill(centerX - 45, y + 90, centerX + 45, y + 104, 0x88000000);
            g.text(this.font, text, centerX - textWidth / 2, y + 94, 0xFFFFFF, false);
        }

        boolean canStart = !be.isAnyChibiAdventuring();

        int startX = x + 150;
        int startY = y + 122;
        int startW = 70;
        int startH = 20;

        int fillColor = canStart ? 0xFF6FA8DC : 0xFF888888;
        int borderColor = canStart ? 0xFF2E5D87 : 0xFF555555;

        g.fill(startX, startY, startX + startW, startY + startH, fillColor);
        drawBorder(g, startX, startY, startW, startH, borderColor);

        Component startText = Component.translatable("gui.tree_of_yorishiro.start_adventure");
        int startTextX = startX + (startW - this.font.width(startText)) / 2;
        g.text(this.font, startText, startTextX, startY + 6, 0xFFFFFFFF, false);

        g.text(this.font,
                Component.translatable("gui.tree_of_yorishiro.adventure_rewards"),
                x + 26, y + 152, 0xFFFFFF, false);

        boolean hasRewards = false;
        for (int i = 0; i < be.getAdventureInventory().getContainerSize(); i++) {
            if (!be.getAdventureInventory().getItem(i).isEmpty()) {
                hasRewards = true;
                break;
            }
        }


        int claimX = x + 180;
        int claimY = y + 164;
        int claimW = 60;
        int claimH = 20;

        int claimFill = hasRewards ? 0xFF6FA8DC : 0xFF888888;
        int claimBorder = hasRewards ? 0xFF2E5D87 : 0xFF555555;

        g.fill(claimX, claimY, claimX + claimW, claimY + claimH, claimFill);
        drawBorder(g, claimX, claimY, claimW, claimH, claimBorder);

        Component claimText = Component.translatable("gui.tree_of_yorishiro.claim_rewards");
        int claimTextX = claimX + (claimW - this.font.width(claimText)) / 2;
        g.text(this.font, claimText, claimTextX, claimY + 6, 0xFFFFFFFF, false);
    }

    private void drawAdventureChibi3D(
            GuiGraphicsExtractor g,
            int drawX,
            int drawY,
            int mouseX,
            int mouseY,
            ChibishiroEntity entity
    ) {
        InventoryScreen.extractEntityInInventoryFollowsMouse(
                g,
                drawX - 24,
                drawY - 56,
                drawX + 24,
                drawY + 8,
                20,
                0.0625F,
                mouseX,
                mouseY,
                entity
        );
    }

    @Nullable
    private ChibishiroEntity getAdventurePreviewEntity(int index) {
        if (this.minecraft.level == null) {
            return null;
        }

        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) {
            return null;
        }

        List<TreeChibishiroData> chibis = be.getChibis();
        if (index < 0 || index >= chibis.size()) {
            return null;
        }

        TreeChibishiroData data = chibis.get(index);
        if (data == null || data.getEntityUuid() == null) {
            return null;
        }

        AABB box = new AABB(be.getBlockPos()).inflate(32.0);

        for (ChibishiroEntity entity : this.minecraft.level.getEntitiesOfClass(
                ChibishiroEntity.class,
                box,
                e -> e.getUUID().equals(data.getEntityUuid())
        )) {
            return entity;
        }

        return null;
    }

    private void ensurePreviewChibi() {
        if (this.minecraft.level == null || selectedTab == 5) {
            this.previewChibi = null;
            return;
        }

        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) {
            this.previewChibi = null;
            return;
        }

        TreeChibishiroData data = be.getChibiDataByColor(getSelectedColor());
        if (data == null || data.getEntityUuid() == null) {
            this.previewChibi = null;
            return;
        }

        AABB box = new AABB(be.getBlockPos()).inflate(32.0);

        for (ChibishiroEntity entity : this.minecraft.level.getEntitiesOfClass(
                ChibishiroEntity.class,
                box,
                e -> e.getUUID().equals(data.getEntityUuid())
        )) {
            this.previewChibi = entity;
            return;
        }

        this.previewChibi = null;
    }

    private void drawSingleChibi3D(GuiGraphicsExtractor g, int x, int y, int mouseX, int mouseY) {
        ensurePreviewChibi();
        if (previewChibi == null) return;

        InventoryScreen.extractEntityInInventoryFollowsMouse(
                g,
                x + 35,
                y + 25,
                x + 105,
                y + 100,
                38,
                0.0625F,
                mouseX,
                mouseY,
                previewChibi
        );
    }

    private void drawActionButtons(GuiGraphicsExtractor g, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, BUTTON_UI, x + 180, y + 60, 0, 0, 64, 20, 64, 20);
        g.blit(RenderPipelines.GUI_TEXTURED, BUTTON_UI, x + 180, y + 84, 0, 0, 64, 20, 64, 20);
        g.blit(RenderPipelines.GUI_TEXTURED, BUTTON_UI, x + 180, y + 108, 0, 0, 64, 20, 64, 20);
        g.blit(RenderPipelines.GUI_TEXTURED, BUTTON_UI, x + 180, y + 132, 0, 0, 64, 20, 64, 20);

        g.text(this.font, Component.translatable("screen.tree_of_yorishiro.meal"), x + 200, y + 64, 0x000000, true);
        g.text(this.font, Component.translatable("screen.tree_of_yorishiro.study"), x + 198, y + 88, 0x000000, true);
        g.text(this.font, Component.translatable("screen.tree_of_yorishiro.exercise"), x + 190, y + 112, 0x000000, true);
        g.text(this.font, Component.translatable("screen.tree_of_yorishiro.play"), x + 201, y + 136, 0x000000, true);
    }

    private void drawTrainingDetailPage(GuiGraphicsExtractor g, int x, int y) {
        g.text(this.font, Component.translatable("screen.tree_of_yorishiro.back"), x + 18, y + 28, 0xFFFFFF, true);

        switch (detailPage) {
            case MEAL -> {
                g.text(this.font, Component.translatable("screen.tree_of_yorishiro.meal"), x + 90, y + 40, 0xFFFFFF, true);
                g.text(this.font, Component.translatable("screen.tree_of_yorishiro.food_required"), x + 130, y + 72, 0xFFFFFF, true);
                g.text(this.font, Component.translatable("screen.tree_of_yorishiro.slot_meal"), x + 130, y + 92, 0xFFFFFF, true);
            }
            case STUDY, EXERCISE, PLAY -> {
                g.text(this.font, getDetailTitleText(), x + 90, y + 32, 0xFFFFFF, true);

                g.text(this.font, Component.literal("Lv1"), x + 70, y + 68, 0xFFFFFF, true);
                g.text(this.font, Component.literal("Lv2"), x + 70, y + 98, 0xFFFFFF, true);
                g.text(this.font, Component.literal("Lv3"), x + 70, y + 128, 0xFFFFFF, true);

                g.text(this.font, getRequirementText(1), x + 130, y + 68, 0xFFFFFF, true);
                g.text(this.font, getRequirementText(2), x + 130, y + 98, 0xFFFFFF, true);
                g.text(this.font, getRequirementText(3), x + 130, y + 128, 0xFFFFFF, true);
            }
            default -> {
            }
        }
    }

    private Component getDetailTitleText() {
        return switch (detailPage) {
            case MEAL -> Component.translatable("screen.tree_of_yorishiro.meal");
            case STUDY -> Component.translatable("screen.tree_of_yorishiro.study");
            case EXERCISE -> Component.translatable("screen.tree_of_yorishiro.exercise");
            case PLAY -> Component.translatable("screen.tree_of_yorishiro.play");
            default -> Component.empty();
        };
    }

    private Component getRequirementText(int level) {
        return switch (detailPage) {
            case STUDY -> switch (level) {
                case 1 -> Component.translatable("item.tree_of_yorishiro.study_book");
                case 2 -> Component.translatable("item.tree_of_yorishiro.study_set");
                case 3 -> Component.translatable("item.tree_of_yorishiro.hard_study_set");
                default -> Component.empty();
            };
            case EXERCISE -> switch (level) {
                case 1 -> Component.translatable("item.tree_of_yorishiro.headband");
                case 2 -> Component.translatable("item.tree_of_yorishiro.punching_set");
                case 3 -> Component.translatable("item.tree_of_yorishiro.running_set");
                default -> Component.empty();
            };
            case PLAY -> switch (level) {
                case 1 -> Component.translatable("item.tree_of_yorishiro.ball");
                case 2 -> Component.translatable("item.tree_of_yorishiro.bubble_set");
                case 3 -> Component.translatable("item.tree_of_yorishiro.game");
                default -> Component.empty();
            };
            default -> Component.empty();
        };
    }

    private void setDetailPage(DetailPage page) {
        this.detailPage = page;

        TreeOfYorishiroScreenHandler.DetailPage handlerPage = switch (page) {
            case MAIN -> TreeOfYorishiroScreenHandler.DetailPage.MAIN;
            case MEAL -> TreeOfYorishiroScreenHandler.DetailPage.MEAL;
            case STUDY -> TreeOfYorishiroScreenHandler.DetailPage.STUDY;
            case EXERCISE -> TreeOfYorishiroScreenHandler.DetailPage.EXERCISE;
            case PLAY -> TreeOfYorishiroScreenHandler.DetailPage.PLAY;
            case ADVENTURE -> TreeOfYorishiroScreenHandler.DetailPage.ADVENTURE;
        };

        this.menu.setCurrentPage(handlerPage);

        int buttonId = switch (page) {
            case MAIN -> TreeOfYorishiroScreenHandler.BUTTON_MAIN;
            case MEAL -> TreeOfYorishiroScreenHandler.BUTTON_MEAL;
            case STUDY -> TreeOfYorishiroScreenHandler.BUTTON_STUDY;
            case EXERCISE -> TreeOfYorishiroScreenHandler.BUTTON_EXERCISE;
            case PLAY -> TreeOfYorishiroScreenHandler.BUTTON_PLAY;
            case ADVENTURE -> TreeOfYorishiroScreenHandler.BUTTON_ADVENTURE;
        };

        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.getSyncIdForClient(), buttonId);
        }
    }

    private void drawSlotBox(GuiGraphicsExtractor g, int x, int y) {
        g.fill(x, y, x + 18, y + 18, 0x80FFFFFF);
        g.fill(x, y, x + 18, y + 1, 0xFFAAAAAA);
        g.fill(x, y, x + 1, y + 18, 0xFFAAAAAA);
        g.fill(x + 17, y, x + 18, y + 18, 0xFF555555);
        g.fill(x, y + 17, x + 18, y + 18, 0xFF555555);
    }

    private void drawBorder(GuiGraphicsExtractor g, int x, int y, int width, int height, int color) {
        g.fill(x, y, x + width, y + 1, color);
        g.fill(x, y, x + 1, y + height, color);
        g.fill(x + width - 1, y, x + width, y + height, color);
        g.fill(x, y + height - 1, x + width, y + height, color);
    }

    private boolean isTrainingDetailPage() {
        return detailPage == DetailPage.MEAL
                || detailPage == DetailPage.STUDY
                || detailPage == DetailPage.EXERCISE
                || detailPage == DetailPage.PLAY;
    }

    private void drawTrainingSlots(GuiGraphicsExtractor g, int x, int y) {
        switch (detailPage) {
            case MEAL -> drawSlotBox(g, x + 110, y + 70);
            case STUDY, EXERCISE, PLAY -> {
                drawSlotBox(g, x + 96, y + 66);
                drawSlotBox(g, x + 96, y + 96);
                drawSlotBox(g, x + 96, y + 126);
            }
            default -> {
            }
        }
    }

    private void drawPlayerInventorySlots(GuiGraphicsExtractor g, int x, int y) {
        int startX = 48;
        int startY = 190;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBox(g, x + startX + col * 18, y + startY + row * 18);
            }
        }
    }

    private void sendSelectedTabColorToServer(int tab) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }

        int buttonId = switch (tab) {
            case 1 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_RED;
            case 2 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_BLUE;
            case 3 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_YELLOW;
            case 4 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_PURPLE;
            case 0 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_WHITE;
            default -> -1;
        };

        if (buttonId >= 0) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.getSyncIdForClient(), buttonId);
        }
    }

    private ChibishiroColor getSelectedColor() {
        return switch (selectedTab) {
            case 1 -> ChibishiroColor.RED;
            case 2 -> ChibishiroColor.BLUE;
            case 3 -> ChibishiroColor.YELLOW;
            case 4 -> ChibishiroColor.PURPLE;
            default -> ChibishiroColor.WHITE;
        };
    }
}