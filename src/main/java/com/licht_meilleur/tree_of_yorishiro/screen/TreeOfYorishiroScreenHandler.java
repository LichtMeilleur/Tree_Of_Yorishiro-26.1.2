package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TreeOfYorishiroScreenHandler extends AbstractContainerMenu {

    public enum DetailPage {
        MAIN,
        MEAL,
        STUDY,
        EXERCISE,
        PLAY,
        ADVENTURE
    }

    public static final int BUTTON_MAIN = 0;
    public static final int BUTTON_MEAL = 1;
    public static final int BUTTON_STUDY = 2;
    public static final int BUTTON_EXERCISE = 3;
    public static final int BUTTON_PLAY = 4;
    public static final int BUTTON_ADVENTURE = 5;

    public static final int BUTTON_START_TRAINING = 6;

    public static final int BUTTON_SELECT_WHITE = 10;
    public static final int BUTTON_SELECT_RED = 11;
    public static final int BUTTON_SELECT_BLUE = 12;
    public static final int BUTTON_SELECT_YELLOW = 13;
    public static final int BUTTON_SELECT_PURPLE = 14;

    public static final int BUTTON_START_ADVENTURE = 30;
    public static final int BUTTON_CLAIM_ADVENTURE = 31;

    private final BlockPos blockPos;
    private final ContainerLevelAccess context;
    private DetailPage currentPage = DetailPage.MAIN;
    private final Level playerLevel;

    public TreeOfYorishiroScreenHandler(int syncId, Inventory inventory, BlockPos blockPos) {
        super(ModScreenHandlers.TREE_OF_YORISHIRO, syncId);

        this.blockPos = blockPos;
        this.context = ContainerLevelAccess.create(inventory.player.level(), blockPos);
        this.playerLevel = inventory.player.level();

        TreeOfYorishiroBlockEntity be = getBlockEntity(inventory.player.level());
        Container inv = be != null ? be.getTrainingInventory() : new SimpleContainer(4);
        Container adventureInv = be != null ? be.getAdventureInventory() : new SimpleContainer(9);

        this.addSlot(new Slot(inv, 0, 110, 70) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.MEAL
                        && stack.get(DataComponents.FOOD) != null;
            }

            @Override
            public boolean isActive() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.MEAL;
            }
        });

        this.addSlot(new Slot(inv, 1, 96, 66) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.is(ModItems.STUDY_BOOK);
                    case EXERCISE -> stack.is(ModItems.HEADBAND);
                    case PLAY -> stack.is(ModItems.BALL);
                    default -> false;
                };
            }

            @Override
            public boolean isActive() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.STUDY
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.EXERCISE
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.PLAY;
            }
        });

        this.addSlot(new Slot(inv, 2, 96, 96) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.is(ModItems.STUDY_SET);
                    case EXERCISE -> stack.is(ModItems.PUNCHING_SET);
                    case PLAY -> stack.is(ModItems.BUBBLE_SET);
                    default -> false;
                };
            }

            @Override
            public boolean isActive() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.STUDY
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.EXERCISE
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.PLAY;
            }
        });

        this.addSlot(new Slot(inv, 3, 96, 126) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.is(ModItems.HARD_STUDY_SET);
                    case EXERCISE -> stack.is(ModItems.RUNNING_SET);
                    case PLAY -> stack.is(ModItems.GAME);
                    default -> false;
                };
            }

            @Override
            public boolean isActive() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.STUDY
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.EXERCISE
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.PLAY;
            }
        });

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(adventureInv, i, 26 + i * 18, 168) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean isActive() {
                    return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.ADVENTURE;
                }
            });
        }

        int startX = 48;
        int startY = 190;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, startX + col * 18, startY + row * 18) {
                    @Override
                    public boolean isActive() {
                        return isTrainingPage(TreeOfYorishiroScreenHandler.this.currentPage);
                    }
                });
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, startX + col * 18, startY + 58) {
                @Override
                public boolean isActive() {
                    return isTrainingPage(TreeOfYorishiroScreenHandler.this.currentPage);
                }
            });
        }
    }

    public int getSyncIdForClient() {
        return this.containerId;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public TreeOfYorishiroBlockEntity getBlockEntity(Level level) {
        if (level == null) return null;
        if (!(level.getBlockEntity(blockPos) instanceof TreeOfYorishiroBlockEntity be)) return null;
        return be;
    }

    public DetailPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(DetailPage page) {
        this.currentPage = page;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= BUTTON_SELECT_WHITE && id <= BUTTON_SELECT_PURPLE) {
            TreeOfYorishiroBlockEntity be = getBlockEntity(player.level());
            if (be != null) {
                ChibishiroColor color = switch (id) {
                    case BUTTON_SELECT_RED -> ChibishiroColor.RED;
                    case BUTTON_SELECT_BLUE -> ChibishiroColor.BLUE;
                    case BUTTON_SELECT_YELLOW -> ChibishiroColor.YELLOW;
                    case BUTTON_SELECT_PURPLE -> ChibishiroColor.PURPLE;
                    case BUTTON_SELECT_WHITE -> ChibishiroColor.WHITE;
                    default -> ChibishiroColor.WHITE;
                };

                be.setSelectedColor(color);
                be.setChanged();
            }
            return true;
        }

        if (id == BUTTON_START_TRAINING) {
            if (!isTrainingPage(this.currentPage)) {
                return false;
            }

            int selectedSlot = getSelectedTrainingSlot();
            if (selectedSlot < 0) {
                return false;
            }

            ItemStack consumed = consumeOneFromSpecificSlot(selectedSlot);
            if (consumed.isEmpty()) {
                return false;
            }

            TreeOfYorishiroBlockEntity be = getBlockEntity(player.level());
            if (be != null) {
                if (be.isAnyChibiAdventuring()) {
                    return false;
                }

                be.startTrainingFromScreen(this.currentPage.name(), selectedSlot, consumed);
                be.setChanged();
            }

            return true;
        }

        if (id == BUTTON_START_ADVENTURE) {
            TreeOfYorishiroBlockEntity be = getBlockEntity(player.level());
            if (be != null) {
                be.startAdventureFromScreen();
                be.setChanged();
            }
            return true;
        }

        if (id == BUTTON_CLAIM_ADVENTURE) {
            TreeOfYorishiroBlockEntity be = getBlockEntity(player.level());
            if (be != null && player instanceof ServerPlayer serverPlayer) {
                be.claimAdventureRewards(serverPlayer);
                be.setChanged();
            }
            return true;
        }

        DetailPage oldPage = this.currentPage;

        DetailPage newPage = switch (id) {
            case BUTTON_MEAL -> DetailPage.MEAL;
            case BUTTON_STUDY -> DetailPage.STUDY;
            case BUTTON_EXERCISE -> DetailPage.EXERCISE;
            case BUTTON_PLAY -> DetailPage.PLAY;
            case BUTTON_ADVENTURE -> DetailPage.ADVENTURE;
            case BUTTON_MAIN -> DetailPage.MAIN;
            default -> this.currentPage;
        };

        if (oldPage != newPage) {
            if (isTrainingPage(oldPage) && !isTrainingPage(newPage)) {
                dropTrainingItemsAtTree(player);
            }
            this.currentPage = newPage;
        }

        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level().getBlockEntity(this.blockPos) instanceof TreeOfYorishiroBlockEntity) {
            return player.distanceToSqr(
                    this.blockPos.getX() + 0.5,
                    this.blockPos.getY() + 0.5,
                    this.blockPos.getZ() + 0.5
            ) <= 64.0;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack originalStack = slot.getItem();
        newStack = originalStack.copy();

        int containerSlots = 4;
        int playerInvStart = containerSlots;
        int playerInvEnd = this.slots.size();

        if (slotIndex < containerSlots) {
            if (!this.moveItemStackTo(originalStack, playerInvStart, playerInvEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;

            for (int i = 0; i < containerSlots; i++) {
                Slot target = this.slots.get(i);
                if (target.mayPlace(originalStack) && !target.hasItem()) {
                    if (this.moveItemStackTo(originalStack, i, i + 1, false)) {
                        moved = true;
                        break;
                    }
                }
            }

            if (!moved) {
                return ItemStack.EMPTY;
            }
        }

        if (originalStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return newStack;
    }

    private void dropTrainingItemsAtTree(Player player) {
        if (player.level().isClientSide()) return;

        Level level = player.level();

        for (int i = 0; i < 4; i++) {
            Slot slot = this.slots.get(i);
            if (!slot.hasItem()) continue;

            ItemStack stack = slot.getItem().copy();
            slot.set(ItemStack.EMPTY);

            ItemEntity itemEntity = new ItemEntity(
                    level,
                    this.blockPos.getX() + 0.5,
                    this.blockPos.getY() + 1.0,
                    this.blockPos.getZ() + 0.5,
                    stack
            );
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide()) {
            dropTrainingItemsAtTree(player);
        }
    }

    public int getSelectedTrainingSlot() {
        if (currentPage == DetailPage.MEAL) {
            return this.getSlot(0).hasItem() ? 0 : -1;
        }

        if (currentPage == DetailPage.STUDY
                || currentPage == DetailPage.EXERCISE
                || currentPage == DetailPage.PLAY) {

            int found = -1;

            for (int i = 1; i <= 3; i++) {
                if (this.getSlot(i).hasItem()) {
                    if (found != -1) {
                        return -2;
                    }
                    found = i;
                }
            }

            return found;
        }

        return -1;
    }

    public boolean canStartTraining() {
        return getSelectedTrainingSlot() >= 0;
    }

    private boolean isTrainingPage(DetailPage page) {
        return page == DetailPage.MEAL
                || page == DetailPage.STUDY
                || page == DetailPage.EXERCISE
                || page == DetailPage.PLAY;
    }

    private ItemStack consumeOneFromSpecificSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= 4) {
            return ItemStack.EMPTY;
        }

        Slot slot = this.getSlot(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack consumed = stack.copy();
        consumed.setCount(1);

        stack.shrink(1);

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return consumed;
    }

    private boolean isAdventureLocked() {
        TreeOfYorishiroBlockEntity be = getBlockEntity(playerLevel);
        return be != null && be.isAnyChibiAdventuring();
    }
}