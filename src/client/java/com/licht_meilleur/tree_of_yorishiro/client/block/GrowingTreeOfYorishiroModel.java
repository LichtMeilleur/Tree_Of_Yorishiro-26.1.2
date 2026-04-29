package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.GrowingTreeOfYorishiroBlockEntity;
import net.minecraft.resources.Identifier;

public class GrowingTreeOfYorishiroModel extends GeoModel<GrowingTreeOfYorishiroBlockEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return TreeofYorishiroMod.id("tree_of_yorishiro");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TreeofYorishiroMod.id("textures/block/tree_of_yorishiro.png");
    }

    @Override
    public Identifier getAnimationResource(GrowingTreeOfYorishiroBlockEntity animatable) {
        return TreeofYorishiroMod.id("tree_of_yorishiro");
    }
}