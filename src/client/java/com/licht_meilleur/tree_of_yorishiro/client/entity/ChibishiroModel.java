package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChibishiroModel extends GeoModel<ChibishiroEntity> {

    @Override
    public Identifier getModelResource(ChibishiroEntity animatable) {
        return TreeofYorishiroMod.id("geo/chibishiro.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChibishiroEntity animatable) {
        ChibishiroColor color = animatable.getColor();
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
        return TreeofYorishiroMod.id("animations/chibishiro.animation.json");
    }
}