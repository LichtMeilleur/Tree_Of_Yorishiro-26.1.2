package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import net.minecraft.resources.Identifier;

public class SyokuninDeskModel extends GeoModel<SyokuninDeskBlockEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return TreeofYorishiroMod.id("syokunin_desk");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TreeofYorishiroMod.id("textures/block/syokunin_desk.png");
    }

    @Override
    public Identifier getAnimationResource(SyokuninDeskBlockEntity animatable) {
        return TreeofYorishiroMod.id("syokunin_desk");
    }
}