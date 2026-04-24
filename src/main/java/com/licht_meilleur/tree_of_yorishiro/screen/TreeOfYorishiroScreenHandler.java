package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeChibishiroData;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroAnimState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeOfYorishiroScreenHandler extends ScreenHandler {

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
    private final ScreenHandlerContext context;
    private DetailPage currentPage = DetailPage.MAIN;
    private final World playerWorld;



    public TreeOfYorishiroScreenHandler(int syncId, PlayerInventory inventory, BlockPos blockPos) {
        super(ModScreenHandlers.TREE_OF_YORISHIRO, syncId);
        this.blockPos = blockPos;
        this.context = ScreenHandlerContext.create(inventory.player.getWorld(), blockPos);
        this.playerWorld = inventory.player.getWorld();

        TreeOfYorishiroBlockEntity be = getBlockEntity(inventory.player.getWorld());
        Inventory inv = be != null ? be.getTrainingInventory() : new SimpleInventory(4);

        Inventory adventureInv = be != null ? be.getAdventureInventory() : new SimpleInventory(9);

        // slot0 = 食事
        this.addSlot(new Slot(inv, 0, 110, 70) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.MEAL
                        && stack.isFood();
            }

            @Override
            public boolean isEnabled() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.MEAL;
            }
        });

        // slot1 = Lv1
        this.addSlot(new Slot(inv, 1, 96, 66) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.isOf(ModItems.STUDY_BOOK);
                    case EXERCISE -> stack.isOf(ModItems.HEADBAND);
                    case PLAY -> stack.isOf(ModItems.BALL);
                    default -> false;
                };
            }

            @Override
            public boolean isEnabled() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.STUDY
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.EXERCISE
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.PLAY;
            }
        });

        // slot2 = Lv2
        this.addSlot(new Slot(inv, 2, 96, 96) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.isOf(ModItems.STUDY_SET);
                    case EXERCISE -> stack.isOf(ModItems.PUNCHING_SET);
                    case PLAY -> stack.isOf(ModItems.BUBBLE_SET);
                    default -> false;
                };
            }

            @Override
            public boolean isEnabled() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.STUDY
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.EXERCISE
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.PLAY;
            }
        });

        // slot3 = Lv3
        this.addSlot(new Slot(inv, 3, 96, 126) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (TreeOfYorishiroScreenHandler.this.isAdventureLocked()) {
                    return false;
                }

                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.isOf(ModItems.HARD_STUDY_SET);
                    case EXERCISE -> stack.isOf(ModItems.RUNNING_SET);
                    case PLAY -> stack.isOf(ModItems.GAME);
                    default -> false;
                };
            }

            @Override
            public boolean isEnabled() {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.STUDY
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.EXERCISE
                        || TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.PLAY;
            }
        });

        // 冒険成果物スロット 9個
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(adventureInv, i, 26 + i * 18, 168) {
                @Override
                public boolean canInsert(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean isEnabled() {
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
                    public boolean isEnabled() {
                        return isTrainingPage(TreeOfYorishiroScreenHandler.this.currentPage);
                    }
                });
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, startX + col * 18, startY + 58) {
                @Override
                public boolean isEnabled() {
                    return isTrainingPage(TreeOfYorishiroScreenHandler.this.currentPage);
                }
            });
        }
    }

    public TreeOfYorishiroScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, buf.readBlockPos());
    }

    public int getSyncIdForClient() {
        return this.syncId;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public TreeOfYorishiroBlockEntity getBlockEntity(World world) {
        if (world == null) return null;
        if (!(world.getBlockEntity(blockPos) instanceof TreeOfYorishiroBlockEntity be)) return null;
        return be;
    }

    public DetailPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(DetailPage page) {
        this.currentPage = page;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= BUTTON_SELECT_WHITE && id <= BUTTON_SELECT_PURPLE) {
            TreeOfYorishiroBlockEntity be = getBlockEntity(player.getWorld());
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

            TreeOfYorishiroBlockEntity be = getBlockEntity(player.getWorld());
            if (be != null) {
                if (be.isAnyChibiAdventuring()) {
                    return false;
                }

                be.startTrainingFromScreen(this.currentPage.name(), selectedSlot, consumed);
                be.markDirty();

            }

            return true;
        }
        if (id == BUTTON_START_ADVENTURE) {
            TreeOfYorishiroBlockEntity be = getBlockEntity(player.getWorld());
            if (be != null) {

                be.startAdventureFromScreen();
                be.markDirty();
            }
            return true;
        }
        if (id == BUTTON_CLAIM_ADVENTURE) {
            TreeOfYorishiroBlockEntity be = getBlockEntity(player.getWorld());
            if (be != null && player instanceof ServerPlayerEntity serverPlayer) {
                be.claimAdventureRewards(serverPlayer);
                be.markDirty();
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
    public boolean canUse(PlayerEntity player) {
        if (player.getWorld().getBlockEntity(this.blockPos) instanceof TreeOfYorishiroBlockEntity) {
            return player.squaredDistanceTo(
                    this.blockPos.getX() + 0.5,
                    this.blockPos.getY() + 0.5,
                    this.blockPos.getZ() + 0.5
            ) <= 64.0;
        }
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot == null || !slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();

        int containerSlots = 4;
        int playerInvStart = containerSlots;
        int playerInvEnd = this.slots.size();

        if (slotIndex < containerSlots) {
            if (!this.insertItem(originalStack, playerInvStart, playerInvEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;

            for (int i = 0; i < containerSlots; i++) {
                Slot target = this.slots.get(i);
                if (target.canInsert(originalStack) && !target.hasStack()) {
                    if (this.insertItem(originalStack, i, i + 1, false)) {
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
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return newStack;
    }

    private void dropTrainingItemsAtTree(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        World world = player.getWorld();

        for (int i = 0; i < 4; i++) {
            Slot slot = this.slots.get(i);
            if (!slot.hasStack()) continue;

            ItemStack stack = slot.getStack().copy();
            slot.setStack(ItemStack.EMPTY);

            ItemEntity itemEntity = new ItemEntity(
                    world,
                    this.blockPos.getX() + 0.5,
                    this.blockPos.getY() + 1.0,
                    this.blockPos.getZ() + 0.5,
                    stack
            );
            world.spawnEntity(itemEntity);
        }
    }
    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        if (!player.getWorld().isClient()) {
            dropTrainingItemsAtTree(player);
        }
    }
    public int getSelectedTrainingSlot() {
        if (currentPage == DetailPage.MEAL) {
            return this.getSlot(0).hasStack() ? 0 : -1;
        }

        if (currentPage == DetailPage.STUDY
                || currentPage == DetailPage.EXERCISE
                || currentPage == DetailPage.PLAY) {

            int found = -1;

            for (int i = 1; i <= 3; i++) {
                if (this.getSlot(i).hasStack()) {
                    if (found != -1) {
                        return -2; // 複数入っているので無効
                    }
                    found = i;
                }
            }

            return found; // 1～3 or -1
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
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        ItemStack consumed = stack.copy();
        consumed.setCount(1);

        stack.decrement(1);

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return consumed;
    }

    private boolean isAdventureLocked() {
        TreeOfYorishiroBlockEntity be = getBlockEntity(playerWorld);
        return be != null && be.isAnyChibiAdventuring();
    }


}