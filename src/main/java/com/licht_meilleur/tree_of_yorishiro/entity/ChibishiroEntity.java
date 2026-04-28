package com.licht_meilleur.tree_of_yorishiro.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.licht_meilleur.tree_of_yorishiro.entity.ai.ChibishiroAssignedTaskGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ChibishiroEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(ChibishiroEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ANIM_STATE =
            SynchedEntityData.defineId(ChibishiroEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ANIM_TICKS =
            SynchedEntityData.defineId(ChibishiroEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<ItemStack> DISPLAY_FOOD =
            SynchedEntityData.defineId(ChibishiroEntity.class, EntityDataSerializers.ITEM_STACK);

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

    private BlockPos homeTreePos;
    private UUID homeTreeUuid;

    public ChibishiroEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, 0);
        builder.define(ANIM_STATE, ChibishiroAnimState.IDLE.ordinal());
        builder.define(ANIM_TICKS, 0);
        builder.define(DISPLAY_FOOD, ItemStack.EMPTY);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ChibishiroAssignedTaskGoal(this));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D);
    }

    public void setColor(ChibishiroColor color) {
        this.entityData.set(COLOR, color.ordinal());
    }

    public ChibishiroColor getColor() {
        return ChibishiroColor.byIndex(this.entityData.get(COLOR));
    }

    public ChibishiroAnimState getAnimState() {
        int index = this.entityData.get(ANIM_STATE);
        ChibishiroAnimState[] values = ChibishiroAnimState.values();

        if (index < 0 || index >= values.length) {
            return ChibishiroAnimState.IDLE;
        }

        return values[index];
    }

    public void setAnimState(ChibishiroAnimState state) {
        this.entityData.set(ANIM_STATE, state.ordinal());
    }

    public int getAnimTicks() {
        return this.entityData.get(ANIM_TICKS);
    }

    public void setAnimTicks(int ticks) {
        this.entityData.set(ANIM_TICKS, ticks);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && homeTreePos != null) {
            double maxDistance = 8.0D;
            Vec3 center = Vec3.atCenterOf(homeTreePos);

            if (this.position().distanceToSqr(center) > maxDistance * maxDistance) {
                this.getNavigation().moveTo(center.x, center.y, center.z, 1.0D);
            }

            if (this.position().distanceToSqr(center) > 20.0D * 20.0D) {
                float yaw = this.getYRot();
                float pitch = this.getXRot();

                this.setPos(center.x, center.y, center.z);
                this.setYRot(yaw);
                this.setXRot(pitch);
            }
        }

        int ticks = getAnimTicks();

        if (ticks > 0) {
            setAnimTicks(ticks - 1);

            if (ticks - 1 <= 0) {
                ChibishiroAnimState state = getAnimState();

                switch (state) {
                    case TRAINING1_START -> setAnimState(ChibishiroAnimState.TRAINING1_LOOP);
                    case TRAINING2_START -> setAnimState(ChibishiroAnimState.TRAINING2_LOOP);
                    case TRAINING3_START -> setAnimState(ChibishiroAnimState.TRAINING3_LOOP);

                    case STUDY1_START -> setAnimState(ChibishiroAnimState.STUDY1_LOOP);
                    case STUDY2_START -> setAnimState(ChibishiroAnimState.STUDY2_LOOP);
                    case STUDY3_START -> setAnimState(ChibishiroAnimState.STUDY3_LOOP);

                    case MEAL_START -> setAnimState(ChibishiroAnimState.MEAL_LOOP);
                    case SLEEP_START -> setAnimState(ChibishiroAnimState.SLEEP_LOOP);

                    case GAME1_START -> setAnimState(ChibishiroAnimState.GAME1_LOOP);
                    case GAME2_START -> setAnimState(ChibishiroAnimState.GAME2_LOOP);
                    case GAME3_START -> setAnimState(ChibishiroAnimState.GAME3_LOOP);

                    case PLAY1, PLAY2, PLAY3, PLAY4, PLAY5 -> setAnimState(ChibishiroAnimState.IDLE);

                    case TREASURE_START -> {
                        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(
                                    ParticleTypes.CLOUD,
                                    this.getX(),
                                    this.getY() + this.getBbHeight() * 0.5D,
                                    this.getZ(),
                                    12,
                                    0.2D,
                                    0.2D,
                                    0.2D,
                                    0.02D
                            );
                        }

                        this.remove(Entity.RemovalReason.DISCARDED);
                    }

                    default -> {
                    }
                }

                return;
            }
        }

        if (getAnimTicks() <= 0 && !isInAssignedTaskAnimation()) {
            boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 0.0025D;

            if (moving) {
                setAnimState(ChibishiroAnimState.WALK);
            } else {
                if (this.tickCount % 100 == 0) {
                    int r = this.getRandom().nextInt(8);

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

    public void startTreasureAndVanish() {
        setAnimState(ChibishiroAnimState.TREASURE_START);
        setAnimTicks(58);
    }

    public ItemStack getDisplayFoodStack() {
        return this.entityData.get(DISPLAY_FOOD);
    }

    public void setDisplayFoodStack(ItemStack stack) {
        ItemStack copy = stack == null ? ItemStack.EMPTY : stack.copy();

        if (!copy.isEmpty()) {
            copy.setCount(1);
        }

        this.entityData.set(DISPLAY_FOOD, copy);
    }

    public void setHomeTreePos(BlockPos pos) {
        this.homeTreePos = pos;
    }

    public BlockPos getHomeTreePos() {
        return homeTreePos;
    }

    public void setHomeTreeUuid(UUID uuid) {
        this.homeTreeUuid = uuid;
    }

    public UUID getHomeTreeUuid() {
        return homeTreeUuid;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
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
                 SLEEP_TASK -> true;

            default -> false;
        };
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
            case SLEEP_LOOP, SLEEP_TASK -> RawAnimation.begin().thenLoop(ANIM_SLEEP);

            case GAME1_START -> RawAnimation.begin().thenPlay(ANIM_GAME1START);
            case GAME1_LOOP -> RawAnimation.begin().thenLoop(ANIM_GAME1);
            case GAME2_START -> RawAnimation.begin().thenPlay(ANIM_GAME2START);
            case GAME2_LOOP -> RawAnimation.begin().thenLoop(ANIM_GAME2);
            case GAME3_START -> RawAnimation.begin().thenPlay(ANIM_GAME3START);
            case GAME3_LOOP -> RawAnimation.begin().thenLoop(ANIM_GAME3);

            case TREASURE_START -> RawAnimation.begin().thenPlay(ANIM_TREASURE_START);

            case MEAL_TASK -> RawAnimation.begin().thenPlay(ANIM_MEAL_START).thenLoop(ANIM_MEALING);

            case STUDY1_TASK -> RawAnimation.begin().thenPlay(ANIM_STUDY1START).thenLoop(ANIM_STUDY1);
            case STUDY2_TASK -> RawAnimation.begin().thenPlay(ANIM_STUDY2START).thenLoop(ANIM_STUDY2);
            case STUDY3_TASK -> RawAnimation.begin().thenPlay(ANIM_STUDY3START).thenLoop(ANIM_STUDY3);

            case TRAINING1_TASK -> RawAnimation.begin().thenPlay(ANIM_TRAINING1START).thenLoop(ANIM_TRAINING1);
            case TRAINING2_TASK -> RawAnimation.begin().thenPlay(ANIM_TRAINING2START).thenLoop(ANIM_TRAINING2);
            case TRAINING3_TASK -> RawAnimation.begin().thenPlay(ANIM_TRAINING3START).thenLoop(ANIM_TRAINING3);

            case GAME1_TASK -> RawAnimation.begin().thenPlay(ANIM_GAME1START).thenLoop(ANIM_GAME1);
            case GAME2_TASK -> RawAnimation.begin().thenPlay(ANIM_GAME2START).thenLoop(ANIM_GAME2);
            case GAME3_TASK -> RawAnimation.begin().thenPlay(ANIM_GAME3START).thenLoop(ANIM_GAME3);

            default -> RawAnimation.begin().thenLoop(ANIM_IDLE);
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                "controller",
                0,
                state -> {
                    state.setAnimation(getAnimationForState(getAnimState()));
                    return PlayState.CONTINUE;
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}