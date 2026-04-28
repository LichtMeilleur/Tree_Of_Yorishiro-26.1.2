package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.mojang.serialization.Codec;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreeOfYorishiroBlockEntity extends BlockEntity {

    private final List<TreeChibishiroData> chibis = new ArrayList<>();
    private boolean initialized = false;

    private final SimpleContainer trainingInventory = new SimpleContainer(4);
    private final SimpleContainer adventureInventory = new SimpleContainer(9);

    private UUID treeId = UUID.randomUUID();
    private ChibishiroColor selectedColor = ChibishiroColor.WHITE;

    private boolean growing = false;
    private int growTicks = 0;

    public TreeOfYorishiroBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TREE_OF_YORISHIRO, pos, state);
    }

    public void initDefaultChibisIfNeeded() {
        if (initialized) return;

        chibis.clear();
        chibis.add(new TreeChibishiroData(ChibishiroColor.WHITE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.RED));
        chibis.add(new TreeChibishiroData(ChibishiroColor.BLUE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.YELLOW));
        chibis.add(new TreeChibishiroData(ChibishiroColor.PURPLE));

        initialized = true;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TreeOfYorishiroBlockEntity be) {
        if (level.isClientSide()) return;

        be.initDefaultChibisIfNeeded();

        if (be.growing) {
            be.growTicks--;
            if (be.growTicks <= 0) {
                be.growing = false;
                be.growTicks = 0;
                be.setChanged();
            }
        }
    }

    public void startGrowAnimation() {
        this.growing = true;
        this.growTicks = 30;
        setChanged();
    }

    public List<TreeChibishiroData> getChibis() {
        return chibis;
    }

    public SimpleContainer getTrainingInventory() {
        return trainingInventory;
    }

    public SimpleContainer getAdventureInventory() {
        return adventureInventory;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("DataVersion", 1);
        output.putBoolean("Initialized", initialized);

        if (treeId != null) {
            output.putString("TreeId", treeId.toString());
        }

        output.putString("SelectedColor", selectedColor.getId());
        output.putBoolean("Growing", growing);
        output.putInt("GrowTicks", growTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.read("Initialized", Codec.BOOL).ifPresent(v -> initialized = v);
        input.read("GrowTicks", Codec.INT).ifPresent(v -> growTicks = v);
        input.read("Growing", Codec.BOOL).ifPresent(v -> growing = v);

        input.read("TreeId", Codec.STRING).ifPresent(s -> {
            try {
                treeId = UUID.fromString(s);
            } catch (IllegalArgumentException ignored) {
                treeId = UUID.randomUUID();
            }
        });

        input.read("SelectedColor", Codec.STRING).ifPresent(s -> {
            selectedColor = switch (s) {
                case "blue" -> ChibishiroColor.BLUE;
                case "yellow" -> ChibishiroColor.YELLOW;
                case "purple" -> ChibishiroColor.PURPLE;
                case "red" -> ChibishiroColor.RED;
                default -> ChibishiroColor.WHITE;
            };
        });
    }
    public TreeChibishiroData getChibiDataByColor(ChibishiroColor color) {
        if (color == null) return null;

        for (TreeChibishiroData data : this.chibis) {
            if (data.getColor() == color) {
                return data;
            }
        }

        return null;
    }
    public void setSelectedColor(ChibishiroColor color) {
        this.selectedColor = color != null ? color : ChibishiroColor.WHITE;
        setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isAnyChibiAdventuring() {
        for (TreeChibishiroData data : this.chibis) {
            if (data.isAdventuring()) {
                return true;
            }
        }
        return false;
    }

    public void startTrainingFromScreen(String pageName, int slotIndex, ItemStack consumedStack) {
        TreeChibishiroData data = getChibiDataByColor(this.selectedColor);
        if (data == null) return;

        int level = switch (pageName) {
            case "MEAL" -> 1;
            case "STUDY", "EXERCISE", "PLAY" -> switch (slotIndex) {
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 3;
                default -> 0;
            };
            default -> 0;
        };

        data.setTraining(true);
        data.setTrainingCompleted(false);
        data.setTrainingType(pageName);
        data.setTrainingLevel(level);
        data.setTrainingEndTick(this.level != null ? this.level.getGameTime() + 5000L : 0L);
        data.setTrainingLastRewardTick(this.level != null ? this.level.getGameTime() : 0L);
        data.setSleeping(false);
        data.setSleepingSinceTick(0L);

        setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void startAdventureFromScreen() {
        long now = this.level != null ? this.level.getGameTime() : 0L;

        for (TreeChibishiroData data : this.chibis) {
            data.setAdventuring(true);
            data.setAdventureEndTick(now + 8000L);
        }

        setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void claimAdventureRewards(ServerPlayer player) {
        for (int i = 0; i < this.adventureInventory.getContainerSize(); i++) {
            ItemStack stack = this.adventureInventory.getItem(i);
            if (stack.isEmpty()) continue;

            ItemStack copy = stack.copy();

            if (!player.getInventory().add(copy)) {
                player.drop(copy, false);
            }

            this.adventureInventory.setItem(i, ItemStack.EMPTY);
        }

        setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}