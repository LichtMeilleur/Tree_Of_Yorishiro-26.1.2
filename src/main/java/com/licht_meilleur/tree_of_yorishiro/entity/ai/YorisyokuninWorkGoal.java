package com.licht_meilleur.tree_of_yorishiro.entity.ai;

import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class YorisyokuninWorkGoal extends Goal {

    private final YorisyokuninEntity mob;

    public YorisyokuninWorkGoal(YorisyokuninEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return mob.isWorking() && mob.hasWorkLookTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isWorking() && mob.hasWorkLookTarget();
    }

    @Override
    public void start() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        mob.getNavigation().stop();

        Vec3 target = mob.getWorkLookTarget();
        if (target == null) return;

        double dx = target.x - mob.getX();
        double dz = target.z - mob.getZ();

        float targetYaw = (float)(Mth.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;
        float currentYaw = mob.getYRot();

        float newYaw = Mth.approachDegrees(currentYaw, targetYaw, 8.0F);

        mob.setYRot(newYaw);
        mob.setYHeadRot(newYaw);
        mob.setYBodyRot(newYaw);

        float diff = Math.abs(Mth.wrapDegrees(targetYaw - newYaw));

        if (diff <= 8.0F && !mob.isWorkAnimationActive()) {
            mob.playWorkAnimation();
        }
    }

    @Override
    public void stop() {
        mob.clearWorkLookTarget();
    }
}