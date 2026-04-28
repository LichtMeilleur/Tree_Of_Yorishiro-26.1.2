package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.minecraft.resources.Identifier;

public class YorisyokuninModel extends GeoModel<YorisyokuninEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return TreeofYorishiroMod.id("yorisyokunin");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return TreeofYorishiroMod.id("textures/entity/yorisyokunin.png");
    }

    @Override
    public Identifier getAnimationResource(YorisyokuninEntity animatable) {
        return TreeofYorishiroMod.id("yorisyokunin");
    }
}