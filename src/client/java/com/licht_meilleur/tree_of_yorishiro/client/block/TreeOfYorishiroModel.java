package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class TreeOfYorishiroModel extends GeoModel<TreeOfYorishiroBlockEntity> {

    @Override
    public Identifier getModelResource(TreeOfYorishiroBlockEntity animatable) {
        return TreeofYorishiroMod.id("geo/tree_of_yorishiro.geo.json");
    }

    @Override
    public Identifier getTextureResource(TreeOfYorishiroBlockEntity animatable) {
        return TreeofYorishiroMod.id("textures/block/tree_of_yorishiro.png");
    }

    @Override
    public Identifier getAnimationResource(TreeOfYorishiroBlockEntity animatable) {
        return TreeofYorishiroMod.id("animations/tree_of_yorishiro.animation.json");
    }
}