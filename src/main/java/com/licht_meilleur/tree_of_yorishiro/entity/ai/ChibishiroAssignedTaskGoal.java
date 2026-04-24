package com.licht_meilleur.tree_of_yorishiro.entity.ai;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeChibishiroData;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroAnimState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class ChibishiroAssignedTaskGoal extends Goal {

    private final ChibishiroEntity chibi;
    private boolean startedAnimation = false;

    public ChibishiroAssignedTaskGoal(ChibishiroEntity chibi) {
        this.chibi = chibi;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        TreeChibishiroData data = getTaskData();
        return data != null && data.isTraining();
    }

    @Override
    public boolean shouldContinue() {
        TreeChibishiroData data = getTaskData();
        return data != null && data.isTraining();
    }

    @Override
    public void start() {
        this.startedAnimation = false;
    }

    @Override
    public void stop() {
        this.startedAnimation = false;
        this.chibi.getNavigation().stop();

        if (!chibi.getWorld().isClient()) {
            chibi.setAnimState(ChibishiroAnimState.IDLE);
            chibi.setAnimTicks(0);
        }
    }

    @Override
    public void tick() {
        TreeChibishiroData data = getTaskData();
        if (data == null) return;

        BlockPos home = chibi.getHomeTreePos();
        if (home == null) return;

        double tx = home.getX() + 0.5;
        double ty = home.getY() + 1.0;
        double tz = home.getZ() + 0.5;

        double distSq = chibi.squaredDistanceTo(tx, ty, tz);

        if (distSq > 6.0D) {
            chibi.getNavigation().startMovingTo(tx, ty, tz, 1.0D);
            return;
        }

        chibi.getNavigation().stop();
        chibi.setVelocity(0.0, chibi.getVelocity().y, 0.0);
        chibi.getLookControl().lookAt(tx, ty, tz);

        if (!startedAnimation) {
            startedAnimation = true;
            startTaskAnimation(data);
        }
    }

    private void startTaskAnimation(TreeChibishiroData data) {
        String type = data.getTrainingType();
        int level = data.getTrainingLevel();

        switch (type) {
            case "MEAL" -> chibi.startMealTask();
            case "STUDY" -> {
                if (level == 1) chibi.startStudy1Task();
                else if (level == 2) chibi.startStudy2Task();
                else if (level == 3) chibi.startStudy3Task();
            }
            case "EXERCISE" -> {
                if (level == 1) chibi.startTraining1Task();
                else if (level == 2) chibi.startTraining2Task();
                else if (level == 3) chibi.startTraining3Task();
            }
            case "PLAY" -> {
                if (level == 1) chibi.startGame1Task();
                else if (level == 2) chibi.startGame2Task();
                else if (level == 3) chibi.startGame3Task();
            }
        }
    }

    private TreeChibishiroData getTaskData() {
        BlockPos home = chibi.getHomeTreePos();
        if (home == null) return null;

        if (!(chibi.getWorld().getBlockEntity(home) instanceof TreeOfYorishiroBlockEntity be)) {
            return null;
        }

        return be.getChibiDataByColor(chibi.getColor());
    }
}