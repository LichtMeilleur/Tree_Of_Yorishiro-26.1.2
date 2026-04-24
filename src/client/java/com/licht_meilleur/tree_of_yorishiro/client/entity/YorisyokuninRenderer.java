package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class YorisyokuninRenderer extends GeoEntityRenderer<YorisyokuninEntity> {

    public YorisyokuninRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new YorisyokuninModel());
        this.shadowRadius = 0.35f;
    }

    @Override
    public void renderRecursively(
            MatrixStack poseStack,
            YorisyokuninEntity animatable,
            GeoBone bone,
            RenderLayer renderType,
            VertexConsumerProvider bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red, float green, float blue, float alpha
    ) {
        super.renderRecursively(
                poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha
        );

        if (!"take_item_locator".equals(bone.getName())) {
            return;
        }

        ItemStack stack = animatable.getHeldWorkItem();
        if (stack.isEmpty()) {
            return;
        }

        poseStack.push();

        RenderUtils.translateMatrixToBone(poseStack, bone);
        RenderUtils.translateToPivotPoint(poseStack, bone);
        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

        // ここはあとで微調整
        poseStack.translate(0.0F, 0.0F, 0.0F);
        poseStack.scale(0.8F, 0.8F, 0.8F);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                animatable.getWorld(),
                0
        );

        poseStack.pop();
    }
}