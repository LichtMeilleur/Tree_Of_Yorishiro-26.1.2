package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.block.entity.GrowingTreeOfYorishiroBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class GrowingTreeOfYorishiroRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends GeoBlockRenderer<GrowingTreeOfYorishiroBlockEntity, R> {

    public GrowingTreeOfYorishiroRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new GrowingTreeOfYorishiroModel());
    }
}