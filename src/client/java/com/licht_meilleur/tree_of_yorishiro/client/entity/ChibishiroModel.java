package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.minecraft.resources.Identifier;

public class ChibishiroModel extends GeoModel<ChibishiroEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return TreeofYorishiroMod.id("chibishiro");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        ChibishiroColor color = renderState.getGeckolibData(ChibishiroRenderTickets.COLOR);

        if (color == null) {
            color = ChibishiroColor.WHITE;
        }

        return switch (color) {
            case RED -> TreeofYorishiroMod.id("textures/entity/red.png");
            case BLUE -> TreeofYorishiroMod.id("textures/entity/blue.png");
            case YELLOW -> TreeofYorishiroMod.id("textures/entity/yellow.png");
            case PURPLE -> TreeofYorishiroMod.id("textures/entity/purple.png");
            case WHITE -> TreeofYorishiroMod.id("textures/entity/white.png");
        };
    }

    @Override
    public Identifier getAnimationResource(ChibishiroEntity animatable) {
        return TreeofYorishiroMod.id("chibishiro");
    }
}