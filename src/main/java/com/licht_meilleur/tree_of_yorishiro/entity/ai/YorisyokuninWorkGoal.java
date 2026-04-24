package com.licht_meilleur.tree_of_yorishiro.entity.ai;

import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class YorisyokuninWorkGoal extends Goal {

    private final YorisyokuninEntity mob;

    public YorisyokuninWorkGoal(YorisyokuninEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.LOOK, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return mob.isWorking() && mob.hasWorkLookTarget();
    }

    @Override
    public boolean shouldContinue() {
        return mob.isWorking() && mob.hasWorkLookTarget();
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        mob.getNavigation().stop();

        Vec3d target = mob.getWorkLookTarget();
        if (target == null) return;

        double dx = target.x - mob.getX();
        double dz = target.z - mob.getZ();

        float targetYaw = (float)(MathHelper.atan2(dz, dx) * (180.0F / Math.PI)) - 90.0F;
        float currentYaw = mob.getYaw();
        float newYaw = MathHelper.stepUnwrappedAngleTowards(currentYaw, targetYaw, 8.0F);

        mob.setYaw(newYaw);
        mob.setHeadYaw(newYaw);
        mob.setBodyYaw(newYaw);
        mob.prevYaw = newYaw;
        mob.prevHeadYaw = newYaw;
        mob.prevBodyYaw = newYaw;

        float diff = Math.abs(MathHelper.wrapDegrees(targetYaw - newYaw));
        if (diff <= 8.0F && !mob.isWorkAnimationActive()) {
            mob.playWorkAnimation();
        }
    }

    @Override
    public void stop() {
        mob.clearWorkLookTarget();
    }
}