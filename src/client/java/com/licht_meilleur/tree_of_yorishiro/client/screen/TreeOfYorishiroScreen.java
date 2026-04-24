package com.licht_meilleur.tree_of_yorishiro.client.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.screen.TreeOfYorishiroScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeChibishiroData;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import org.jetbrains.annotations.Nullable;

public class TreeOfYorishiroScreen extends HandledScreen<TreeOfYorishiroScreenHandler> {

    private static final Identifier RED_UI = TreeofYorishiroMod.id("textures/gui/red_ui.png");
    private static final Identifier BLUE_UI = TreeofYorishiroMod.id("textures/gui/blue_ui.png");
    private static final Identifier YELLOW_UI = TreeofYorishiroMod.id("textures/gui/yellow_ui.png");
    private static final Identifier PURPLE_UI = TreeofYorishiroMod.id("textures/gui/purple_ui.png");
    private static final Identifier WHITE_UI = TreeofYorishiroMod.id("textures/gui/white_ui.png");
    private static final Identifier TREASURE_UI = TreeofYorishiroMod.id("textures/gui/treasure_ui.png");

    private static final Identifier TAB_UI = TreeofYorishiroMod.id("textures/gui/chibishiro_ui_tab.png");
    private static final Identifier BUTTON_UI = TreeofYorishiroMod.id("textures/gui/chibishiro_button.png");

    private static final Identifier WHITE_TEX = TreeofYorishiroMod.id("textures/entity/white.png");
    private static final Identifier RED_TEX = TreeofYorishiroMod.id("textures/entity/red.png");
    private static final Identifier BLUE_TEX = TreeofYorishiroMod.id("textures/entity/blue.png");
    private static final Identifier YELLOW_TEX = TreeofYorishiroMod.id("textures/entity/yellow.png");
    private static final Identifier PURPLE_TEX = TreeofYorishiroMod.id("textures/entity/purple.png");

    // 0=白, 1=赤, 2=青, 3=黄, 4=紫, 5=冒険
    private int selectedTab = 0;


    private com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity previewChibi;
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

    public TreeOfYorishiroScreen(TreeOfYorishiroScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;
        this.playerInventoryTitleY = 10000;
    }

    @Override
    protected void init() {
        super.init();

        this.titleX = this.backgroundWidth - this.textRenderer.getWidth(this.title) - 10;
        this.titleY = this.backgroundHeight - 230;

        // UIのデフォルト表示と実際の選択色を一致させる
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
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 背景
        context.drawTexture(getCurrentBackground(), x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        // タブ
        context.drawTexture(TAB_UI, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        // タブラベル
        context.drawText(this.textRenderer, "W",   x + 0,  y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "R",   x + 24,  y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "B",   x + 48,  y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "Y",   x + 72, y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "P",   x + 96, y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "ADV", x + 120, y + 5, 0xFFFFFF, true);

        // 選択中タブのハイライト
        drawSelectedTabHighlight(context, x, y);


        if (selectedTab != 5 && isTrainingDetailPage()) {
            drawTrainingSlots(context, x, y);
            drawPlayerInventorySlots(context, x, y);
        }

        if (selectedTab != 5) {
            if (detailPage == DetailPage.MAIN) {
                drawSingleChibi3D(context, x, y);
                drawStatusTexts(context, x, y);
                drawActionButtons(context, x, y);
            } else {
                drawTrainingDetailPage(context, x, y);
            }
        } else {
            drawAdventureArea(context, x, y);
        }
        if (isTrainingDetailPage()) {
            boolean canStart = this.handler.canStartTraining();

            int fillColor = canStart ? 0xFF6FA8DC : 0xFF888888;
            int borderColor = canStart ? 0xFF2E5D87 : 0xFF555555;

            context.fill(x + 150, y + 145, x + 210, y + 165, fillColor);
            context.drawBorder(x + 150, y + 145, 60, 20, borderColor);
            context.drawText(this.textRenderer, "start", x + 168, y + 151, 0xFFFFFFFF, false);
        }
    }

    private void drawSelectedTabHighlight(DrawContext context, int x, int y) {
        int tabX;
        int tabWidth;

        switch (selectedTab) {
            case 0 -> { tabX = x + 0;  tabWidth = 24; } // 白
            case 1 -> { tabX = x + 24;  tabWidth = 24; } // 赤
            case 2 -> { tabX = x + 48;  tabWidth = 24; } // 青
            case 3 -> { tabX = x + 72; tabWidth = 24; } // 黄
            case 4 -> { tabX = x + 96; tabWidth = 24; } // 紫
            case 5 -> { tabX = x + 120; tabWidth = 68; } // 冒険
            default -> { tabX = x + 0; tabWidth = 24; }
        }

        int tabY = y + 2;
        int tabHeight = 16;

        // 半透明白
        context.fill(tabX, tabY, tabX + tabWidth, tabY + tabHeight, 0x80FFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // 白
        if (isInside(mouseX, mouseY, x + 0, y + 8, 24, 16)) {
            selectedTab = 0;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }
        // 赤
        if (isInside(mouseX, mouseY, x + 24, y + 8, 24, 16)) {
            selectedTab = 1;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }
        // 青
        if (isInside(mouseX, mouseY, x + 48, y + 8, 24, 16)) {
            selectedTab = 2;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }
        // 黄
        if (isInside(mouseX, mouseY, x + 72, y + 8, 24, 16)) {
            selectedTab = 3;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }
        // 紫
        if (isInside(mouseX, mouseY, x + 96, y + 8, 24, 16)) {
            selectedTab = 4;
            setDetailPage(DetailPage.MAIN);
            sendSelectedTabColorToServer(selectedTab);
            return true;
        }
        // 冒険
        if (isInside(mouseX, mouseY, x + 120, y + 8, 68, 16)) {
            selectedTab = 5;
            setDetailPage(DetailPage.ADVENTURE);
            return true;
        }

        int bx = x + 180;
        int by = y + 60;

        // 通常画面のときだけ
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

        // startボタン（育成ページのときだけ）
        if (isTrainingDetailPage()) {
            if (isPointIn(mouseX, mouseY, x + 150, y + 145, 60, 20)) {
                if (this.handler.canStartTraining()
                        && this.client != null
                        && this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(
                            this.handler.getSyncIdForClient(),
                            TreeOfYorishiroScreenHandler.BUTTON_START_TRAINING
                    );
                }
                return true;
            }
        }

        // もどる
        if (detailPage != DetailPage.MAIN && detailPage != DetailPage.ADVENTURE) {
            if (isInside(mouseX, mouseY, x + 18, y + 28, 40, 12)) {
                setDetailPage(DetailPage.MAIN);
                return true;
            }
        }
        //冒険開始ボタン
        if (detailPage == DetailPage.ADVENTURE) {
            if (isPointIn(mouseX, mouseY, x + 150, y + 122, 70, 20)) {
                if (this.client != null && this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(
                            this.handler.getSyncIdForClient(),
                            TreeOfYorishiroScreenHandler.BUTTON_START_ADVENTURE
                    );
                }
                return true;
            }

            if (isPointIn(mouseX, mouseY, x + 180, y + 164, 60, 20)) {
                if (this.client != null && this.client.interactionManager != null) {
                    this.client.interactionManager.clickButton(
                            this.handler.getSyncIdForClient(),
                            TreeOfYorishiroScreenHandler.BUTTON_CLAIM_ADVENTURE
                    );
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isPointIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width
                && mouseY >= y && mouseY < y + height;
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
    private Identifier getTextureForColor(ChibishiroColor color) {
        return switch (color) {
            case RED -> RED_TEX;
            case BLUE -> BLUE_TEX;
            case YELLOW -> YELLOW_TEX;
            case PURPLE -> PURPLE_TEX;
            default -> WHITE_TEX;
        };
    }

    private TreeOfYorishiroBlockEntity getTreeBlockEntity() {
        if (client == null || client.world == null) return null;

        TreeOfYorishiroBlockEntity be = handler.getBlockEntity(client.world);
        if (be != null && !be.isInitialized()) {
            be.initDefaultChibisIfNeeded();
        }
        return be;
    }

    private TreeChibishiroData getSelectedChibiData() {
        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) {
            System.out.println("[YorishiroUI] BlockEntity is null");
            return null;
        }

        if (selectedTab < 0 || selectedTab >= 5) {
            System.out.println("[YorishiroUI] selectedTab out of range: " + selectedTab);
            return null;
        }

        TreeChibishiroData data = be.getChibi(selectedTab);
        if (data == null) {
            System.out.println("[YorishiroUI] Chibi data is null at index " + selectedTab);
        }

        return data;
    }


    private void drawStatusTexts(DrawContext context, int x, int y) {
        TreeChibishiroData data = getSelectedChibiData();
        if (data == null) {
            context.drawText(this.textRenderer, "NO DATA", x + 20, y + 90, 0xFF4444, true);
            return;
        }

        int sx = x + 20;
        int sy = y + 110;
        int line = 22;

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.genki").append(" : " + data.getGenki()),
                sx, sy, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.kashikosa").append(" : " + data.getKashikosa()),
                sx, sy + line, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.chikara").append(" : " + data.getChikara()),
                sx, sy + line * 2, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.stress").append(" : " + data.getStress()),
                sx, sy + line * 3, 0xFFFFFF, true);
    }

    private void drawAdventureArea(DrawContext context, int x, int y) {
        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) return;

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.adventure_menu"),
                x + 78, y + 22, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.send_adventure"),
                x + 42, y + 46, 0xFFFFFF, true);

        // 5人表示
        for (int i = 0; i < 5; i++) {
            int drawX = x + 28 + (i * 42);
            int drawY = y + 108;

            context.fill(drawX - 18, drawY - 38, drawX + 18, drawY + 6, 0x22000000);

            ChibishiroEntity entity = getAdventurePreviewEntity(i);
            if (entity != null) {
                net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity(
                        context,
                        drawX,
                        drawY,
                        20,
                        0,
                        0,
                        entity
                );
            }
        }

        // 5人全員が冒険中なら中央に1つだけ表示
        boolean allAdventuring = true;
        for (int i = 0; i < 5; i++) {
            TreeChibishiroData data = be.getChibi(i);
            if (data == null || !data.isAdventuring()) {
                allAdventuring = false;
                break;
            }
        }

        if (allAdventuring) {
            Text text = Text.translatable("gui.tree_of_yorishiro.adventuring");
            int centerX = x + this.backgroundWidth / 2;
            int textWidth = this.textRenderer.getWidth(text);

            context.fill(centerX - 45, y + 90, centerX + 45, y + 104, 0x88000000);
            context.drawText(this.textRenderer, text, centerX - textWidth / 2, y + 94, 0xFFFFFF, false);
        }

        boolean canStart = be.canStartAdventure();

        int startX = x + 150;
        int startY = y + 122;
        int startW = 70;
        int startH = 20;

        int fillColor = canStart ? 0xFF6FA8DC : 0xFF888888;
        int borderColor = canStart ? 0xFF2E5D87 : 0xFF555555;

        context.fill(startX, startY, startX + startW, startY + startH, fillColor);
        context.drawBorder(startX, startY, startW, startH, borderColor);

        Text startText = Text.translatable("gui.tree_of_yorishiro.start_adventure");
        int startTextX = startX + (startW - this.textRenderer.getWidth(startText)) / 2;
        context.drawText(this.textRenderer, startText, startTextX, startY + 6, 0xFFFFFFFF, false);

        // 成果物ラベル
        context.drawText(this.textRenderer,
                Text.translatable("gui.tree_of_yorishiro.adventure_rewards"),
                x + 26, y + 152, 0xFFFFFF, false);

        // 受け取りボタン
        boolean hasRewards = be.hasAdventureRewards();

        int claimX = x + 180;
        int claimY = y + 164;
        int claimW = 60;
        int claimH = 20;

        int claimFill = hasRewards ? 0xFF6FA8DC : 0xFF888888;
        int claimBorder = hasRewards ? 0xFF2E5D87 : 0xFF555555;

        context.fill(claimX, claimY, claimX + claimW, claimY + claimH, claimFill);
        context.drawBorder(claimX, claimY, claimW, claimH, claimBorder);

        Text claimText = Text.translatable("gui.tree_of_yorishiro.claim_rewards");
        int claimTextX = claimX + (claimW - this.textRenderer.getWidth(claimText)) / 2;
        context.drawText(this.textRenderer, claimText, claimTextX, claimY + 6, 0xFFFFFFFF, false);
    }

    @Nullable
    private ChibishiroEntity getAdventurePreviewEntity(int index) {
        if (this.client == null || this.client.world == null) return null;

        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) return null;

        TreeChibishiroData data = be.getChibi(index);
        if (data == null || data.getEntityUuid() == null) return null;

        for (ChibishiroEntity entity : this.client.world.getEntitiesByClass(
                ChibishiroEntity.class,
                new net.minecraft.util.math.Box(be.getPos()).expand(32.0),
                e -> e.getUuid().equals(data.getEntityUuid())
        )) {
            return entity;
        }

        return null;
    }
    private void ensurePreviewChibi() {
        if (this.client == null || this.client.world == null) return;
        if (selectedTab < 0 || selectedTab > 4) return;

        TreeOfYorishiroBlockEntity be = handler.getBlockEntity(this.client.world);
        if (be == null) return;

        TreeChibishiroData data = getSelectedChibiData();
        if (data == null || data.getEntityUuid() == null) return;

        com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity found = null;

        for (com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity entity :
                this.client.world.getEntitiesByClass(
                        com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity.class,
                        new net.minecraft.util.math.Box(be.getPos()).expand(32.0),
                        e -> e.getUuid().equals(data.getEntityUuid())
                )) {
            found = entity;
            break;
        }

        previewChibi = found;
        previewColorCache = data.getColor().ordinal();
    }
    private void drawSingleChibi3D(DrawContext context, int x, int y) {
        ensurePreviewChibi();
        if (previewChibi == null) return;

        int drawX = x + 70;
        int drawY = y + 92;
        int scale = 38;

        net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity(
                context,
                drawX,
                drawY,
                scale,
                0,
                0,
                previewChibi
        );
    }
    private void drawActionButtons(DrawContext context, int x, int y) {
        context.drawTexture(BUTTON_UI, x + 180, y + 60,  0, 0, 64, 20, 64, 20);
        context.drawTexture(BUTTON_UI, x + 180, y + 84,  0, 0, 64, 20, 64, 20);
        context.drawTexture(BUTTON_UI, x + 180, y + 108, 0, 0, 64, 20, 64, 20);
        context.drawTexture(BUTTON_UI, x + 180, y + 132, 0, 0, 64, 20, 64, 20);

        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.meal"), x + 200, y + 64, 0x000000, true);
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.study"), x + 198, y + 88, 0x000000, true);
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.exercise"), x + 190, y + 112, 0x000000, true);
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.play"), x + 201, y + 136, 0x000000, true);
    }
    private void drawTrainingDetailPage(DrawContext context, int x, int y) {
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.back"), x + 18, y + 28, 0xFFFFFF, true);

        switch (detailPage) {
            case MEAL -> {
                context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.meal"), x + 90, y + 40, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.food_required"), x + 130, y + 72, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.slot_meal"), x + 130, y + 92, 0xFFFFFF, true);
            }
            case STUDY, EXERCISE, PLAY -> {
                context.drawText(this.textRenderer, getDetailTitleText(), x + 90, y + 32, 0xFFFFFF, true);

                context.drawText(this.textRenderer, Text.literal("Lv1"), x + 70, y + 68, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.literal("Lv2"), x + 70, y + 98, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.literal("Lv3"), x + 70, y + 128, 0xFFFFFF, true);

                context.drawText(this.textRenderer, getRequirementText(1), x + 130, y + 68, 0xFFFFFF, true);
                context.drawText(this.textRenderer, getRequirementText(2), x + 130, y + 98, 0xFFFFFF, true);
                context.drawText(this.textRenderer, getRequirementText(3), x + 130, y + 128, 0xFFFFFF, true);
            }
        }
    }
    private Text getDetailTitleText() {
        return switch (detailPage) {
            case MEAL -> Text.translatable("screen.tree_of_yorishiro.meal");
            case STUDY -> Text.translatable("screen.tree_of_yorishiro.study");
            case EXERCISE -> Text.translatable("screen.tree_of_yorishiro.exercise");
            case PLAY -> Text.translatable("screen.tree_of_yorishiro.play");
            default -> Text.empty();
        };
    }

    private Text getRequirementText(int level) {
        return switch (detailPage) {
            case STUDY -> switch (level) {
                case 1 -> Text.translatable("item.tree_of_yorishiro.study_book");
                case 2 -> Text.translatable("item.tree_of_yorishiro.study_set");
                case 3 -> Text.translatable("item.tree_of_yorishiro.hard_study_set");
                default -> Text.empty();
            };
            case EXERCISE -> switch (level) {
                case 1 -> Text.translatable("item.tree_of_yorishiro.headband");
                case 2 -> Text.translatable("item.tree_of_yorishiro.punching_set");
                case 3 -> Text.translatable("item.tree_of_yorishiro.running_set");
                default -> Text.empty();
            };
            case PLAY -> switch (level) {
                case 1 -> Text.translatable("item.tree_of_yorishiro.ball");
                case 2 -> Text.translatable("item.tree_of_yorishiro.bubble_set");
                case 3 -> Text.translatable("item.tree_of_yorishiro.game");
                default -> Text.empty();
            };
            default -> Text.empty();
        };
    }
    private void setDetailPage(DetailPage page) {
        this.detailPage = page;

        // クライアント側 handler も即更新する
        TreeOfYorishiroScreenHandler.DetailPage handlerPage = switch (page) {
            case MAIN -> TreeOfYorishiroScreenHandler.DetailPage.MAIN;
            case MEAL -> TreeOfYorishiroScreenHandler.DetailPage.MEAL;
            case STUDY -> TreeOfYorishiroScreenHandler.DetailPage.STUDY;
            case EXERCISE -> TreeOfYorishiroScreenHandler.DetailPage.EXERCISE;
            case PLAY -> TreeOfYorishiroScreenHandler.DetailPage.PLAY;
            case ADVENTURE -> TreeOfYorishiroScreenHandler.DetailPage.ADVENTURE;
        };
        this.handler.setCurrentPage(handlerPage);

        int buttonId = switch (page) {
            case MAIN -> TreeOfYorishiroScreenHandler.BUTTON_MAIN;
            case MEAL -> TreeOfYorishiroScreenHandler.BUTTON_MEAL;
            case STUDY -> TreeOfYorishiroScreenHandler.BUTTON_STUDY;
            case EXERCISE -> TreeOfYorishiroScreenHandler.BUTTON_EXERCISE;
            case PLAY -> TreeOfYorishiroScreenHandler.BUTTON_PLAY;
            case ADVENTURE -> TreeOfYorishiroScreenHandler.BUTTON_ADVENTURE;
        };

        if (this.client != null && this.client.interactionManager != null) {
            this.client.interactionManager.clickButton(this.handler.getSyncIdForClient(), buttonId);
        }
    }
    private void drawSlotBox(DrawContext context, int x, int y) {
        context.fill(x, y, x + 18, y + 18, 0x80FFFFFF); // 薄い白
        context.fill(x, y, x + 18, y + 1, 0xFFAAAAAA);  // 上
        context.fill(x, y, x + 1, y + 18, 0xFFAAAAAA);  // 左
        context.fill(x + 17, y, x + 18, y + 18, 0xFF555555); // 右
        context.fill(x, y + 17, x + 18, y + 18, 0xFF555555); // 下
    }
    private boolean isTrainingDetailPage() {
        return detailPage == DetailPage.MEAL
                || detailPage == DetailPage.STUDY
                || detailPage == DetailPage.EXERCISE
                || detailPage == DetailPage.PLAY;
    }

    private void drawTrainingSlots(DrawContext context, int x, int y) {
        switch (detailPage) {
            case MEAL -> {
                // しょくじは1スロットだけ
                drawSlotBox(context, x + 110, y + 70);
            }
            case STUDY, EXERCISE, PLAY -> {
                // そのほかは3スロット
                drawSlotBox(context, x + 96, y + 66);   // Lv1
                drawSlotBox(context, x + 96, y + 96);   // Lv2
                drawSlotBox(context, x + 96, y + 126);  // Lv3
            }
            default -> {
            }
        }
    }

    private void drawPlayerInventorySlots(DrawContext context, int x, int y) {
        int startX = 48;
        int startY = 190;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBox(context, x + startX + col * 18, y + startY + row * 18);
            }
        }
    }
    private void sendSelectedTabColorToServer(int tab) {
        if (this.client == null || this.client.interactionManager == null) return;

        int buttonId = switch (tab) {
            case 1 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_RED;
            case 2 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_BLUE;
            case 3 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_YELLOW;
            case 4 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_PURPLE;
            case 0 -> TreeOfYorishiroScreenHandler.BUTTON_SELECT_WHITE;
            default -> -1;
        };

        if (buttonId >= 0) {
            this.client.interactionManager.clickButton(this.handler.getSyncIdForClient(), buttonId);
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
    @Nullable
    private ChibishiroEntity getPreviewEntity() {
        TreeOfYorishiroBlockEntity be = handler.getBlockEntity(client.world);
        if (be == null || client == null || client.world == null) {
            return null;
        }

        return be.getChibiEntityByColor(client.world, getSelectedColor());
    }
}