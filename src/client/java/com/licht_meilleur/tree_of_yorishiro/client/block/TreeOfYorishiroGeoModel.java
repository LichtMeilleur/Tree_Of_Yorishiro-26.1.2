package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.TreeOfYorishiroPartBlock;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroPartBlockEntity;
import net.minecraft.resources.Identifier;

public class TreeOfYorishiroGeoModel extends GeoModel<TreeOfYorishiroPartBlockEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        TreeOfYorishiroPartBlock.Part part =
                state.getGeckolibData(TreeOfYorishiroRenderTickets.TREE_PART);

        if (part == null) {
            part = TreeOfYorishiroPartBlock.Part.UNDER;
        }

        return switch (part) {
            case UNDER -> TreeofYorishiroMod.id("tree_of_yorishiro_under");
            case MIDDLE -> TreeofYorishiroMod.id("tree_of_yorishiro_middle");
            case TOP -> TreeofYorishiroMod.id("tree_of_yorishiro_top");
        };
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TreeofYorishiroMod.id("textures/block/tree_of_yorishiro.png");
    }

    @Override
    public Identifier getAnimationResource(TreeOfYorishiroPartBlockEntity animatable) {
        return TreeofYorishiroMod.id("tree_of_yorishiro");
    }
}