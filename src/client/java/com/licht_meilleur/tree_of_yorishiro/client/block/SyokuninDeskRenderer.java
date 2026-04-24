package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.licht_meilleur.tree_of_yorishiro.block.SyokuninDeskBlock;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SyokuninDeskRenderer extends GeoBlockRenderer<SyokuninDeskBlockEntity> {

    public SyokuninDeskRenderer() {
        super(new SyokuninDeskModel());
    }

    @Override
    public void preRender(MatrixStack poseStack,
                          SyokuninDeskBlockEntity animatable,
                          BakedGeoModel model,
                          VertexConsumerProvider bufferSource,
                          VertexConsumer buffer,
                          boolean isReRender,
                          float partialTick,
                          int packedLight,
                          int packedOverlay,
                          float red, float green, float blue, float alpha) {

        Direction facing = animatable.getCachedState().get(SyokuninDeskBlock.FACING);

        poseStack.translate(0.5, 0.0, 0.5);

        // ここが補助ブロック配置に対して 90° 足りなかった分
        switch (facing) {
            case NORTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f));
            case EAST  -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f));
            case SOUTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f));
            case WEST  -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f));
            default -> {
            }
        }

        poseStack.translate(-0.5, 0.0, -0.5);

        super.preRender(
                poseStack, animatable, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha
        );
    }
}