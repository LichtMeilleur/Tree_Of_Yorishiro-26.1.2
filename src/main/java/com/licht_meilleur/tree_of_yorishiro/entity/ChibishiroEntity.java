package com.licht_meilleur.tree_of_yorishiro.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.licht_meilleur.tree_of_yorishiro.entity.ai.ChibishiroAssignedTaskGoal;

import java.util.UUID;

public class ChibishiroEntity extends PathAwareEntity implements GeoEntity {

    private static final TrackedData<Integer> COLOR =
            DataTracker.registerData(ChibishiroEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Integer> ANIM_STATE =
            DataTracker.registerData(ChibishiroEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Integer> ANIM_TICKS =
            DataTracker.registerData(ChibishiroEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final String ANIM_IDLE = "animation.model.idle";
    public static final String ANIM_WALK = "animation.model.walk";

    public static final String ANIM_PLAY = "animation.model.play";
    public static final String ANIM_PLAY2 = "animation.model.play2";
    public static final String ANIM_PLAY3 = "animation.model.play3";
    public static final String ANIM_PLAY4 = "animation.model.play4";
    public static final String ANIM_PLAY5 = "animation.model.play5";

    public static final String ANIM_TRAINING1START = "animation.model.training1start";
    public static final String ANIM_TRAINING1 = "animation.model.training1";
    public static final String ANIM_TRAINING2START = "animation.model.training2start";
    public static final String ANIM_TRAINING2 = "animation.model.training2";
    public static final String ANIM_TRAINING3START = "animation.model.training3start";
    public static final String ANIM_TRAINING3 = "animation.model.training3";

    public static final String ANIM_MEAL_START = "animation.model.meal_start";
    public static final String ANIM_MEALING = "animation.model.mealing";

    public static final String ANIM_STUDY1START = "animation.model.study1start";
    public static final String ANIM_STUDY1 = "animation.model.study1";
    public static final String ANIM_STUDY2START = "animation.model.study2start";
    public static final String ANIM_STUDY2 = "animation.model.study2";
    public static final String ANIM_STUDY3START = "animation.model.study3start";
    public static final String ANIM_STUDY3 = "animation.model.study3";

    public static final String ANIM_SLEEP_START = "animation.model.sleep_start";
    public static final String ANIM_SLEEP = "animation.model.sleep";

    public static final String ANIM_GAME1START = "animation.model.game1start";
    public static final String ANIM_GAME1 = "animation.model.game1";
    public static final String ANIM_GAME2START = "animation.model.game2start";
    public static final String ANIM_GAME2 = "animation.model.game2";
    public static final String ANIM_GAME3START = "animation.model.game3start";
    public static final String ANIM_GAME3 = "animation.model.game3";

    public static final String ANIM_TREASURE_START = "animation.model.treasure_start";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private net.minecraft.util.math.BlockPos homeTreePos;
    private java.util.UUID homeTreeUuid;

    public ChibishiroEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, 0);
        this.dataTracker.startTracking(ANIM_STATE, ChibishiroAnimState.IDLE.ordinal());
        this.dataTracker.startTracking(ANIM_TICKS, 0);

        this.dataTracker.startTracking(DISPLAY_FOOD, ItemStack.EMPTY);
    }



    public void setColor(ChibishiroColor color) {
        this.dataTracker.set(COLOR, color.ordinal());
    }


    public ChibishiroColor getColor() {
        return ChibishiroColor.byIndex(this.dataTracker.get(COLOR));
    }

    public ChibishiroAnimState getAnimState() {
        return ChibishiroAnimState.values()[this.dataTracker.get(ANIM_STATE)];
    }

    public void setAnimState(ChibishiroAnimState state) {
        this.dataTracker.set(ANIM_STATE, state.ordinal());
    }

    public int getAnimTicks() {
        return this.dataTracker.get(ANIM_TICKS);
    }

    public void setAnimTicks(int ticks) {
        this.dataTracker.set(ANIM_TICKS, ticks);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new ChibishiroAssignedTaskGoal(this));

        // 通常時の待機/うろうろ
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && homeTreePos != null) {
            double maxDistance = 8.0;
            net.minecraft.util.math.Vec3d center = net.minecraft.util.math.Vec3d.ofCenter(homeTreePos);

            if (this.getPos().squaredDistanceTo(center) > maxDistance * maxDistance) {
                this.getNavigation().startMovingTo(center.x, center.y, center.z, 1.0);
            }
            if (this.getPos().squaredDistanceTo(center) > 20.0 * 20.0) {
                this.refreshPositionAndAngles(center.x, center.y, center.z, this.getYaw(), this.getPitch());
            }
        }


        int ticks = getAnimTicks();
        if (ticks > 0) {
            setAnimTicks(ticks - 1);

            if (ticks - 1 <= 0) {
                ChibishiroAnimState state = getAnimState();
                switch (state) {
                    case TRAINING1_START -> {
                        setAnimState(ChibishiroAnimState.TRAINING1_LOOP);
                        return;
                    }
                    case TRAINING2_START -> {
                        setAnimState(ChibishiroAnimState.TRAINING2_LOOP);
                        return;
                    }
                    case TRAINING3_START -> {
                        setAnimState(ChibishiroAnimState.TRAINING3_LOOP);
                        return;
                    }

                    case STUDY1_START -> {
                        setAnimState(ChibishiroAnimState.STUDY1_LOOP);
                        return;
                    }
                    case STUDY2_START -> {
                        setAnimState(ChibishiroAnimState.STUDY2_LOOP);
                        return;
                    }
                    case STUDY3_START -> {
                        setAnimState(ChibishiroAnimState.STUDY3_LOOP);
                        return;
                    }

                    case MEAL_START -> {
                        setAnimState(ChibishiroAnimState.MEAL_LOOP);
                        return;
                    }
                    case SLEEP_START -> {
                        setAnimState(ChibishiroAnimState.SLEEP_LOOP);
                        return;
                    }

                    case GAME1_START -> {
                        setAnimState(ChibishiroAnimState.GAME1_LOOP);
                        return;
                    }
                    case GAME2_START -> {
                        setAnimState(ChibishiroAnimState.GAME2_LOOP);
                        return;
                    }
                    case GAME3_START -> {
                        setAnimState(ChibishiroAnimState.GAME3_LOOP);
                        return;
                    }

                    case PLAY1, PLAY2, PLAY3, PLAY4, PLAY5 -> {
                        setAnimState(ChibishiroAnimState.IDLE);
                        return;
                    }

                    case TREASURE_START -> {
                        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld sw) {
                            sw.spawnParticles(
                                    ParticleTypes.CLOUD,
                                    this.getX(), this.getBodyY(0.5), this.getZ(),
                                    12, 0.2, 0.2, 0.2, 0.02
                            );
                        }

                        this.discard(); // ←ここで消える
                        return;
                    }
                }
            }
        }

        if (getAnimTicks() <= 0 && !isInAssignedTaskAnimation()) {

            boolean moving = this.getVelocity().horizontalLengthSquared() > 0.0025;

            if (moving) {
                setAnimState(ChibishiroAnimState.WALK);
            } else {
                if (this.age % 100 == 0) {
                    Random random = this.getRandom();
                    int r = random.nextInt(8);

                    switch (r) {
                        case 0 -> {
                            setAnimState(ChibishiroAnimState.PLAY1);
                            setAnimTicks(120);
                        }
                        case 1 -> {
                            setAnimState(ChibishiroAnimState.PLAY2);
                            setAnimTicks(120);
                        }
                        case 2 -> {
                            setAnimState(ChibishiroAnimState.PLAY3);
                            setAnimTicks(120);
                        }
                        case 3 -> {
                            setAnimState(ChibishiroAnimState.PLAY4);
                            setAnimTicks(120);
                        }
                        case 4 -> {
                            setAnimState(ChibishiroAnimState.PLAY5);
                            setAnimTicks(120);
                        }
                        default -> setAnimState(ChibishiroAnimState.IDLE);
                    }
                } else if (getAnimState() == ChibishiroAnimState.WALK) {
                    setAnimState(ChibishiroAnimState.IDLE);
                }
            }
        }
    }

    public void startTraining1() {
        setAnimState(ChibishiroAnimState.TRAINING1_START);
        setAnimTicks(24);
    }

    public void startTraining2() {
        setAnimState(ChibishiroAnimState.TRAINING2_START);
        setAnimTicks(72);
    }

    public void startTraining3() {
        setAnimState(ChibishiroAnimState.TRAINING3_START);
        setAnimTicks(42);
    }

    public void startStudy1() {

        setAnimState(ChibishiroAnimState.STUDY1_START);
        setAnimTicks(24);
    }

    public void startStudy2() {
        setAnimState(ChibishiroAnimState.STUDY2_START);
        setAnimTicks(30);
    }

    public void startStudy3() {
        setAnimState(ChibishiroAnimState.STUDY3_START);
        setAnimTicks(30);
    }



    public void startMeal() {
        setAnimState(ChibishiroAnimState.MEAL_START);
        setAnimTicks(12);
    }

    public void startSleep() {
        setAnimState(ChibishiroAnimState.SLEEP_START);
        setAnimTicks(42);
    }

    public void startGame1() {
        setAnimState(ChibishiroAnimState.GAME1_START);
        setAnimTicks(12);
    }

    public void startGame2() {
        setAnimState(ChibishiroAnimState.GAME2_START);
        setAnimTicks(18);
    }

    public void startGame3() {
        setAnimState(ChibishiroAnimState.GAME3_START);
        setAnimTicks(48);
    }

    public void startTreasure() {
        setAnimState(ChibishiroAnimState.TREASURE_START);
        setAnimTicks(58);
    }

    public void startMealTask() {
        setAnimState(ChibishiroAnimState.MEAL_TASK);
        setAnimTicks(0);
    }

    public void startStudy1Task() {
        setAnimState(ChibishiroAnimState.STUDY1_TASK);
        setAnimTicks(0);
    }

    public void startStudy2Task() {
        setAnimState(ChibishiroAnimState.STUDY2_TASK);
        setAnimTicks(0);
    }

    public void startStudy3Task() {
        setAnimState(ChibishiroAnimState.STUDY3_TASK);
        setAnimTicks(0);
    }

    public void startTraining1Task() {
        setAnimState(ChibishiroAnimState.TRAINING1_TASK);
        setAnimTicks(0);
    }

    public void startTraining2Task() {
        setAnimState(ChibishiroAnimState.TRAINING2_TASK);
        setAnimTicks(0);
    }

    public void startTraining3Task() {
        setAnimState(ChibishiroAnimState.TRAINING3_TASK);
        setAnimTicks(0);
    }

    public void startGame1Task() {
        setAnimState(ChibishiroAnimState.GAME1_TASK);
        setAnimTicks(0);
    }

    public void startGame2Task() {
        setAnimState(ChibishiroAnimState.GAME2_TASK);
        setAnimTicks(0);
    }

    public void startGame3Task() {
        setAnimState(ChibishiroAnimState.GAME3_TASK);
        setAnimTicks(0);
    }

    public void startSleepTask() {
        setAnimState(ChibishiroAnimState.SLEEP_TASK);
        setAnimTicks(0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Color", this.dataTracker.get(COLOR));
        nbt.putInt("AnimState", this.dataTracker.get(ANIM_STATE));
        nbt.putInt("AnimTicks", this.dataTracker.get(ANIM_TICKS));

        if (homeTreePos != null) {
            nbt.putInt("HomeTreeX", homeTreePos.getX());
            nbt.putInt("HomeTreeY", homeTreePos.getY());
            nbt.putInt("HomeTreeZ", homeTreePos.getZ());
        }
        if (homeTreeUuid != null) {
            nbt.putUuid("HomeTreeUuid", homeTreeUuid);
        }
        if (!getDisplayFoodStack().isEmpty()) {
            nbt.put("DisplayFood", getDisplayFoodStack().writeNbt(new NbtCompound()));
        }

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(COLOR, nbt.getInt("Color"));
        this.dataTracker.set(ANIM_STATE, nbt.getInt("AnimState"));
        this.dataTracker.set(ANIM_TICKS, nbt.getInt("AnimTicks"));

        if (nbt.contains("HomeTreeX") && nbt.contains("HomeTreeY") && nbt.contains("HomeTreeZ")) {
            this.homeTreePos = new BlockPos(
                    nbt.getInt("HomeTreeX"),
                    nbt.getInt("HomeTreeY"),
                    nbt.getInt("HomeTreeZ")
            );
        }
        if (nbt.containsUuid("HomeTreeUuid")) {
            this.homeTreeUuid = nbt.getUuid("HomeTreeUuid");
        }
        if (nbt.contains("DisplayFood")) {
            setDisplayFoodStack(ItemStack.fromNbt(nbt.getCompound("DisplayFood")));
        } else {
            setDisplayFoodStack(ItemStack.EMPTY);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private RawAnimation getAnimationForState(ChibishiroAnimState state) {
        return switch (state) {
            case WALK -> RawAnimation.begin().thenLoop(ANIM_WALK);

            case PLAY1 -> RawAnimation.begin().thenLoop(ANIM_PLAY);
            case PLAY2 -> RawAnimation.begin().thenLoop(ANIM_PLAY2);
            case PLAY3 -> RawAnimation.begin().thenLoop(ANIM_PLAY3);
            case PLAY4 -> RawAnimation.begin().thenLoop(ANIM_PLAY4);
            case PLAY5 -> RawAnimation.begin().thenLoop(ANIM_PLAY5);

            case TRAINING1_START -> RawAnimation.begin().thenPlay(ANIM_TRAINING1START);
            case TRAINING1_LOOP -> RawAnimation.begin().thenLoop(ANIM_TRAINING1);
            case TRAINING2_START -> RawAnimation.begin().thenPlay(ANIM_TRAINING2START);
            case TRAINING2_LOOP -> RawAnimation.begin().thenLoop(ANIM_TRAINING2);
            case TRAINING3_START -> RawAnimation.begin().thenPlay(ANIM_TRAINING3START);
            case TRAINING3_LOOP -> RawAnimation.begin().thenLoop(ANIM_TRAINING3);

            case MEAL_START -> RawAnimation.begin().thenPlay(ANIM_MEAL_START);
            case MEAL_LOOP -> RawAnimation.begin().thenLoop(ANIM_MEALING);

            case STUDY1_START -> RawAnimation.begin().thenPlay(ANIM_STUDY1START);
            case STUDY1_LOOP -> RawAnimation.begin().thenLoop(ANIM_STUDY1);
            case STUDY2_START -> RawAnimation.begin().thenPlay(ANIM_STUDY2START);
            case STUDY2_LOOP -> RawAnimation.begin().thenLoop(ANIM_STUDY2);
            case STUDY3_START -> RawAnimation.begin().thenPlay(ANIM_STUDY3START);
            case STUDY3_LOOP -> RawAnimation.begin().thenLoop(ANIM_STUDY3);

            case SLEEP_START -> RawAnimation.begin().thenPlay(ANIM_SLEEP_START);
            case SLEEP_LOOP -> RawAnimation.begin().thenLoop(ANIM_SLEEP);

            case GAME1_START -> RawAnimation.begin().thenPlay(ANIM_GAME1START);
            case GAME1_LOOP -> RawAnimation.begin().thenLoop(ANIM_GAME1);
            case GAME2_START -> RawAnimation.begin().thenPlay(ANIM_GAME2START);
            case GAME2_LOOP -> RawAnimation.begin().thenLoop(ANIM_GAME2);
            case GAME3_START -> RawAnimation.begin().thenPlay(ANIM_GAME3START);
            case GAME3_LOOP -> RawAnimation.begin().thenLoop(ANIM_GAME3);

            case TREASURE_START -> RawAnimation.begin().thenPlay(ANIM_TREASURE_START);

            // TASK系: start → loop を1本で渡す
            case MEAL_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_MEAL_START)
                    .thenLoop(ANIM_MEALING);

            case STUDY1_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_STUDY1START)
                    .thenLoop(ANIM_STUDY1);
            case STUDY2_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_STUDY2START)
                    .thenLoop(ANIM_STUDY2);
            case STUDY3_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_STUDY3START)
                    .thenLoop(ANIM_STUDY3);

            case TRAINING1_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_TRAINING1START)
                    .thenLoop(ANIM_TRAINING1);
            case TRAINING2_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_TRAINING2START)
                    .thenLoop(ANIM_TRAINING2);
            case TRAINING3_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_TRAINING3START)
                    .thenLoop(ANIM_TRAINING3);

            case GAME1_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_GAME1START)
                    .thenLoop(ANIM_GAME1);
            case GAME2_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_GAME2START)
                    .thenLoop(ANIM_GAME2);
            case GAME3_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_GAME3START)
                    .thenLoop(ANIM_GAME3);
            case SLEEP_TASK -> RawAnimation.begin()
                    .thenPlay(ANIM_SLEEP_START)
                    .thenLoop(ANIM_SLEEP);

            default -> RawAnimation.begin().thenLoop(ANIM_IDLE);
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            ChibishiroAnimState animState = getAnimState();
            state.setAndContinue(getAnimationForState(animState));
            return PlayState.CONTINUE;
        }));
    }

    public void setHomeTreePos(net.minecraft.util.math.BlockPos pos) {
        this.homeTreePos = pos;
    }

    public net.minecraft.util.math.BlockPos getHomeTreePos() {
        return homeTreePos;
    }

    public void setHomeTreeUuid(java.util.UUID uuid) {
        this.homeTreeUuid = uuid;
    }

    public java.util.UUID getHomeTreeUuid() {
        return homeTreeUuid;
    }
    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        return false;
    }
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    public boolean isInAssignedTaskAnimation() {
        ChibishiroAnimState state = getAnimState();

        return switch (state) {
            case MEAL_START, MEAL_LOOP, MEAL_TASK,

                 STUDY1_START, STUDY1_LOOP, STUDY1_TASK,
                 STUDY2_START, STUDY2_LOOP, STUDY2_TASK,
                 STUDY3_START, STUDY3_LOOP, STUDY3_TASK,

                 TRAINING1_START, TRAINING1_LOOP, TRAINING1_TASK,
                 TRAINING2_START, TRAINING2_LOOP, TRAINING2_TASK,
                 TRAINING3_START, TRAINING3_LOOP, TRAINING3_TASK,

                 GAME1_START, GAME1_LOOP, GAME1_TASK,
                 GAME2_START, GAME2_LOOP, GAME2_TASK,
                 GAME3_START, GAME3_LOOP, GAME3_TASK,
                 SLEEP_TASK-> true;

            default -> false;
        };
    }
    //食事アイテム描画


    public ItemStack getDisplayFoodStack() {
        return this.dataTracker.get(DISPLAY_FOOD);
    }

    public void setDisplayFoodStack(ItemStack stack) {
        ItemStack copy = stack == null ? ItemStack.EMPTY : stack.copy();
        if (!copy.isEmpty()) {
            copy.setCount(1);
        }
        this.dataTracker.set(DISPLAY_FOOD, copy);
    }
    private static final TrackedData<ItemStack> DISPLAY_FOOD =
            DataTracker.registerData(ChibishiroEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);


    public void startTreasureAndVanish() {
        setAnimState(ChibishiroAnimState.TREASURE_START);
        setAnimTicks(58); // あとで調整
    }




}