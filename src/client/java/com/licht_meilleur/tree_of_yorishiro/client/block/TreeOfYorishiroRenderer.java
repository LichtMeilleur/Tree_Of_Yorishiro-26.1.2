package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.licht_meilleur.tree_of_yorishiro.block.TreeOfYorishiroPartBlock;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroPartBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.jetbrains.annotations.Nullable;

public class TreeOfYorishiroRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends GeoBlockRenderer<TreeOfYorishiroPartBlockEntity, R> {

    public TreeOfYorishiroRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new TreeOfYorishiroGeoModel());
    }
    @Override
    public void addRenderData(
            TreeOfYorishiroPartBlockEntity animatable,
            @Nullable Void relatedObject,
            R renderState,
            float partialTick
    ) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(
                TreeOfYorishiroRenderTickets.TREE_PART,
                animatable.getPart()
        );

    }
}