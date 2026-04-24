package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.licht_meilleur.tree_of_yorishiro.client.entity.ChibishiroModel;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformationMode;

public class ChibishiroRenderer extends GeoEntityRenderer<ChibishiroEntity> {

    public ChibishiroRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ChibishiroModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public void render(ChibishiroEntity entity,
                       float entityYaw,
                       float partialTick,
                       MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int packedLight) {

        matrices.push();

        float s = 0.65f; // 0.5f～0.8fで調整
        matrices.scale(s, s, s);

        super.render(entity, entityYaw, partialTick, matrices, vertexConsumers, packedLight);

        matrices.pop();
    }
    @Override
    public void renderRecursively(
            MatrixStack poseStack,
            ChibishiroEntity animatable,
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

        if (!"dish_locator".equals(bone.getName())) {
            return;
        }

        ItemStack stack = animatable.getDisplayFoodStack();
        if (stack.isEmpty()) {
            return;
        }

        poseStack.push();

        RenderUtils.translateMatrixToBone(poseStack, bone);
        RenderUtils.translateToPivotPoint(poseStack, bone);
        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

        poseStack.translate(0.0F, 1.8F, -0.6F);
        poseStack.scale(0.5F, 0.5F, 0.5F);

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

        //System.out.println("[Renderer] bone=" + bone.getName()
          //      + " color=" + animatable.getColor()
            //    + " stack=" + stack);
    }

}