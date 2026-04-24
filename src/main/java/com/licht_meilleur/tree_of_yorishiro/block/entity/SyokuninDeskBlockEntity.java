package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.block.SyokuninDeskBlock;
import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import com.licht_meilleur.tree_of_yorishiro.screen.YorisyokuninTradeScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class SyokuninDeskBlockEntity extends BlockEntity implements GeoBlockEntity, ExtendedScreenHandlerFactory {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.normal");
    private static final RawAnimation WORK = RawAnimation.begin().thenLoop("animation.operation");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private final SimpleInventory inventory = new SimpleInventory(3) {
        @Override
        public int size() {
            return stacks.size();
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            return stacks.get(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            ItemStack result = Inventories.splitStack(stacks, slot, amount);
            if (!result.isEmpty()) {
                markDirty();
            }
            return result;
        }

        @Override
        public ItemStack removeStack(int slot) {
            ItemStack result = Inventories.removeStack(stacks, slot);
            markDirty();
            return result;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            stacks.set(slot, stack);
            if (stack.getCount() > getMaxCountPerStack()) {
                stack.setCount(getMaxCountPerStack());
            }
            markDirty();
        }

        @Override
        public void markDirty() {
            SyokuninDeskBlockEntity.this.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            stacks.clear();
            markDirty();
        }
    };

    private UUID syokuninUuid;

    private boolean working = false;
    private int workTicks = 0;
    private static final int MAX_WORK_TICKS = 120;

    private ItemStack pendingOutput = ItemStack.EMPTY;
    private boolean showHeldOutput = false;

    public SyokuninDeskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SYOKUNIN_DESK, pos, state);
    }

    public static void tick(net.minecraft.world.World world, BlockPos pos, BlockState state, SyokuninDeskBlockEntity be) {
        if (world.isClient) return;

        if (be.working) {
            YorisyokuninEntity syokunin = be.getSyokunin();

            // 向き完了 → workアニメ開始後にだけ進捗を進める
            if (syokunin == null || !syokunin.isWorkAnimationActive()) {
                return;
            }

            be.workTicks++;

            if (be.workTicks >= 100 && !be.showHeldOutput && !be.pendingOutput.isEmpty()) {
                be.showHeldOutput = true;
                syokunin.setHeldWorkItem(be.pendingOutput.copy());

                be.markDirty();
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }

            if (be.workTicks >= MAX_WORK_TICKS) {
                be.finishWork();
            }
        }
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    public boolean isWorking() {
        return working;
    }

    public int getWorkTicks() {
        return workTicks;
    }

    public void tryStartWork(com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef recipe) {
        if (world == null || world.isClient) return;
        if (working || recipe == null) return;

        java.util.List<ItemStack> inputs = java.util.List.of(
                inventory.getStack(0),
                inventory.getStack(1),
                inventory.getStack(2)
        );

        if (!recipe.matches(inputs)) return;

        for (int i = 0; i < 3; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                stack.decrement(1);
                if (stack.isEmpty()) {
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }

        this.pendingOutput = recipe.getOutput();
        this.working = true;
        this.workTicks = 0;
        this.showHeldOutput = false;

        YorisyokuninEntity syokunin = getSyokunin();
        if (syokunin != null) {
            syokunin.beginDeskWork(getWorkLookTarget());
            syokunin.setHeldWorkItem(ItemStack.EMPTY);
        }

        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    private void finishWork() {
        if (world == null || world.isClient) return;

        YorisyokuninEntity syokunin = getSyokunin();
        if (syokunin != null) {
            syokunin.stopDeskWork();
            syokunin.setHeldWorkItem(ItemStack.EMPTY);
        }

        if (!pendingOutput.isEmpty()) {
            Block.dropStack(world, pos.up(), pendingOutput.copy());
        }

        pendingOutput = ItemStack.EMPTY;
        working = false;
        workTicks = 0;
        showHeldOutput = false;

        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public void spawnYorisyokunin() {
        if (!(world instanceof ServerWorld sw)) return;
        if (syokuninUuid != null && sw.getEntity(syokuninUuid) instanceof YorisyokuninEntity) return;

        YorisyokuninEntity entity = ModEntities.YORISYOKUNIN.create(sw);
        if (entity == null) return;

        Direction facing = this.getCachedState().get(SyokuninDeskBlock.FACING);
        float yaw = switch (facing) {
            case SOUTH -> 180.0f;
            case WEST -> 90.0f;
            case EAST -> -90.0f;
            default -> 0.0f;
        };

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;

        entity.refreshPositionAndAngles(x, y, z, yaw, 0f);
        entity.setDeskPos(pos);

        sw.spawnEntity(entity);
        syokuninUuid = entity.getUuid();

        sw.spawnParticles(
                net.minecraft.particle.ParticleTypes.CLOUD,
                x, y + 0.3, z,
                18, 0.25, 0.25, 0.25, 0.02
        );

        markDirty();
    }

    public void discardYorisyokunin() {
        if (!(world instanceof ServerWorld sw)) return;
        if (syokuninUuid == null) return;

        Entity e = sw.getEntity(syokuninUuid);
        if (e instanceof YorisyokuninEntity syokunin) {
            syokunin.discard();
        }

        syokuninUuid = null;
        markDirty();
    }

    @Nullable
    public YorisyokuninEntity getSyokunin() {
        if (!(world instanceof ServerWorld sw)) return null;
        if (syokuninUuid == null) return null;

        Entity e = sw.getEntity(syokuninUuid);
        return e instanceof YorisyokuninEntity syokunin ? syokunin : null;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("よりしょくにん");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new YorisyokuninTradeScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "desk_controller", 0, state -> {
            state.setAndContinue(this.working ? WORK : IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        Inventories.writeNbt(nbt, stacks);

        nbt.putBoolean("Working", working);
        nbt.putInt("WorkTicks", workTicks);
        nbt.putBoolean("ShowHeldOutput", showHeldOutput);

        if (!pendingOutput.isEmpty()) {
            nbt.put("PendingOutput", pendingOutput.writeNbt(new NbtCompound()));
        }

        if (syokuninUuid != null) {
            nbt.putUuid("SyokuninUuid", syokuninUuid);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, stacks);

        working = nbt.getBoolean("Working");
        workTicks = nbt.getInt("WorkTicks");
        showHeldOutput = nbt.getBoolean("ShowHeldOutput");

        if (nbt.contains("PendingOutput")) {
            pendingOutput = ItemStack.fromNbt(nbt.getCompound("PendingOutput"));
        } else {
            pendingOutput = ItemStack.EMPTY;
        }

        if (nbt.containsUuid("SyokuninUuid")) {
            syokuninUuid = nbt.getUuid("SyokuninUuid");
        }
    }

    private static BlockPos rotateNorthOffset(BlockPos offset, Direction facing) {
        return switch (facing) {
            case NORTH -> offset;
            case EAST  -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST  -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default    -> offset;
        };
    }

    public Vec3d getWorkLookTarget() {
        Direction facing = this.getCachedState().get(SyokuninDeskBlock.FACING);

        BlockPos frontOffset = new BlockPos(0, 0, -1);
        BlockPos rotated = rotateNorthOffset(frontOffset, facing);
        BlockPos targetPos = this.pos.add(rotated);

        return new Vec3d(
                targetPos.getX() + 0.5,
                targetPos.getY() + 1.0,
                targetPos.getZ() + 0.5
        );
    }
}