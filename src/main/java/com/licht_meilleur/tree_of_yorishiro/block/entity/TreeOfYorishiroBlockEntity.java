package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroAnimState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.inventory.SimpleInventory;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;


import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreeOfYorishiroBlockEntity extends BlockEntity implements GeoBlockEntity, ExtendedScreenHandlerFactory {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final List<TreeChibishiroData> chibis = new ArrayList<>();
    private boolean initialized = false;

    private final SimpleInventory trainingInventory = new SimpleInventory(4);
    private int summonCheckCooldown = 0;
    private java.util.UUID treeId = java.util.UUID.randomUUID();

    private ChibishiroColor selectedColor = ChibishiroColor.WHITE;

    private static final RawAnimation TREE_LOOP = RawAnimation.begin().thenLoop("animation.model.loop");
    private static final RawAnimation TREE_GROW = RawAnimation.begin().thenPlay("animation.model.grow");

    private boolean growing = false;
    private int growTicks = 0;
    private static final int GROW_ANIM_TICKS = 30;
    private final SimpleInventory adventureInventory = new SimpleInventory(9);

    private static final TagKey<Item> ADVENTURE_COMMON = TagKey.of(
            Registries.ITEM.getKey(),
            new Identifier("tree_of_yorishiro", "adventure_common")
    );

    private static final TagKey<Item> ADVENTURE_UNCOMMON = TagKey.of(
            Registries.ITEM.getKey(),
            new Identifier("tree_of_yorishiro", "adventure_uncommon")
    );

    private static final TagKey<Item> ADVENTURE_RARE = TagKey.of(
            Registries.ITEM.getKey(),
            new Identifier("tree_of_yorishiro", "adventure_rare")
    );

    private static final long TRAINING_DURATION = 5000L;          // 5時間
    private static final long TRAINING_REWARD_INTERVAL = 500L;    // 30分ごと
    private static final long ADVENTURE_DURATION = 8000L;         // 8時間


    public java.util.UUID getTreeId() {
        return treeId;
    }

    public TreeOfYorishiroBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TREE_OF_YORISHIRO, pos, state);
    }
    public SimpleInventory getTrainingInventory() {
        return trainingInventory;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initDefaultChibisIfNeeded() {
        if (initialized) return;
        if (!chibis.isEmpty()) {
            initialized = true;
            markDirty();
            return;
        }

        chibis.clear();
        chibis.add(new TreeChibishiroData(ChibishiroColor.WHITE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.RED));
        chibis.add(new TreeChibishiroData(ChibishiroColor.BLUE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.YELLOW));
        chibis.add(new TreeChibishiroData(ChibishiroColor.PURPLE));

        initialized = true;
        markDirty();
    }

    public List<TreeChibishiroData> getChibis() {
        return chibis;
    }

    public TreeChibishiroData getChibi(int index) {
        if (index < 0 || index >= chibis.size()) return null;
        return chibis.get(index);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TreeOfYorishiroBlockEntity be) {
        if (world.isClient) return;

        be.initDefaultChibisIfNeeded();
        tickGrowAnimation(be);
        tickTraining(be);
        tickAdventure(be);
        tickSleep(be);

        be.cleanupOrphanChibishiros();

        if (be.summonCheckCooldown > 0) {
            be.summonCheckCooldown--;
            return;
        }

        be.summonCheckCooldown = 40;
        be.ensureChibishiros();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "tree_controller", 0, state -> {
            if (this.growing) {
                state.setAndContinue(TREE_GROW);
                return PlayState.CONTINUE;
            }

            state.setAndContinue(TREE_LOOP);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        initialized = nbt.getBoolean("Initialized");
        chibis.clear();
        if (nbt.containsUuid("TreeId")) {
            treeId = nbt.getUuid("TreeId");
        }


        NbtList list = nbt.getList("Chibis", 10);
        for (int i = 0; i < list.size(); i++) {
            chibis.add(TreeChibishiroData.fromNbt(list.getCompound(i)));
        }
        for (int i = 0; i < trainingInventory.size(); i++) {
            if (nbt.contains("TrainSlot" + i)) {
                trainingInventory.setStack(i, net.minecraft.item.ItemStack.fromNbt(nbt.getCompound("TrainSlot" + i)));
            }
        }

        for (int i = 0; i < adventureInventory.size(); i++) {
            adventureInventory.setStack(i, ItemStack.EMPTY);
        }

        NbtList adventureInvList = nbt.getList("AdventureInventory", 10);
        for (int i = 0; i < adventureInvList.size(); i++) {
            NbtCompound stackNbt = adventureInvList.getCompound(i);
            int slot = stackNbt.getByte("Slot") & 255;
            if (slot >= 0 && slot < adventureInventory.size()) {
                adventureInventory.setStack(slot, ItemStack.fromNbt(stackNbt));
            }
        }

        String selectedColorId = nbt.getString("SelectedColor");
        this.selectedColor = switch (selectedColorId) {
            case "blue" -> ChibishiroColor.BLUE;
            case "yellow" -> ChibishiroColor.YELLOW;
            case "purple" -> ChibishiroColor.PURPLE;
            case "red" -> ChibishiroColor.RED;
            default -> ChibishiroColor.WHITE;
        };
        this.growing = nbt.getBoolean("Growing");
        this.growTicks = nbt.getInt("GrowTicks");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("Initialized", initialized);

        if (treeId != null) {
            nbt.putUuid("TreeId", treeId);
        }
        NbtList list = new NbtList();
        for (TreeChibishiroData data : chibis) {
            NbtCompound entry = new NbtCompound();
            data.writeNbt(entry);
            list.add(entry);
        }
        nbt.put("Chibis", list);

        for (int i = 0; i < trainingInventory.size(); i++) {
            NbtCompound stackNbt = new NbtCompound();
            trainingInventory.getStack(i).writeNbt(stackNbt);
            nbt.put("TrainSlot" + i, stackNbt);
        }
        nbt.putBoolean("Growing", this.growing);
        nbt.putInt("GrowTicks", this.growTicks);

        nbt.putString("SelectedColor", this.selectedColor.getId());

        NbtList adventureInvList = new NbtList();
        for (int i = 0; i < adventureInventory.size(); i++) {
            ItemStack stack = adventureInventory.getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound stackNbt = new NbtCompound();
                stackNbt.putByte("Slot", (byte) i);
                stack.writeNbt(stackNbt);
                adventureInvList.add(stackNbt);
            }
        }
        nbt.put("AdventureInventory", adventureInvList);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("screen.tree_of_yorishiro.title");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        initDefaultChibisIfNeeded();
        return new com.licht_meilleur.tree_of_yorishiro.screen.TreeOfYorishiroScreenHandler(syncId, playerInventory, this.pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }


    public void ensureChibishiros() {
        if (!(this.world instanceof net.minecraft.server.world.ServerWorld sw)) return;

        for (TreeChibishiroData data : this.chibis) {
            com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity found = null;


                if (data.isAdventuring()) {
                    continue;
                }



            if (data.getEntityUuid() != null) {
                Entity e = sw.getEntity(data.getEntityUuid());

                if (e instanceof ChibishiroEntity c && c.isAlive()) {
                    BlockPos homePos = c.getHomeTreePos();
                    UUID homeUuid = c.getHomeTreeUuid();

                    if (homePos != null && homePos.equals(this.pos)
                            && this.treeId != null && this.treeId.equals(homeUuid)) {
                        found = c;
                    } else {
                        c.discard();
                        data.setEntityUuid(null);
                    }
                } else {
                    data.setEntityUuid(null);
                }
            }

            if (found == null) {
                spawnOneChibi(sw, data);
            }
        }

        markDirty();
    }
    private void spawnOneChibi(ServerWorld sw, TreeChibishiroData data) {
        ChibishiroEntity chibi = new ChibishiroEntity(ModEntities.CHIBISHIRO, sw);

        chibi.setColor(data.getColor());
        chibi.setHomeTreePos(this.pos);
        chibi.setHomeTreeUuid(this.treeId);

        chibi.refreshPositionAndAngles(
                this.pos.getX() + 0.5 + (sw.random.nextDouble() - 0.5) * 2.0,
                this.pos.getY() + 1.0,
                this.pos.getZ() + 0.5 + (sw.random.nextDouble() - 0.5) * 2.0,
                0f,
                0f
        );

        sw.spawnEntity(chibi);
        data.setEntityUuid(chibi.getUuid());
    }
    public void discardAllChildren() {
        if (!(this.world instanceof ServerWorld sw)) return;

        for (TreeChibishiroData data : this.chibis) {
            if (data.getEntityUuid() == null) continue;

            Entity e = sw.getEntity(data.getEntityUuid());
            if (e instanceof ChibishiroEntity chibi) {
                chibi.discard();
            }
            data.setEntityUuid(null);
        }

        markDirty();
    }
    public void startTrainingFromScreen(String pageName, int slotIndex, ItemStack consumedStack) {
        if (this.world == null || this.world.isClient()) {
            return;
        }

        if (isAnyChibiAdventuring()) {
            return;
        }


        TreeChibishiroData data = getCurrentChibiData();
        if (data == null) {
            return;
        }


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

        long now = this.world.getTime();
        data.setTrainingEndTick(now + TRAINING_DURATION);
        data.setTrainingLastRewardTick(now);
        data.setSleeping(false);
        data.setSleepingSinceTick(0L);



        switch (pageName) {
            case "MEAL" -> data.setAnimState(ChibishiroAnimState.MEAL_TASK);
            case "STUDY" -> data.setAnimState(switch (level) {
                case 1 -> ChibishiroAnimState.STUDY1_TASK;
                case 2 -> ChibishiroAnimState.STUDY2_TASK;
                case 3 -> ChibishiroAnimState.STUDY3_TASK;
                default -> ChibishiroAnimState.IDLE;
            });
            case "EXERCISE" -> data.setAnimState(switch (level) {
                case 1 -> ChibishiroAnimState.TRAINING1_TASK;
                case 2 -> ChibishiroAnimState.TRAINING2_TASK;
                case 3 -> ChibishiroAnimState.TRAINING3_TASK;
                default -> ChibishiroAnimState.IDLE;
            });
            case "PLAY" -> data.setAnimState(switch (level) {
                case 1 -> ChibishiroAnimState.GAME1_TASK;
                case 2 -> ChibishiroAnimState.GAME2_TASK;
                case 3 -> ChibishiroAnimState.GAME3_TASK;
                default -> ChibishiroAnimState.IDLE;
            });
            default -> data.setAnimState(ChibishiroAnimState.IDLE);
        }


            if (data.getEntityUuid() != null && this.world instanceof ServerWorld sw) {
                Entity e = sw.getEntity(data.getEntityUuid());

                if (e instanceof ChibishiroEntity chibi) {
                    chibi.getNavigation().stop();
                    chibi.setAnimTicks(0);

                    if ("MEAL".equals(pageName)) {
                        chibi.setDisplayFoodStack(consumedStack);
                        chibi.startMealTask();
                    }

                    if ("STUDY".equals(pageName)) {
                        if (level == 1) chibi.startStudy1Task();
                        else if (level == 2) chibi.startStudy2Task();
                        else if (level == 3) chibi.startStudy3Task();
                    }

                    if ("EXERCISE".equals(pageName)) {
                        if (level == 1) chibi.startTraining1Task();
                        else if (level == 2) chibi.startTraining2Task();
                        else if (level == 3) chibi.startTraining3Task();
                    }

                    if ("PLAY".equals(pageName)) {
                        if (level == 1) chibi.startGame1Task();
                        else if (level == 2) chibi.startGame2Task();
                        else if (level == 3) chibi.startGame3Task();
                    }
                }

        }



        markDirty();
        this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }
    public TreeChibishiroData getChibiDataByColor(ChibishiroColor color) {
        for (TreeChibishiroData data : this.chibis) {
            if (data.getColor() == color) {
                return data;
            }
        }
        return null;
    }
    private TreeChibishiroData getCurrentChibiData() {
        return getChibiDataByColor(this.selectedColor);
    }


    private static void tickTraining(TreeOfYorishiroBlockEntity be) {
        if (be.world == null || be.world.isClient()) return;

        long now = be.world.getTime();
        boolean changed = false;

        for (TreeChibishiroData data : be.chibis) {
            if (!data.isTraining()) continue;
            if (data.isTrainingCompleted()) continue;

            long lastReward = data.getTrainingLastRewardTick();
            if (lastReward <= 0L) {
                data.setTrainingLastRewardTick(now);
                lastReward = now;
                changed = true;
            }

            while (now >= lastReward + TRAINING_REWARD_INTERVAL) {
                applyTrainingTickReward(data);
                lastReward += TRAINING_REWARD_INTERVAL;
                data.setTrainingLastRewardTick(lastReward);
                changed = true;
            }

            if (now >= data.getTrainingEndTick()) {
                data.setTraining(false);
                data.setTrainingCompleted(true);
                data.setAnimState(ChibishiroAnimState.IDLE);

                data.setTrainingType("");
                data.setTrainingLevel(0);
                data.setTrainingEndTick(0L);
                data.setTrainingLastRewardTick(0L);

                if (data.getEntityUuid() != null && be.world instanceof ServerWorld sw) {
                    Entity e = sw.getEntity(data.getEntityUuid());
                    if (e instanceof ChibishiroEntity chibi) {
                        chibi.setDisplayFoodStack(ItemStack.EMPTY);
                        chibi.setAnimState(ChibishiroAnimState.IDLE);
                        chibi.setAnimTicks(0);
                    }
                }

                changed = true;
            }
        }

        if (changed) {
            be.markDirty();
            be.world.updateListeners(be.pos, be.getCachedState(), be.getCachedState(), 3);
        }
    }
    public ChibishiroColor getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(ChibishiroColor selectedColor) {
        this.selectedColor = selectedColor != null ? selectedColor : ChibishiroColor.WHITE;
        markDirty();

        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }
    private ChibishiroEntity getCurrentChibiEntity() {
        if (!(this.world instanceof ServerWorld sw)) return null;

        TreeChibishiroData data = getCurrentChibiData();
        if (data == null || data.getEntityUuid() == null) return null;

        Entity e = sw.getEntity(data.getEntityUuid());
        if (e instanceof ChibishiroEntity chibi) {
            return chibi;
        }
        return null;
    }
    private static void tickGrowAnimation(TreeOfYorishiroBlockEntity be) {
        if (be.world == null || be.world.isClient()) return;

        if (be.growing) {
            be.growTicks--;

            if (be.growTicks <= 0) {
                be.growing = false;
                be.growTicks = 0;
                be.markDirty();
                be.world.updateListeners(be.pos, be.getCachedState(), be.getCachedState(), 3);
            }
        }
    }

    public void startGrowAnimation() {
        this.growing = true;
        this.growTicks = GROW_ANIM_TICKS;
        System.out.println("[TreeOfYorishiro] startGrowAnimation called");
        markDirty();

        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }
    @Nullable
    public ChibishiroEntity getChibiEntityByColor(World world, ChibishiroColor color) {
        if (!(world instanceof net.minecraft.server.world.ServerWorld sw) && !world.isClient) {
            return null;
        }

        TreeChibishiroData data = getChibiDataByColor(color);
        if (data == null || data.getEntityUuid() == null) {
            return null;
        }

        if (world instanceof net.minecraft.server.world.ServerWorld sw) {
            net.minecraft.entity.Entity e = sw.getEntity(data.getEntityUuid());
            return e instanceof ChibishiroEntity chibi ? chibi : null;
        }

        // クライアント側
        for (var e : world.getEntitiesByClass(
                ChibishiroEntity.class,
                new net.minecraft.util.math.Box(this.pos).expand(32),
                entity -> entity.getUuid().equals(data.getEntityUuid())
        )) {
            return e;
        }

        return null;
    }

    public boolean canStartAdventure() {
        if (hasAdventureRewards()) return false;

        for (TreeChibishiroData data : this.chibis) {
            if (data.isTraining()) return false;
            if (data.isAdventuring()) return false;
            if (data.getEntityUuid() == null) return false;
        }
        return true;
    }

    public void startAdventureFromScreen() {
        if (this.world == null || this.world.isClient()) return;
        if (!canStartAdventure()) return;

        long now = this.world.getTime();
        long duration = ADVENTURE_DURATION;


        for (TreeChibishiroData data : this.chibis) {
            data.setAdventuring(true);
            data.setAdventureEndTick(now + duration);
            data.setAnimState(ChibishiroAnimState.TREASURE_START);

            if (data.getEntityUuid() != null && this.world instanceof ServerWorld sw) {
                net.minecraft.entity.Entity e = sw.getEntity(data.getEntityUuid());
                if (e instanceof ChibishiroEntity chibi) {
                    chibi.startTreasureAndVanish();
                }
            }
        }

        markDirty();
        this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }
    private static void tickAdventure(TreeOfYorishiroBlockEntity be) {
        if (be.world == null || be.world.isClient()) return;

        long now = be.world.getTime();
        boolean changed = false;

        for (TreeChibishiroData data : be.chibis) {
            if (!data.isAdventuring()) continue;

            // 消えた実体のUUIDを掃除
            if (data.getEntityUuid() != null && be.world instanceof ServerWorld sw) {
                Entity existing = sw.getEntity(data.getEntityUuid());
                if (!(existing instanceof ChibishiroEntity) || !existing.isAlive()) {
                    data.setEntityUuid(null);
                    changed = true;
                }
            }

            if (now >= data.getAdventureEndTick()) {
                data.setAdventuring(false);
                data.setAdventureEndTick(0L);
                data.setAnimState(ChibishiroAnimState.IDLE);

                Random random = be.world.getRandom();

                // ★ 個体ごとの能力で抽選回数を決める
                int rewardCount = be.getAdventureRollCount(random, data);

                for (int i = 0; i < rewardCount; i++) {
                    be.addAdventureReward(be.rollAdventureReward(random, data));
                }

                // 実体がいなければ帰還再召喚
                if (data.getEntityUuid() == null && be.world instanceof ServerWorld sw) {
                    ChibishiroEntity chibi = ModEntities.CHIBISHIRO.create(sw);
                    if (chibi != null) {
                        chibi.setColor(data.getColor());
                        chibi.setHomeTreePos(be.pos);
                        chibi.setHomeTreeUuid(be.getTreeId());

                        double spawnX = be.pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                        double spawnY = be.pos.getY() + 1.0;
                        double spawnZ = be.pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;

                        chibi.refreshPositionAndAngles(spawnX, spawnY, spawnZ, 0.0F, 0.0F);
                        sw.spawnEntity(chibi);

                        data.setEntityUuid(chibi.getUuid());
                    }
                }

                changed = true;
            }
        }

        if (changed) {
            be.markDirty();
            be.world.updateListeners(be.pos, be.getCachedState(), be.getCachedState(), 3);
        }
    }
    public SimpleInventory getAdventureInventory() {
        return adventureInventory;
    }
    public boolean hasAdventureRewards() {
        for (int i = 0; i < adventureInventory.size(); i++) {
            if (!adventureInventory.getStack(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    private void addAdventureReward(ItemStack stack) {
        for (int i = 0; i < adventureInventory.size(); i++) {
            ItemStack existing = adventureInventory.getStack(i);

            if (existing.isEmpty()) {
                adventureInventory.setStack(i, stack.copy());
                return;
            }

            if (ItemStack.canCombine(existing, stack) && existing.getCount() < existing.getMaxCount()) {
                int movable = Math.min(stack.getCount(), existing.getMaxCount() - existing.getCount());
                existing.increment(movable);
                stack.decrement(movable);
                if (stack.isEmpty()) return;
            }
        }

        // 入り切らなかったら木の場所にドロップ
        if (this.world instanceof ServerWorld sw) {
            Block.dropStack(sw, this.pos.up(), stack);
        }
    }
    private ItemStack rollAdventureReward(Random random, TreeChibishiroData data) {
        int genki = data.getGenki();
        int kashikosa = data.getKashikosa();
        int chikara = data.getChikara();

        // ★ レア率補正
        int bonus = 0;
        bonus += kashikosa / 10;   // 賢さでレア率アップ
        bonus += genki / 25;       // 元気も少し影響

        int roll = random.nextInt(100) + bonus;

        Item item;
        int count;

        if (roll < 65) {
            // common
            item = getRandomItemFromTag(random, ADVENTURE_COMMON, Items.STICK);

            // 力で個数増加
            count = 1 + (chikara / 25) + random.nextInt(2);

        } else if (roll < 92) {
            // uncommon
            item = getRandomItemFromTag(random, ADVENTURE_UNCOMMON, Items.IRON_NUGGET);

            count = 1 + (chikara / 40);
            if (random.nextInt(100) < 30) {
                count += 1;
            }

        } else {
            // rare
            item = getRandomItemFromTag(random, ADVENTURE_RARE, Items.GOLD_NUGGET);

            count = 1;
            if (kashikosa >= 80 && random.nextInt(100) < 20) {
                count += 1;
            }
        }

        count = Math.max(1, Math.min(count, item.getMaxCount()));
        return new ItemStack(item, count);
    }

    public void claimAdventureRewards(ServerPlayerEntity player) {
        if (this.world == null || this.world.isClient()) return;

        for (int i = 0; i < adventureInventory.size(); i++) {
            ItemStack stack = adventureInventory.getStack(i);
            if (stack.isEmpty()) continue;

            ItemStack toGive = stack.copy();
            boolean inserted = player.getInventory().insertStack(toGive);

            if (!inserted || !toGive.isEmpty()) {
                Block.dropStack(this.world, player.getBlockPos(), toGive);
            }

            adventureInventory.setStack(i, ItemStack.EMPTY);
        }

        markDirty();
        this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }
    public boolean isAnyChibiAdventuring() {
        for (TreeChibishiroData data : this.chibis) {
            if (data.isAdventuring()) return true;
        }
        return false;
    }
    public void debugSetAllChibisHighStats() {
        initDefaultChibisIfNeeded();

        for (TreeChibishiroData data : this.chibis) {
            data.setGenki(100);
            data.setKashikosa(80);
            data.setChikara(80);
            data.setStress(0);

            data.setTraining(false);
            data.setAdventuring(false);
            data.setTrainingCompleted(false);
            data.setTrainingType("");
            data.setTrainingLevel(0);
            data.setTrainingEndTick(0L);
            data.setAdventureEndTick(0L);
            data.setAnimState(ChibishiroAnimState.IDLE);
            data.setEntityUuid(null);
        }

        if (this.world instanceof ServerWorld) {
            ensureChibishiros();
        }

        markDirty();

        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }

    private boolean debugTree = false;

    public boolean isDebugTree() {
        return debugTree;
    }

    public void setDebugTree(boolean debugTree) {
        this.debugTree = debugTree;
    }
    public void debugForceSpawnChibisNow() {
        if (!(this.world instanceof ServerWorld sw)) return;

        initDefaultChibisIfNeeded();
        ensureChibishiros();
        markDirty();
        this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }
    private Item getRandomItemFromTag(Random random, TagKey<Item> tag, Item fallback) {
        java.util.List<Item> pool = new java.util.ArrayList<>();

        for (RegistryEntry<Item> entry : Registries.ITEM.iterateEntries(tag)) {
            pool.add(entry.value());
        }

        if (pool.isEmpty()) {
            return fallback;
        }

        return pool.get(random.nextInt(pool.size()));
    }
    private int getAdventureRollCount(Random random, TreeChibishiroData data) {
        int genki = data.getGenki();
        int kashikosa = data.getKashikosa();

        int count = 1 + random.nextInt(2); // 基本 1～2回

        // 元気が高いと追加抽選
        if (genki >= 40) count += 1;
        if (genki >= 80) count += 1;

        // かしこさが高いとまれにさらに追加
        if (kashikosa >= 60 && random.nextInt(100) < 35) {
            count += 1;
        }

        return count;
    }
    private static void applyTrainingTickReward(TreeChibishiroData data) {
        int level = Math.max(1, data.getTrainingLevel());

        switch (data.getTrainingType()) {
            case "MEAL" -> {
                data.setGenki(Math.min(100, data.getGenki() + level));
                data.setStress(Math.max(0, data.getStress() - level));
            }
            case "STUDY" -> {
                data.setKashikosa(data.getKashikosa() + level);
                data.setStress(Math.min(100, data.getStress() + level));
                data.setGenki(Math.max(0, data.getGenki() - 1));
            }
            case "EXERCISE" -> {
                data.setChikara(data.getChikara() + level);
                data.setStress(Math.min(100, data.getStress() + level));
                data.setGenki(Math.max(0, data.getGenki() - level));
            }
            case "PLAY" -> {
                data.setStress(Math.max(0, data.getStress() - (1 + level)));
                data.setGenki(Math.min(100, data.getGenki() + 1));
            }
        }
    }

    private static boolean isNight(World world) {
        long dayTime = world.getTimeOfDay() % 24000L;
        return dayTime >= 13000L && dayTime < 23000L;
    }

    private static boolean isMorning(World world) {
        long dayTime = world.getTimeOfDay() % 24000L;
        return dayTime >= 0L && dayTime < 1000L;
    }

    private static void tickSleep(TreeOfYorishiroBlockEntity be) {
        if (be.world == null || be.world.isClient()) return;

        boolean changed = false;
        boolean night = isNight(be.world);
        boolean morning = isMorning(be.world);

        for (TreeChibishiroData data : be.chibis) {
            if (data.isAdventuring()) continue;
            if (data.isTraining()) continue;

            if (night) {
                if (!data.isSleeping()) {
                    data.setSleeping(true);
                    data.setSleepingSinceTick(be.world.getTimeOfDay());

                    if (data.getEntityUuid() != null && be.world instanceof ServerWorld sw) {
                        Entity e = sw.getEntity(data.getEntityUuid());
                        if (e instanceof ChibishiroEntity chibi) {
                            chibi.getNavigation().stop();
                            chibi.startSleepTask();
                        }
                    }

                    changed = true;
                }
            } else if (data.isSleeping()) {
                if (morning) {
                    data.setGenki(Math.min(100, data.getGenki() + 25));
                    data.setStress(Math.max(0, data.getStress() - 10));
                }

                data.setSleeping(false);
                data.setSleepingSinceTick(0L);

                if (data.getEntityUuid() != null && be.world instanceof ServerWorld sw) {
                    Entity e = sw.getEntity(data.getEntityUuid());
                    if (e instanceof ChibishiroEntity chibi) {
                        chibi.setAnimState(ChibishiroAnimState.IDLE);
                        chibi.setAnimTicks(0);
                    }
                }

                changed = true;
            }
        }

        if (changed) {
            be.markDirty();
            be.world.updateListeners(be.pos, be.getCachedState(), be.getCachedState(), 3);
        }
    }
    public void removeAllChibishiro() {
        if (!(this.world instanceof ServerWorld sw)) return;

        for (TreeChibishiroData data : this.chibis) {
            UUID uuid = data.getEntityUuid();
            if (uuid == null) continue;

            Entity e = sw.getEntity(uuid);
            if (e instanceof ChibishiroEntity chibi) {
                chibi.discard();
            } else if (e != null) {
                e.discard();
            }

            data.setEntityUuid(null);
            data.setTraining(false);
            data.setAdventuring(false);
            data.setTrainingCompleted(false);
            data.setTrainingType("");
            data.setTrainingLevel(0);
            data.setTrainingEndTick(0L);
            data.setTrainingLastRewardTick(0L);
            data.setAdventureEndTick(0L);
            data.setSleeping(false);
            data.setSleepingSinceTick(0L);
            data.setAnimState(ChibishiroAnimState.IDLE);
        }

        markDirty();

        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }

    public void cleanupOrphanChibishiros() {
        if (!(this.world instanceof ServerWorld sw)) return;
        if (this.treeId == null) return;

        // この樹の近くの chibishiro を見る
        var box = new net.minecraft.util.math.Box(this.pos).expand(16.0);

        List<ChibishiroEntity> nearby = sw.getEntitiesByClass(
                ChibishiroEntity.class,
                box,
                entity -> true
        );

        for (ChibishiroEntity chibi : nearby) {
            BlockPos homePos = chibi.getHomeTreePos();
            UUID homeUuid = chibi.getHomeTreeUuid();

            // この樹を親だと主張している個体だけを対象にする
            if (homePos != null && homePos.equals(this.pos)) {
                if (homeUuid == null || !this.treeId.equals(homeUuid)) {
                    chibi.discard();
                }
            }
        }

        // chibis リスト側の UUID も壊れていたら掃除
        for (TreeChibishiroData data : this.chibis) {
            UUID uuid = data.getEntityUuid();
            if (uuid == null) continue;

            Entity e = sw.getEntity(uuid);
            if (!(e instanceof ChibishiroEntity chibi) || !chibi.isAlive()) {
                data.setEntityUuid(null);
                continue;
            }

            BlockPos homePos = chibi.getHomeTreePos();
            UUID homeUuid = chibi.getHomeTreeUuid();

            // 「この樹の子であるべきUUID」だけ厳密に確認
            if (homePos == null || !homePos.equals(this.pos) || homeUuid == null || !this.treeId.equals(homeUuid)) {
                chibi.discard();
                data.setEntityUuid(null);
            }
        }

        markDirty();
    }


}