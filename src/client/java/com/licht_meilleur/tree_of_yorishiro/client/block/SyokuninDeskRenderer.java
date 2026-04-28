package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class SyokuninDeskRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends GeoBlockRenderer<SyokuninDeskBlockEntity, R> {

    public SyokuninDeskRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new SyokuninDeskModel());
    }
}