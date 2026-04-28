package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jspecify.annotations.Nullable;

public class ChibishiroRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<ChibishiroEntity, R> {

    public ChibishiroRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ChibishiroModel());
        this.shadowRadius = 0.25F;

        this.withRenderLayer(renderer -> new ChibishiroDishLayer<>(renderer, ctx.getItemModelResolver()));
    }

    @Override
    public void addRenderData(
            ChibishiroEntity animatable,
            @Nullable Void relatedObject,
            R renderState,
            float partialTick
    ) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(
                ChibishiroRenderTickets.COLOR,
                animatable.getColor()
        );

        renderState.addGeckolibData(
                ChibishiroRenderTickets.DISPLAY_FOOD_STACK,
                animatable.getDisplayFoodStack().copy()
        );
    }
}