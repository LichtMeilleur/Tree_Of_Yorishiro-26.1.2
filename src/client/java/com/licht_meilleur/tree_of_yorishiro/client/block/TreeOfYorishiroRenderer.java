package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TreeOfYorishiroRenderer extends GeoBlockRenderer<TreeOfYorishiroBlockEntity> {

    public TreeOfYorishiroRenderer() {
        super(new TreeOfYorishiroModel());
    }
    @Override
    public boolean rendersOutsideBoundingBox(TreeOfYorishiroBlockEntity blockEntity) {
        return true;
    }
    @Override
    public int getRenderDistance() {
        return 128;
    }
}