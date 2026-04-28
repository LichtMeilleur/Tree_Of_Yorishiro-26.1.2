package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ChibishiroDishLayer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoRenderLayer<ChibishiroEntity, Void, R> {

    private final ItemModelResolver itemModelResolver;
    private final ItemStackRenderState itemState = new ItemStackRenderState();

    public ChibishiroDishLayer(
            GeoEntityRenderer<ChibishiroEntity, R> renderer,
            ItemModelResolver itemModelResolver
    ) {
        super(renderer);
        this.itemModelResolver = itemModelResolver;
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        R state = renderPassInfo.renderState();

        ItemStack stack = state.getGeckolibData(ChibishiroRenderTickets.DISPLAY_FOOD_STACK);
        if (stack == null || stack.isEmpty()) {
            return;
        }

        BakedGeoModel model = this.getDefaultBakedModel(state);

        for (GeoBone bone : model.topLevelBones()) {
            submitBoneRecursive(bone, renderPassInfo, renderTasks, stack);
        }
    }

    private void submitBoneRecursive(
            GeoBone bone,
            RenderPassInfo<R> renderPassInfo,
            SubmitNodeCollector renderTasks,
            ItemStack stack
    ) {
        if ("dish_locator".equals(bone.name())) {
            submitItemAtDish(renderPassInfo, renderTasks, stack);
            return;
        }

        for (GeoBone child : bone.children()) {
            submitBoneRecursive(child, renderPassInfo, renderTasks, stack);
        }
    }

    private void submitItemAtDish(
            RenderPassInfo<R> renderPassInfo,
            SubmitNodeCollector renderTasks,
            ItemStack stack
    ) {
        PoseStack poseStack = renderPassInfo.poseStack();

        poseStack.pushPose();

        poseStack.translate(0.0F, 1.8F, -0.6F);
        poseStack.scale(0.5F, 0.5F, 0.5F);

        this.itemState.clear();

        this.itemModelResolver.updateForNonLiving(
                this.itemState,
                stack,
                ItemDisplayContext.FIXED,
                null
        );

        this.itemState.submit(
                poseStack,
                renderTasks,
                renderPassInfo.packedLight(),
                renderPassInfo.packedOverlay(),
                0
        );

        poseStack.popPose();
    }
}