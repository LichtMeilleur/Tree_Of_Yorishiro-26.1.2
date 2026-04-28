package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jspecify.annotations.Nullable;

public class YorisyokuninRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<YorisyokuninEntity, R> {

    public YorisyokuninRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new YorisyokuninModel());
        this.shadowRadius = 0.35F;

        this.withRenderLayer(renderer -> new YorisyokuninWorkItemLayer<>(renderer, ctx.getItemModelResolver()));
    }

    @Override
    public void addRenderData(
            YorisyokuninEntity animatable,
            @Nullable Void relatedObject,
            R renderState,
            float partialTick
    ) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(
                YorisyokuninRenderTickets.HELD_WORK_ITEM,
                animatable.getHeldWorkItem().copy()
        );
    }
}