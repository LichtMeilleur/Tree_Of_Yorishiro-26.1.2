package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SyokuninDeskModel extends GeoModel<SyokuninDeskBlockEntity> {
    @Override
    public Identifier getModelResource(SyokuninDeskBlockEntity animatable) {
        return TreeofYorishiroMod.id("geo/syokunin_desk.geo.json");
    }

    @Override
    public Identifier getTextureResource(SyokuninDeskBlockEntity animatable) {
        return TreeofYorishiroMod.id("textures/block/syokunin_desk.png");
    }

    @Override
    public Identifier getAnimationResource(SyokuninDeskBlockEntity animatable) {
        return TreeofYorishiroMod.id("animations/syokunin_desk.animation.json");
    }
}