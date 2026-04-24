package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class YorisyokuninModel extends GeoModel<YorisyokuninEntity> {

    @Override
    public Identifier getModelResource(YorisyokuninEntity animatable) {
        return TreeofYorishiroMod.id("geo/yorisyokunin.geo.json");
    }

    @Override
    public Identifier getTextureResource(YorisyokuninEntity animatable) {
        return TreeofYorishiroMod.id("textures/entity/yorisyokunin.png");
    }

    @Override
    public Identifier getAnimationResource(YorisyokuninEntity animatable) {
        return TreeofYorishiroMod.id("animations/yorisyokunin.animation.json");
    }
}
