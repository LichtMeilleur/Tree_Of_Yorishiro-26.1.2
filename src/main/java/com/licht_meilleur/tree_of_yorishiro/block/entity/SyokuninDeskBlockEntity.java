package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class SyokuninDeskBlockEntity extends BlockEntity implements GeoAnimatable {

    private boolean working = false;
    private int workTicks = 0;

    private ItemStack pendingOutput = ItemStack.EMPTY;
    private UUID syokuninUuid;

    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            SyokuninDeskBlockEntity.this.setChanged();
        }
    };

    private static final int MAX_WORK_TICKS = 120;

    public SyokuninDeskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SYOKUNIN_DESK, pos, state);
    }

    public static void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, SyokuninDeskBlockEntity be) {
        if (level.isClientSide()) return;

        if (be.working) {
            be.workTicks++;

            if (be.workTicks >= MAX_WORK_TICKS) {
                be.working = false;
                be.workTicks = 0;
                be.setChanged();
            }
        }
    }

    // =========================
    // 🔽 NBT（ValueOutput）
    // =========================

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putBoolean("Working", working);
        output.putInt("WorkTicks", workTicks);

        if (syokuninUuid != null) {
            output.putString("SyokuninUuid", syokuninUuid.toString());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.read("Working", com.mojang.serialization.Codec.BOOL)
                .ifPresent(v -> working = v);

        input.read("WorkTicks", com.mojang.serialization.Codec.INT)
                .ifPresent(v -> workTicks = v);

        input.read("SyokuninUuid", com.mojang.serialization.Codec.STRING)
                .ifPresent(s -> {
                    try {
                        syokuninUuid = UUID.fromString(s);
                    } catch (Exception ignored) {
                        syokuninUuid = null;
                    }
                });
    }

    public Container getInventory() {
        return inventory;
    }

    public boolean isWorking() {
        return working;
    }

    public int getWorkTicks() {
        return workTicks;
    }

    public void tryStartWork(YorisyokuninRecipeDef recipe) {
        if (level == null || level.isClientSide()) return;
        if (working || recipe == null) return;

        var inputs = java.util.List.of(
                inventory.getItem(0),
                inventory.getItem(1),
                inventory.getItem(2)
        );

        if (!recipe.matches(inputs)) return;

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }
            }
        }

        this.pendingOutput = recipe.getOutput();
        this.working = true;
        this.workTicks = 0;

        setChanged();
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation NORMAL =
            RawAnimation.begin().thenLoop("animation.normal");

    private static final RawAnimation OPERATION =
            RawAnimation.begin().thenLoop("animation.operation");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main", 0, state -> {
            state.setAndContinue(NORMAL);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}